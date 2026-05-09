package storingService.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisServiceClient {

  private final RestClient restClient;

  @Value("${analysis.service.url:http://analysis-service:8082}")
  private String analysisServiceUrl;

  public void triggerAnalysis(Long homeworkId, String fileName) {
    try {
      restClient.post()
          .uri(analysisServiceUrl + "/api/analysis/trigger")
          .body(new AnalysisRequest(homeworkId, fileName))
          .retrieve()
          .toBodilessEntity();
      log.info("Запрос на анализ отправлен: homeworkId={}, fileName={}", homeworkId, fileName);
    } catch (Exception e) {
      log.error("Не удалось отправить запрос на анализ: {}", e.getMessage());
    }
  }

  record AnalysisRequest(Long homeworkId, String fileName) {

  }
}
