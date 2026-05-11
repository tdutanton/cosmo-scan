package storingService.domain.entity.readable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import storingService.domain.entity.uploader.Student;
import storingService.domain.entity.uploader.Uploader;

@Entity
@Table(name = "files")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Homework implements Readable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @Column(name = "file_id")
  private Long id;

  @Column(nullable = false, name = "file_name")
  @EqualsAndHashCode.Include
  private String fileName;

  @Column(name = "created_at")
  private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private Student student;

  public boolean hasName() {
    return !Objects.toString(fileName, "").isBlank();
  }

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }

  public Homework(String name) {
    this.fileName = name;
  }

}
