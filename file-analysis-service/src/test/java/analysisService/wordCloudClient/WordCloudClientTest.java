package analysisService.wordCloudClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WordCloudClientTest {

  private final WordCloudClient wordCloudClient = new WordCloudClient();
  @TempDir
  Path tempDir;

  @Test
  void generateWordCloud_fileWithContent() throws Exception {
    File testFile = tempDir.resolve("test.txt").toFile();
    try (FileWriter writer = new FileWriter(testFile)) {
      writer.write("Hello world test content for word cloud generation");
    }

    assertDoesNotThrow(() -> {
      byte[] result = wordCloudClient.generateWordCloud(testFile.getAbsolutePath());
      assertNotNull(result);
    });
  }
}