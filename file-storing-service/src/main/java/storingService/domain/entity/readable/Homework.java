package storingService.domain.entity.readable;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Homework implements Readable {

  @EqualsAndHashCode.Include
  private Long id;
  private String fileName;

  public boolean hasExtension() {
    return !Objects.toString(fileName, "").isBlank() && fileName.contains(".");
  }

}
