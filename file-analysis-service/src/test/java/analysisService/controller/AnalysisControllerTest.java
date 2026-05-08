package analysisService.controller;

import analysisService.dto.AnalysisTriggerRequest;
import analysisService.dto.ReportResponse;
import analysisService.service.AnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AnalysisControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AnalysisService analysisService;

  @InjectMocks
  private AnalysisController controller;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void triggerAnalysis() throws Exception {
    ReportResponse response = new ReportResponse(1L, 1L, "test.pdf", "принято", ".pdf", 1000L, List.of());
    when(analysisService.analyze(any(), any())).thenReturn(response);

    AnalysisTriggerRequest request = new AnalysisTriggerRequest(1L, "test.pdf");

    mockMvc.perform(post("/api/analysis/trigger")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("принято"));
  }

  @Test
  void getAllReports() throws Exception {
    List<ReportResponse> reports = List.of(
        new ReportResponse(1L, 1L, "test.pdf", "принято", ".pdf", 1000L, List.of())
    );
    when(analysisService.getAllReports()).thenReturn(reports);

    mockMvc.perform(get("/api/analysis/reports"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getReport() throws Exception {
    ReportResponse response = new ReportResponse(1L, 1L, "test.pdf", "принято", ".pdf", 1000L, List.of());
    when(analysisService.getReportById(1L)).thenReturn(response);

    mockMvc.perform(get("/api/analysis/reports/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void getReportsByHomework() throws Exception {
    List<ReportResponse> reports = List.of(
        new ReportResponse(1L, 5L, "test.pdf", "принято", ".pdf", 1000L, List.of())
    );
    when(analysisService.getReportsByHomeworkId(5L)).thenReturn(reports);

    mockMvc.perform(get("/api/analysis/homeworks/5/reports"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].homeworkId").value(5));
  }
}
