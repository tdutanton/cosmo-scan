package apiGateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

  @GetMapping("/storing")
  public Mono<ResponseEntity<Map<String, String>>> storingFallback() {
    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(Map.of("error", "Сервис хранения файлов временно недоступен. Попробуйте позже.")));
  }

  @GetMapping("/analysis")
  public Mono<ResponseEntity<Map<String, String>>> analysisFallback() {
    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(Map.of("error", "Сервис анализа временно недоступен. Попробуйте позже.")));
  }
}
