package analysisService.controller;

import analysisService.dto.AnalysisTriggerRequest;
import analysisService.dto.ReportResponse;
import analysisService.service.AnalysisService;
import analysisService.wordCloudClient.WordCloudClient;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

  private final AnalysisService analysisService;
  private final WordCloudClient wordCloudClient;

  @PostMapping("/trigger")
  public ResponseEntity<ReportResponse> triggerAnalysis(
      @RequestBody AnalysisTriggerRequest request) {
    ReportResponse report = analysisService.analyze(request.homeworkId(), request.fileName());
    return ResponseEntity.ok(report);
  }

  @GetMapping("/reports")
  public ResponseEntity<List<ReportResponse>> getAllReports() {
    return ResponseEntity.ok(analysisService.getAllReports());
  }

  @GetMapping("/reports/{reportId}")
  public ResponseEntity<ReportResponse> getReport(@PathVariable Long reportId) {
    return ResponseEntity.ok(analysisService.getReportById(reportId));
  }

  @GetMapping("/homeworks/{homeworkId}/reports")
  public ResponseEntity<List<ReportResponse>> getReportsByHomework(@PathVariable Long homeworkId) {
    return ResponseEntity.ok(analysisService.getReportsByHomeworkId(homeworkId));
  }

  @PostMapping("/word-cloud")
  public ResponseEntity<byte[]> createWordCloud() throws IOException {
    try {
      Optional<Path> firstFilePath = analysisService.randomTxtFile();
      if (firstFilePath.isPresent()) {

        byte[] image = wordCloudClient.generateWordCloud(firstFilePath.get().toString());

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .header("Content-Disposition", "attachment; filename=\"wordcloud.png\"")
            .body(image);
      }
      log.error("Нет подходящего файла");
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      log.error("Ошибка генерации", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
