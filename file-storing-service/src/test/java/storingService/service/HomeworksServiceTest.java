package storingService.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import storingService.domain.entity.readable.Homework;
import storingService.dto.HomeworkResponse;
import storingService.repository.FilesRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeworksServiceTest {

  @Mock
  private FilesRepository filesRepository;

  private HomeworksService homeworksService;

  @BeforeEach
  void setUp() {
    homeworksService = new HomeworksService(filesRepository);
  }

  @Test
  void createHomework_success() {
    when(filesRepository.findByFileName("Домашняя работа 1")).thenReturn(Optional.empty());

    Homework result = homeworksService.createHomework("Домашняя работа 1");

    assertNotNull(result);
    assertEquals("Домашняя работа 1", result.getFileName());
  }

  @Test
  void createHomework_emptyName_throwsException() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> homeworksService.createHomework(""));

    assertTrue(ex.getMessage().contains("Некорректный объект"));
  }

  @Test
  void createHomework_duplicateName_throwsException() {
    Homework existing = new Homework("Домашняя работа 1");
    existing.setId(1L);
    when(filesRepository.findByFileName("Домашняя работа 1")).thenReturn(Optional.of(existing));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> homeworksService.createHomework("Домашняя работа 1"));

    assertTrue(ex.getMessage().contains("Некорректный объект"));
  }

  @Test
  void deleteHomework_success() {
    Homework homework = new Homework("Домашняя работа 1");
    homework.setId(1L);
    when(filesRepository.findByFileName("Домашняя работа 1")).thenReturn(Optional.of(homework));
    doNothing().when(filesRepository).delete(homework);

    assertDoesNotThrow(() -> homeworksService.deleteHomework("Домашняя работа 1"));
    verify(filesRepository).delete(homework);
  }

  @Test
  void deleteHomework_notFound() {
    when(filesRepository.findByFileName("Неизвестная работа")).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> homeworksService.deleteHomework("Неизвестная работа"));

    assertTrue(ex.getMessage().contains("Не найден файл"));
  }

  @Test
  void saveHomework_success() {
    Homework homework = new Homework("Новая работа");
    when(filesRepository.save(any(Homework.class))).thenAnswer(invocation -> {
      Homework h = invocation.getArgument(0);
      h.setId(1L);
      return h;
    });

    assertDoesNotThrow(() -> homeworksService.saveHomework(homework));
    verify(filesRepository).save(homework);
  }

  @Test
  void getAllHomeworks_success() {
    List<Homework> homeworks = List.of(
        createHomework(1L, "Работа 1"),
        createHomework(2L, "Работа 2")
    );
    when(filesRepository.findAll()).thenReturn(homeworks);

    List<HomeworkResponse> result = homeworksService.getAllHomeworks();

    assertEquals(2, result.size());
    assertEquals("Работа 1", result.get(0).fileName());
  }

  @Test
  void getAllHomeworks_empty() {
    when(filesRepository.findAll()).thenReturn(List.of());

    List<HomeworkResponse> result = homeworksService.getAllHomeworks();

    assertTrue(result.isEmpty());
  }

  @Test
  void getHomeworkById_success() {
    Homework homework = createHomework(1L, "Домашняя работа 1");
    when(filesRepository.findById(1L)).thenReturn(Optional.of(homework));

    HomeworkResponse result = homeworksService.getHomeworkById(1L);

    assertNotNull(result);
    assertEquals("Домашняя работа 1", result.fileName());
  }

  @Test
  void getHomeworkById_notFound() {
    when(filesRepository.findById(99L)).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> homeworksService.getHomeworkById(99L));

    assertTrue(ex.getMessage().contains("Работа не найдена по id"));
  }

  @Test
  void deleteHomeworkById_success() {
    Homework homework = createHomework(1L, "Домашняя работа 1");
    when(filesRepository.findById(1L)).thenReturn(Optional.of(homework));
    doNothing().when(filesRepository).delete(homework);

    assertDoesNotThrow(() -> homeworksService.deleteHomeworkById(1L));
    verify(filesRepository).delete(homework);
  }

  @Test
  void deleteHomeworkById_notFound() {
    when(filesRepository.findById(99L)).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> homeworksService.deleteHomeworkById(99L));

    assertTrue(ex.getMessage().contains("Работа не найдена по id"));
  }

  private Homework createHomework(Long id, String fileName) {
    Homework homework = new Homework(fileName);
    homework.setId(id);
    return homework;
  }
}
