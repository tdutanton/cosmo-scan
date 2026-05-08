package storingService.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import storingService.domain.entity.uploader.Student;
import storingService.repository.StudentsRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentsService {
  private final StudentsRepository studentsRepository;

  @Transactional
  public Student createStudent(String name) {
    if(studentsRepository.findByName(name).isPresent()) {
      throw new IllegalArgumentException("Такой студент уже есть в списках: " + name);
    }
    Student student = new Student(name);
    if (student.correctFullName()) {
      studentsRepository.save(student);
      return student;
    }
    throw new IllegalArgumentException("Некорректное имя студента: " + name);
  }

  public void deleteStudent(String name) {
    Student student = studentsRepository.findByName(name).orElseThrow(
        () -> new IllegalArgumentException("Студент не найден: " + name)
    );
  }
}
