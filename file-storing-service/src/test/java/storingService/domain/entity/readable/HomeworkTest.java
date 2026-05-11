package storingService.domain.entity.readable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class HomeworkTest {

  @Test
  void existExtension() {
    Homework homework = new Homework();
    homework.setId(1L);
    homework.setFileName("homework.pdf");
    assertTrue(homework.hasName());
  }

  @Test
  void noExtension() {
    Homework homework = new Homework();
    homework.setId(2L);
    homework.setFileName("");
    assertFalse(homework.hasName());
  }

  @Test
  void nullFileName() {
    Homework homework = new Homework();
    homework.setId(3L);
    homework.setFileName(null);
    assertFalse(homework.hasName());
  }

  @Test
  void testEquals() {
    Homework h1 = new Homework();
    h1.setId(1L);
    h1.setFileName("test.pdf");

    Homework h2 = new Homework();
    h2.setId(1L);
    h2.setFileName("test.pdf");

    assertEquals(h1, h2);
  }

  @Test
  void ctorWArg() {
    Homework homework = new Homework("name");
    assertEquals("name", homework.getFileName());
  }
}
