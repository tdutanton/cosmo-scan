package storingService.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import storingService.domain.entity.uploader.Student;
import storingService.dto.StudentResponse;
import storingService.repository.StudentsRepository;

import java.util.List;

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

  @Transactional
  public Student safetyGetStudentFromRepo(String name) {
    return studentsRepository.findByName(name)
        .orElseGet(() -> createStudent(name));
  }

  @Transactional
  public void deleteStudent(String name) {
    Student student = studentsRepository.findByName(name).orElseThrow(
        () -> new IllegalArgumentException("Студент не найден: " + name)
    );
    studentsRepository.delete(student);
  }

  public List<StudentResponse> getAllStudents() {
    return studentsRepository.findAll().stream()
        .map(StudentResponse::from)
        .toList();
  }

  public StudentResponse getStudentById(Long id) {
    return studentsRepository.findById(id)
        .map(StudentResponse::from)
        .orElseThrow(() -> new IllegalArgumentException("Студент не найден по id: " + id));
  }

  @Transactional
  public void deleteStudentById(Long id) {
    Student student = studentsRepository.findById(id).orElseThrow(
        () -> new IllegalArgumentException("Студент не найден по id: " + id)
    );
    studentsRepository.delete(student);
  }
}
