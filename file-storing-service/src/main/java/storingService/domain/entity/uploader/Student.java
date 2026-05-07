package storingService.domain.entity.uploader;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@Builder
@AllArgsConstructor
public class Student implements Uploader {

  @EqualsAndHashCode.Include
  private Long id;
  private String name;
  private String surname;

  public boolean correctFullName() {
    return !Objects.toString(name, "").isBlank()
        && !Objects.toString(surname, "").isBlank();
  }

}
