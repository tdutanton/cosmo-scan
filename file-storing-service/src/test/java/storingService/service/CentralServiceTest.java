package storingService.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import storingService.domain.entity.readable.Homework;
import storingService.domain.entity.uploader.Student;
import storingService.dto.HomeworkResponse;
import storingService.dto.StudentResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CentralServiceTest {

  @Mock
  private HomeworksService homeworksService;

  @Mock
  private StudentsService studentsService;

  private CentralService centralService;

  @BeforeEach
  void setUp() {
    centralService = new CentralService(homeworksService, studentsService);
  }

  @Test
  void createStudent_delegatesToStudentsService() {
    Student student = new Student("Иванов Иван Иванович");
    student.setId(1L);
    when(studentsService.createStudent("Иванов Иван Иванович")).thenReturn(student);

    Student result = centralService.createStudent("Иванов Иван Иванович");

    assertNotNull(result);
    assertEquals("Иванов Иван Иванович", result.getName());
    verify(studentsService).createStudent("Иванов Иван Иванович");
  }

  @Test
  void safetyGetStudentFromRepo_delegatesToStudentsService() {
    Student student = new Student("Иванов Иван Иванович");
    student.setId(1L);
    when(studentsService.safetyGetStudentFromRepo("Иванов Иван Иванович")).thenReturn(student);

    Student result = centralService.safetyGetStudentFromRepo("Иванов Иван Иванович");

    assertNotNull(result);
    verify(studentsService).safetyGetStudentFromRepo("Иванов Иван Иванович");
  }

  @Test
  void deleteStudent_delegatesToStudentsService() {
    doNothing().when(studentsService).deleteStudent("Иванов Иван Иванович");

    assertDoesNotThrow(() -> centralService.deleteStudent("Иванов Иван Иванович"));
    verify(studentsService).deleteStudent("Иванов Иван Иванович");
  }

  @Test
  void getAllStudents_delegatesToStudentsService() {
    List<StudentResponse> students = List.of(
        new StudentResponse(1L, "Студент 1"),
        new StudentResponse(2L, "Студент 2")
    );
    when(studentsService.getAllStudents()).thenReturn(students);

    List<StudentResponse> result = centralService.getAllStudents();

    assertEquals(2, result.size());
    verify(studentsService).getAllStudents();
  }

  @Test
  void getStudentById_delegatesToStudentsService() {
    StudentResponse student = new StudentResponse(1L, "Иванов Иван Иванович");
    when(studentsService.getStudentById(1L)).thenReturn(student);

    StudentResponse result = centralService.getStudentById(1L);

    assertNotNull(result);
    assertEquals("Иванов Иван Иванович", result.name());
    verify(studentsService).getStudentById(1L);
  }

  @Test
  void deleteStudentById_delegatesToStudentsService() {
    doNothing().when(studentsService).deleteStudentById(1L);

    assertDoesNotThrow(() -> centralService.deleteStudentById(1L));
    verify(studentsService).deleteStudentById(1L);
  }

  @Test
  void addHomework_success() {
    Student student = new Student("Иванов Иван Иванович");
    student.setId(1L);
    when(studentsService.safetyGetStudentFromRepo("Иванов Иван Иванович")).thenReturn(student);

    Homework homework = new Homework("Домашняя работа 1");
    homework.setId(1L);
    when(homeworksService.createHomework("Домашняя работа 1")).thenReturn(homework);
    doNothing().when(homeworksService).saveHomework(any(Homework.class));

    assertDoesNotThrow(() -> centralService.addHomework("Иванов Иван Иванович", "Домашняя работа 1"));

    verify(homeworksService).createHomework("Домашняя работа 1");
    verify(homeworksService).saveHomework(any(Homework.class));
    assertEquals(student, homework.getStudent());
  }

  @Test
  void saveHomework_delegatesToHomeworksService() {
    Homework homework = new Homework("Новая работа");
    homework.setId(1L);
    doNothing().when(homeworksService).saveHomework(homework);

    assertDoesNotThrow(() -> centralService.saveHomework(homework));
    verify(homeworksService).saveHomework(homework);
  }

  @Test
  void getAllHomeworks_delegatesToHomeworksService() {
    List<HomeworkResponse> homeworks = List.of(
        new HomeworkResponse(1L, "Работа 1", null, null),
        new HomeworkResponse(2L, "Работа 2", null, null)
    );
    when(homeworksService.getAllHomeworks()).thenReturn(homeworks);

    List<HomeworkResponse> result = centralService.getAllHomeworks();

    assertEquals(2, result.size());
    verify(homeworksService).getAllHomeworks();
  }

  @Test
  void getHomeworkById_delegatesToHomeworksService() {
    HomeworkResponse homework = new HomeworkResponse(1L, "Домашняя работа 1", null, null);
    when(homeworksService.getHomeworkById(1L)).thenReturn(homework);

    HomeworkResponse result = centralService.getHomeworkById(1L);

    assertNotNull(result);
    assertEquals("Домашняя работа 1", result.fileName());
    verify(homeworksService).getHomeworkById(1L);
  }

  @Test
  void deleteHomeworkById_delegatesToHomeworksService() {
    doNothing().when(homeworksService).deleteHomeworkById(1L);

    assertDoesNotThrow(() -> centralService.deleteHomeworkById(1L));
    verify(homeworksService).deleteHomeworkById(1L);
  }
}
