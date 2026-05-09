package storingService.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import storingService.domain.entity.uploader.Student;
import storingService.dto.StudentResponse;
import storingService.repository.StudentsRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentsServiceTest {

  @Mock
  private StudentsRepository studentsRepository;

  private StudentsService studentsService;

  @BeforeEach
  void setUp() {
    studentsService = new StudentsService(studentsRepository);
  }

  @Test
  void createStudent_success() {
    when(studentsRepository.findByName("Иванов Иван Иванович")).thenReturn(Optional.empty());
    when(studentsRepository.save(any(Student.class))).thenAnswer(invocation -> {
      Student s = invocation.getArgument(0);
      s.setId(1L);
      return s;
    });

    Student result = studentsService.createStudent("Иванов Иван Иванович");

    assertNotNull(result);
    assertEquals("Иванов Иван Иванович", result.getName());
    verify(studentsRepository).save(any(Student.class));
  }

  @Test
  void createStudent_duplicateName_throwsException() {
    when(studentsRepository.findByName("Иванов Иван Иванович")).thenReturn(Optional.of(new Student("Иванов Иван Иванович")));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> studentsService.createStudent("Иванов Иван Иванович"));

    assertTrue(ex.getMessage().contains("уже есть в списках"));
  }

  @Test
  void createStudent_invalidName_throwsException() {
    when(studentsRepository.findByName("")).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> studentsService.createStudent(""));

    assertTrue(ex.getMessage().contains("Некорректное имя студента"));
  }

  @Test
  void safetyGetStudentFromRepo_existingStudent() {
    Student existing = new Student("Иванов Иван Иванович");
    existing.setId(1L);
    when(studentsRepository.findByName("Иванов Иван Иванович")).thenReturn(Optional.of(existing));

    Student result = studentsService.safetyGetStudentFromRepo("Иванов Иван Иванович");

    assertEquals(1L, result.getId());
    verify(studentsRepository, never()).save(any());
  }

  @Test
  void safetyGetStudentFromRepo_newStudent() {
    when(studentsRepository.findByName("Новый Студент")).thenReturn(Optional.empty());
    when(studentsRepository.save(any(Student.class))).thenAnswer(invocation -> {
      Student s = invocation.getArgument(0);
      s.setId(1L);
      return s;
    });

    Student result = studentsService.safetyGetStudentFromRepo("Новый Студент");

    assertNotNull(result);
    verify(studentsRepository).save(any(Student.class));
  }

  @Test
  void deleteStudent_success() {
    Student student = new Student("Иванов Иван Иванович");
    student.setId(1L);
    when(studentsRepository.findByName("Иванов Иван Иванович")).thenReturn(Optional.of(student));
    doNothing().when(studentsRepository).delete(student);

    assertDoesNotThrow(() -> studentsService.deleteStudent("Иванов Иван Иванович"));
    verify(studentsRepository).delete(student);
  }

  @Test
  void deleteStudent_notFound() {
    when(studentsRepository.findByName("Неизвестный")).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> studentsService.deleteStudent("Неизвестный"));

    assertTrue(ex.getMessage().contains("Студент не найден"));
  }

  @Test
  void getAllStudents_success() {
    List<Student> students = List.of(
        createStudent(1L, "Студент 1"),
        createStudent(2L, "Студент 2")
    );
    when(studentsRepository.findAll()).thenReturn(students);

    List<StudentResponse> result = studentsService.getAllStudents();

    assertEquals(2, result.size());
    assertEquals("Студент 1", result.get(0).name());
  }

  @Test
  void getAllStudents_empty() {
    when(studentsRepository.findAll()).thenReturn(List.of());

    List<StudentResponse> result = studentsService.getAllStudents();

    assertTrue(result.isEmpty());
  }

  @Test
  void getStudentById_success() {
    Student student = createStudent(1L, "Иванов Иван Иванович");
    when(studentsRepository.findById(1L)).thenReturn(Optional.of(student));

    StudentResponse result = studentsService.getStudentById(1L);

    assertNotNull(result);
    assertEquals("Иванов Иван Иванович", result.name());
  }

  @Test
  void getStudentById_notFound() {
    when(studentsRepository.findById(99L)).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> studentsService.getStudentById(99L));

    assertTrue(ex.getMessage().contains("не найден по id"));
  }

  @Test
  void deleteStudentById_success() {
    Student student = createStudent(1L, "Иванов Иван Иванович");
    when(studentsRepository.findById(1L)).thenReturn(Optional.of(student));
    doNothing().when(studentsRepository).delete(student);

    assertDoesNotThrow(() -> studentsService.deleteStudentById(1L));
    verify(studentsRepository).delete(student);
  }

  @Test
  void deleteStudentById_notFound() {
    when(studentsRepository.findById(99L)).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> studentsService.deleteStudentById(99L));

    assertTrue(ex.getMessage().contains("не найден по id"));
  }

  private Student createStudent(Long id, String name) {
    Student student = new Student(name);
    student.setId(id);
    return student;
  }
}
