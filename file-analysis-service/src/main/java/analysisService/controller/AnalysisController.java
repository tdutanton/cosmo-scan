package analysisService.controller;

import analysisService.dto.AnalysisTriggerRequest;
import analysisService.dto.ReportResponse;
import analysisService.service.AnalysisService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

  private final AnalysisService analysisService;

  @PostMapping("/trigger")
  public ResponseEntity<ReportResponse> triggerAnalysis(@RequestBody AnalysisTriggerRequest request) {
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
}
