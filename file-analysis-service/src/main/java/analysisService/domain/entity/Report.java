package analysisService.domain.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reports")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "report_id")
  private Long id;

  @Column(name = "homework_id", nullable = false)
  private Long homeworkId;

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "file_format")
  private String fileFormat;

  @Column(name = "file_size_bytes")
  private Long fileSizeBytes;

  @Column(name = "created_at")
  private Instant createdAt;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "report_issues", joinColumns = @JoinColumn(name = "report_id"))
  @Column(name = "issue")
  private List<String> issues = new ArrayList<>();

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }
}
