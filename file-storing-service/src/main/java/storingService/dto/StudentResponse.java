package storingService.dto;

public record StudentResponse(Long id, String name) {
  public static StudentResponse from(storingService.domain.entity.uploader.Student student) {
    return new StudentResponse(student.getId(), student.getName());
  }
}
