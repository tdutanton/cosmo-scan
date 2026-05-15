package analysisService.wordCloudClient;

import java.io.File;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordCloudClient {

  private static final String API_URL = "https://quickchart.io/wordcloud";

  private static final RestClient restClient = RestClient.builder()
      .baseUrl(API_URL)
      .defaultHeader("Content-Type", "application/json")
      .requestFactory(new SimpleClientHttpRequestFactory())
      .build();

  public byte[] generateWordCloud(String filePath) {
    String content = "";
    try {
      content = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error(String.valueOf(e));
    }
    JSONObject params = new JSONObject();
    params.put("useWordList", false);
    params.put("text", content);
    params.put("format", "png");
    params.put("fontScale", 20);
    params.put("width", 1000);
    params.put("height", 1000);
    params.put("case", "lower");
    params.put("removeStopwords", true);
    params.put("cleanWords", true);
    params.put("maxNumWords", 10);
    params.put("minWordLength", 3);

    return restClient.post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(params.toString())
        .retrieve()
        .body(byte[].class);

  }
}
