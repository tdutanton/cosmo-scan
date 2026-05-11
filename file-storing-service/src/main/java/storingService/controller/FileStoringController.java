package storingService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import storingService.client.AnalysisServiceClient;
import storingService.domain.entity.readable.Homework;
import storingService.domain.entity.uploader.Student;
import storingService.dto.FileUploadResponse;
import storingService.dto.HomeworkResponse;
import storingService.dto.StudentResponse;
import storingService.service.CentralService;
import storingService.service.FileStorageService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileStoringController {

  private final CentralService centralService;
  private final FileStorageService fileStorageService;
  private final AnalysisServiceClient analysisServiceClient;

  @PostMapping("/students")
  public ResponseEntity<StudentResponse> addStudent(@RequestBody String name) {
    Student student = centralService.createStudent(name);
    return ResponseEntity.ok(StudentResponse.from(student));
  }

  @GetMapping("/students")
  public ResponseEntity<List<StudentResponse>> getAllStudents() {
    List<StudentResponse> students = centralService.getAllStudents();
    return ResponseEntity.ok(students);
  }

  @GetMapping("/students/{id}")
  public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
    StudentResponse student = centralService.getStudentById(id);
    return ResponseEntity.ok(student);
  }

  @DeleteMapping("/students/{id}")
  public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
    centralService.deleteStudentById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/files/upload")
  public ResponseEntity<FileUploadResponse> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("studentName") String studentName) {

    String fileName = fileStorageService.store(file);
    Student student = centralService.safetyGetStudentFromRepo(studentName);

    Homework homework = new Homework(fileName);
    homework.setStudent(student);
    centralService.saveHomework(homework);

    analysisServiceClient.triggerAnalysis(homework.getId(), fileName);

    return ResponseEntity.ok(new FileUploadResponse(homework.getId(), fileName));
  }

  @GetMapping("/files")
  public ResponseEntity<List<HomeworkResponse>> getAllHomeworks() {
    List<HomeworkResponse> homeworks = centralService.getAllHomeworks();
    return ResponseEntity.ok(homeworks);
  }

  @GetMapping("/files/{id}")
  public ResponseEntity<HomeworkResponse> getHomework(@PathVariable Long id) {
    HomeworkResponse homework = centralService.getHomeworkById(id);
    return ResponseEntity.ok(homework);
  }

  @GetMapping("/files/{id}/download")
  public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
    HomeworkResponse homework = centralService.getHomeworkById(id);
    byte[] content = fileStorageService.loadFile(homework.fileName());
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=\"" + homework.fileName() + "\"")
        .body(content);
  }

  @DeleteMapping("/files/{id}")
  public ResponseEntity<Void> deleteHomework(@PathVariable Long id) {
    HomeworkResponse homework = centralService.getHomeworkById(id);
    fileStorageService.deleteFile(homework.fileName());
    centralService.deleteHomeworkById(id);
    return ResponseEntity.noContent().build();
  }
}
