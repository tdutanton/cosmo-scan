package storingService.domain.entity.uploader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StudentTest {

  private Student student;

  @BeforeEach
  void setUp() {
    System.out.println("Запуск нового теста");
  }

  @AfterEach
  void tearDown() {
    System.out.println("Тест завершен");
  }

  @Test
  void testCorrectFullNamee() {
    student = new Student();
    student.setId(1L);
    student.setName("Ivan");
    student.setSurname("Ivanov");
    assertTrue(student.correctFullName());
  }

  @Test
  void testIncorrectFullName() {
    student = new Student();
    student.setId(2L);
    student.setName("Ivan");
    assertFalse(student.correctFullName());
  }

}
