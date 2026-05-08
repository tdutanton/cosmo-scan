package apiGateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(FallbackController.class)
class FallbackControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void storingFallback() {
    webTestClient.get()
        .uri("/fallback/storing")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
        .expectBody()
        .jsonPath("$.error").isNotEmpty();
  }

  @Test
  void analysisFallback() {
    webTestClient.get()
        .uri("/fallback/analysis")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
        .expectBody()
        .jsonPath("$.error").isNotEmpty();
  }
}
