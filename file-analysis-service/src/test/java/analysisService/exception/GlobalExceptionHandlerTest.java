package analysisService.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleIllegalArgument_returnsBadRequest() {
    ResponseEntity<Map<String, String>> response = handler.handleIllegalArgument(
        new IllegalArgumentException("Тестовая ошибка")
    );

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Тестовая ошибка", response.getBody().get("error"));
  }

  @Test
  void handleRuntime_returnsInternalServerError() {
    ResponseEntity<Map<String, String>> response = handler.handleRuntime(
        new RuntimeException("Внутренняя ошибка")
    );

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Внутренняя ошибка", response.getBody().get("error"));
  }
}
