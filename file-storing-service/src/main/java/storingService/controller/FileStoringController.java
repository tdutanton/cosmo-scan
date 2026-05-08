package storingService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import storingService.domain.entity.readable.Homework;
import storingService.service.CentralService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileStoringController {

  private final CentralService centralService;

  @PostMapping("/students")
  public ResponseEntity<Void> addStudent(@RequestBody String name) {
    centralService.createStudent(name);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/files/upload")
  public ResponseEntity<FileUploadResponse> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("studentName") String studentName) {

    String fileName = fileStorageService.store(file);
    Homework homework = homeworksService.createHomework(fileName, studentName);

    // 🔥 Здесь должен быть вызов Analysis Service!
    analysisServiceClient.triggerAnalysis(homework.getId(), fileName);

    return ResponseEntity.ok(new FileUploadResponse(homework.getId()));
  }

}
