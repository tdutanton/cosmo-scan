package storingService.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import storingService.domain.entity.uploader.Student;

@Repository
public interface StudentsRepository extends JpaRepository<Student, Long> {
  Optional<Student> findByName(String name);
}
