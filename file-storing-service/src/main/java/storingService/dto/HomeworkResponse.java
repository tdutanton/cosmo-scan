package storingService.dto;

import java.time.Instant;

public record HomeworkResponse(Long id, String fileName, String studentName, Instant createdAt) {
  public static HomeworkResponse from(storingService.domain.entity.readable.Homework homework) {
    return new HomeworkResponse(
        homework.getId(),
        homework.getFileName(),
        homework.getStudent() != null ? homework.getStudent().getName() : null,
        homework.getCreatedAt()
    );
  }
}
