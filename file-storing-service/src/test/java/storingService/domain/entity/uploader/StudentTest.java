package storingService.domain.entity.uploader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storingService.domain.entity.readable.Homework;

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
    assertTrue(student.correctFullName());
  }

  @Test
  void testIncorrectFullName() {
    student = new Student();
    student.setId(2L);
    student.setName("");
    assertFalse(student.correctFullName());
  }

  @Test
  void ctorWArg() {
    student = new Student("Ivan");
    assertEquals("Ivan", student.getName());
  }

  @Test
  void testAddHomework() {
    student = new Student();
    student.addHomework(new Homework("name"));
    assertEquals("name", student.getHomeworks().getFirst().getFileName());
    assertEquals(student.getHomeworks().getFirst().getStudent(), student);
  }

  @Test
  void testDeleteHomework() {
    student = new Student();
    Homework homework = new Homework("name");
    student.addHomework(homework);
    student.deleteHomework(homework);
    assertEquals(0, student.getHomeworks().size());
    assertNull(homework.getStudent());

  }
}
