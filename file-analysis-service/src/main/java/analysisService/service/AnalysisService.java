package analysisService.service;

import analysisService.domain.entity.Report;
import analysisService.dto.ReportResponse;
import analysisService.repository.ReportRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnalysisService {

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".docx", ".txt");
  private static final long MAX_FILE_SIZE = 1_048_576; // 1 MB

  private final ReportRepository reportRepository;
  private final String storagePath;

  public AnalysisService(ReportRepository reportRepository,
      @Value("${file.storage.path:./uploads}") String storagePath) {
    this.reportRepository = reportRepository;
    this.storagePath = storagePath;
  }

  public ReportResponse analyze(Long homeworkId, String fileName) {
    List<String> issues = new ArrayList<>();
    String status = "принято";
    String fileFormat = getExtension(fileName);
    long fileSize = getFileSize(fileName);

    if (!ALLOWED_EXTENSIONS.contains(fileFormat.toLowerCase())) {
      issues.add("Недопустимый формат файла: " + fileFormat + ". Разрешены: pdf, docx, txt");
      status = "требуется доработка";
    }

    if (fileSize > MAX_FILE_SIZE) {
      issues.add("Превышен лимит размера файла: " + fileSize + " байт (макс. " + MAX_FILE_SIZE + " байт)");
      status = "требуется доработка";
    }

    if (fileSize == 0) {
      issues.add("Файл пустой");
      status = "требуется доработка";
    }

    Report report = new Report();
    report.setHomeworkId(homeworkId);
    report.setFileName(fileName);
    report.setStatus(status);
    report.setFileFormat(fileFormat);
    report.setFileSizeBytes(fileSize);
    report.setIssues(issues);

    report = reportRepository.save(report);
    log.info("Создан отчёт: homeworkId={}, status={}, issues={}", homeworkId, status, issues);

    return ReportResponse.from(report);
  }

  public List<ReportResponse> getReportsByHomeworkId(Long homeworkId) {
    return reportRepository.findByHomeworkId(homeworkId).stream()
        .map(ReportResponse::from)
        .toList();
  }

  public ReportResponse getReportById(Long reportId) {
    return reportRepository.findById(reportId)
        .map(ReportResponse::from)
        .orElseThrow(() -> new IllegalArgumentException("Отчёт не найден: " + reportId));
  }

  public List<ReportResponse> getAllReports() {
    return reportRepository.findAll().stream()
        .map(ReportResponse::from)
        .toList();
  }

  private String getExtension(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
      return "";
    }
    return fileName.substring(fileName.lastIndexOf("."));
  }

  private long getFileSize(String fileName) {
    try {
      Path filePath = Path.of(storagePath).resolve(fileName).normalize();
      if (Files.exists(filePath)) {
        return Files.size(filePath);
      }
      log.warn("Файл не найден в хранилище: {}", fileName);
      return 0;
    } catch (IOException e) {
      log.error("Ошибка при получении размера файла: {}", fileName, e);
      return 0;
    }
  }
}
