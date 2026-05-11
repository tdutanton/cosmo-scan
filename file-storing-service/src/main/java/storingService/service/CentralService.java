package storingService.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import storingService.domain.entity.readable.Homework;
import storingService.domain.entity.uploader.Student;
import storingService.dto.HomeworkResponse;
import storingService.dto.StudentResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CentralService {

  private final HomeworksService homeworksService;
  private final StudentsService studentsService;

  @Transactional
  public Student createStudent(String name) {
    return studentsService.createStudent(name);
  }

  @Transactional
  public Student safetyGetStudentFromRepo(String name) {
    return studentsService.safetyGetStudentFromRepo(name);
  }

  @Transactional
  public void deleteStudent(String name) {
    studentsService.deleteStudent(name);
  }

  public List<StudentResponse> getAllStudents() {
    return studentsService.getAllStudents();
  }

  public StudentResponse getStudentById(Long id) {
    return studentsService.getStudentById(id);
  }

  @Transactional
  public void deleteStudentById(Long id) {
    studentsService.deleteStudentById(id);
  }

  @Transactional
  public void addHomework(String studentName, String homeworkName) {
    Student student = safetyGetStudentFromRepo(studentName);
    Homework homework = homeworksService.createHomework(homeworkName);
    homework.setStudent(student);
    homeworksService.saveHomework(homework);
  }

  @Transactional
  public void saveHomework(Homework homework) {
    homeworksService.saveHomework(homework);
  }

  public List<HomeworkResponse> getAllHomeworks() {
    return homeworksService.getAllHomeworks();
  }

  public HomeworkResponse getHomeworkById(Long id) {
    return homeworksService.getHomeworkById(id);
  }

  @Transactional
  public void deleteHomeworkById(Long id) {
    homeworksService.deleteHomeworkById(id);
  }
}
