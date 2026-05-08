package storingService.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import storingService.domain.entity.readable.Homework;
import storingService.repository.FilesRepository;

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

}
