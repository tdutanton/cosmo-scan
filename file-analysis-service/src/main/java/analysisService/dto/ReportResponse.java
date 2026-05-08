package analysisService.dto;

import java.util.List;

public record ReportResponse(Long id, Long homeworkId, String fileName, String status,
                              String fileFormat, Long fileSizeBytes, List<String> issues) {

  public static ReportResponse from(analysisService.domain.entity.Report report) {
    return new ReportResponse(
        report.getId(),
        report.getHomeworkId(),
        report.getFileName(),
        report.getStatus(),
        report.getFileFormat(),
        report.getFileSizeBytes(),
        report.getIssues()
    );
  }
}
