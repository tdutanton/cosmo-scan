package storingService.dto;

public record FileUploadResponse(Long homeworkId, String fileName, String status) {
  public FileUploadResponse(Long homeworkId, String fileName) {
    this(homeworkId, fileName, "uploaded");
  }
}
