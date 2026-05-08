package storingService.domain.entity.uploader;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import storingService.domain.entity.readable.Homework;

@Entity
@Table(name = "students")
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@AllArgsConstructor
public class Student implements Uploader {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @Column(name = "student_id")
  private Long id;

  @Column(name = "student_name")
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "student_surname")
  @EqualsAndHashCode.Include
  private String surname;

  @Column(name = "created_at")
  private Instant createdAt;

  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Homework> homeworks = new ArrayList<>();

  public boolean correctFullName() {
    return !Objects.toString(name, "").isBlank()
        && !Objects.toString(surname, "").isBlank();
  }

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }

}
