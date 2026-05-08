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

  @Column(nullable = false, unique = true, name = "student_name")
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "created_at")
  private Instant createdAt;

  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Homework> homeworks = new ArrayList<>();

  public boolean correctFullName() {
    return !Objects.toString(name, "").isBlank();
  }

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }

  public Student(String name) {
    this.name = name;
  }

  public void addHomework(Homework homework) {
    homeworks.add(homework);
    homework.setStudent(this);
  }

  public void deleteHomework(Homework homework) {
    homeworks.remove(homework);
    homework.setStudent(null);
  }

}
