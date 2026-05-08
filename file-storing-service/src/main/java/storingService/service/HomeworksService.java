package storingService.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import storingService.domain.entity.readable.Homework;
import storingService.dto.HomeworkResponse;
import storingService.repository.FilesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeworksService {
  private final FilesRepository filesRepository;

  public Homework createHomework(String name) {
    Homework homework = new Homework(name);
    if (homework.hasName() && filesRepository.findByFileName(name).isEmpty()) {
      return homework;
    }
    throw new IllegalArgumentException("Некорректный объект: " + name);
  }

  @Transactional
  public void deleteHomework(String name) {
    Homework homework = filesRepository.findByFileName(name).orElseThrow(
        () -> new IllegalArgumentException("Не найден файл: " + name)
    );
    filesRepository.delete(homework);
  }

  @Transactional
  public void saveHomework(Homework homework) {
    filesRepository.save(homework);
  }

  public List<HomeworkResponse> getAllHomeworks() {
    return filesRepository.findAll().stream()
        .map(HomeworkResponse::from)
        .toList();
  }

  public HomeworkResponse getHomeworkById(Long id) {
    return filesRepository.findById(id)
        .map(HomeworkResponse::from)
        .orElseThrow(() -> new IllegalArgumentException("Работа не найдена по id: " + id));
  }

  @Transactional
  public void deleteHomeworkById(Long id) {
    Homework homework = filesRepository.findById(id).orElseThrow(
        () -> new IllegalArgumentException("Работа не найдена по id: " + id)
    );
    filesRepository.delete(homework);
  }
}
