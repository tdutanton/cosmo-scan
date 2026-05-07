package storingService.domain.entity.readable;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HomeworkTest {

  private Homework homework;

  @BeforeEach
  void setUp() {
    System.out.println("Запуск нового теста");
  }

  @AfterEach
  void tearDown() {
    System.out.println("Тест завершен");
  }

  @Test
  void existExtension() {
    homework = new Homework();
    homework.setId(1L);
    homework.setFileName("aaa.exe");
    assertTrue(homework.hasExtension());
  }

  @Test
  void noExtension() {
    homework = new Homework();
    homework.setId(2L);
    assertFalse(homework.hasExtension());
  }

}
