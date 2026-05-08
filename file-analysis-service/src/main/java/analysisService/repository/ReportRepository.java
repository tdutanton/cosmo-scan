package analysisService.repository;

import analysisService.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
  List<Report> findByHomeworkId(Long homeworkId);
  boolean existsByHomeworkId(Long homeworkId);
}
