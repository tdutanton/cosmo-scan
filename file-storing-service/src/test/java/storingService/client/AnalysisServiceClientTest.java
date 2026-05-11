package storingService.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceClientTest {

  @Mock
  private RestClient restClient;

  @Mock
  private RestClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private RestClient.RequestBodySpec requestBodySpec;

  @Mock
  private RestClient.ResponseSpec responseSpec;

  private AnalysisServiceClient analysisServiceClient;

  @BeforeEach
  void setUp() throws Exception {
    analysisServiceClient = new AnalysisServiceClient(restClient);
    var field = AnalysisServiceClient.class.getDeclaredField("analysisServiceUrl");
    field.setAccessible(true);
    field.set(analysisServiceClient, "http://localhost:8082");
  }

  @Test
  void triggerAnalysis_success() {
    when(restClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(AnalysisServiceClient.AnalysisRequest.class))).thenReturn(
        requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());

    analysisServiceClient.triggerAnalysis(1L, "document.pdf");

    verify(restClient).post();
    verify(requestBodySpec).retrieve();
    verify(responseSpec).toBodilessEntity();
  }

  @Test
  void triggerAnalysis_exception() {
    when(restClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(AnalysisServiceClient.AnalysisRequest.class))).thenReturn(
        requestBodySpec);
    when(requestBodySpec.retrieve()).thenThrow(new RuntimeException("Connection refused"));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      analysisServiceClient.triggerAnalysis(1L, "document.pdf");
    });

    assertThat(exception.getMessage())
        .contains("Не удалось отправить запрос на анализ")
        .contains("Connection refused")
        .contains("Проверьте работу сервиса-получателя запроса");

    verify(restClient).post();
    verify(requestBodyUriSpec).uri(any(String.class));
    verify(requestBodySpec).body(any(AnalysisServiceClient.AnalysisRequest.class));
    verify(requestBodySpec).retrieve();
  }
}
