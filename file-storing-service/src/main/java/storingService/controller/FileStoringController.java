package storingService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import storingService.service.CentralService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileStoringController {

  private final CentralService centralService;

  @PostMapping("/add-student/{name}")
  public ResponseEntity<Void> addStudent(@PathVariable String name) {
    centralService.createStudent(name);
    return ResponseEntity.ok().build();
  }

}
