package storingService.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import storingService.domain.entity.readable.Homework;

@Repository
public interface FilesRepository extends JpaRepository<Homework, Long> {
  Optional<Homework> findByFileName(String name);
}
