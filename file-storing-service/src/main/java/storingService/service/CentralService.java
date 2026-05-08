package storingService.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import storingService.domain.entity.readable.Homework;
import storingService.domain.entity.uploader.Student;

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
  public void deleteStudent(String name) {
    studentsService.deleteStudent(name);
  }

  @Transactional
  public void addHomework(String studentName, String homeworkName) {
    Student student = createStudent(studentName);
    Homework homework = homeworksService.createHomework(homeworkName);
    homework.setStudent(student);
    homeworksService.saveHomework(homework);
  }


}
