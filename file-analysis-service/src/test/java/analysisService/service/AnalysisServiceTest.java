package analysisService.service;

import analysisService.domain.entity.Report;
import analysisService.dto.ReportResponse;
import analysisService.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

  @Mock
  private ReportRepository reportRepository;

  private AnalysisService analysisService;

  @BeforeEach
  void setUp() {
    analysisService = new AnalysisService(reportRepository, "./test-uploads");
  }

  @Test
  void analyze_validPdf() {
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report r = invocation.getArgument(0);
      r.setId(1L);
      return r;
    });

    ReportResponse result = analysisService.analyze(1L, "document.pdf");

    assertNotNull(result);
    assertEquals("document.pdf", result.fileName());
    assertEquals(".pdf", result.fileFormat());
    assertEquals(1L, result.homeworkId());
  }

  @Test
  void analyze_validDocx() {
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report r = invocation.getArgument(0);
      r.setId(1L);
      return r;
    });

    ReportResponse result = analysisService.analyze(1L, "document.docx");

    assertEquals(".docx", result.fileFormat());
  }

  @Test
  void analyze_validTxt() {
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report r = invocation.getArgument(0);
      r.setId(1L);
      return r;
    });

    ReportResponse result = analysisService.analyze(1L, "document.txt");

    assertEquals(".txt", result.fileFormat());
  }

  @Test
  void analyze_invalidFormat() {
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report r = invocation.getArgument(0);
      r.setId(1L);
      return r;
    });

    ReportResponse result = analysisService.analyze(1L, "document.zip");

    assertEquals("требуется доработка", result.status());
    assertFalse(result.issues().isEmpty());
    assertTrue(result.issues().get(0).contains("Недопустимый формат"));
  }

  @Test
  void analyze_noExtension() {
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report r = invocation.getArgument(0);
      r.setId(1L);
      return r;
    });

    ReportResponse result = analysisService.analyze(1L, "document");

    assertEquals("требуется доработка", result.status());
    assertFalse(result.issues().isEmpty());
  }

  @Test
  void getReportsByHomeworkId() {
    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName("test.pdf");
    report.setStatus("принято");
    report.setFileFormat(".pdf");
    report.setFileSizeBytes(1000L);
    report.setIssues(List.of());
    when(reportRepository.findByHomeworkId(1L)).thenReturn(List.of(report));

    List<ReportResponse> result = analysisService.getReportsByHomeworkId(1L);

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).homeworkId());
  }

  @Test
  void getReportById_success() {
    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName("test.pdf");
    report.setStatus("принято");
    report.setFileFormat(".pdf");
    report.setFileSizeBytes(1000L);
    report.setIssues(List.of());
    when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

    ReportResponse result = analysisService.getReportById(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
  }

  @Test
  void getReportById_notFound() {
    when(reportRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> analysisService.getReportById(99L));
  }

  @Test
  void getAllReports() {
    Report r1 = new Report();
    r1.setId(1L);
    r1.setHomeworkId(1L);
    r1.setFileName("test1.pdf");
    r1.setStatus("принято");
    r1.setFileFormat(".pdf");
    r1.setFileSizeBytes(1000L);
    r1.setIssues(List.of());

    Report r2 = new Report();
    r2.setId(2L);
    r2.setHomeworkId(2L);
    r2.setFileName("test2.docx");
    r2.setStatus("требуется доработка");
    r2.setFileFormat(".docx");
    r2.setFileSizeBytes(2000L);
    r2.setIssues(List.of("issue"));

    when(reportRepository.findAll()).thenReturn(List.of(r1, r2));

    List<ReportResponse> result = analysisService.getAllReports();

    assertEquals(2, result.size());
  }

  @Test
  void analyze_fileSizeExceedsLimit(@TempDir Path tempDir) throws Exception {
    File largeFile = tempDir.resolve("large.pdf").toFile();
    largeFile.createNewFile();
    byte[] data = new byte[1024 * 1024 + 1];
    java.nio.file.Files.write(largeFile.toPath(), data);

    analysisService = new AnalysisService(reportRepository, tempDir.toString());

    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report r = invocation.getArgument(0);
      r.setId(1L);
      return r;
    });

    ReportResponse result = analysisService.analyze(1L, "large.pdf");

    assertEquals("требуется доработка", result.status());
    assertTrue(result.issues().stream().anyMatch(i -> i.contains("Превышен лимит размера файла")));
  }

  @Test
  void firstFile_success(@TempDir Path tempDir) throws Exception {
    File testFile = tempDir.resolve("document.pdf").toFile();
    testFile.createNewFile();
    java.nio.file.Files.write(testFile.toPath(), "content".getBytes());

    analysisService = new AnalysisService(reportRepository, tempDir.toString());

    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName("document.pdf");
    report.setStatus("принято");
    report.setFileFormat(".pdf");
    report.setFileSizeBytes(7L);
    report.setIssues(List.of());

    when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

    var result = analysisService.firstFile();

    assertTrue(result.isPresent());
    assertTrue(result.get().getFileName().endsWith("document.pdf"));
  }

  @Test
  void firstFile_fileNotExists(@TempDir Path tempDir) throws Exception {
    analysisService = new AnalysisService(reportRepository, tempDir.toString());

    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName("nonexistent.pdf");
    report.setStatus("принято");
    report.setFileFormat(".pdf");
    report.setFileSizeBytes(0L);
    report.setIssues(List.of());

    when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

    var result = analysisService.firstFile();

    assertTrue(result.isEmpty());
  }

  @Test
  void firstFile_reportNotFound() {
    when(reportRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> analysisService.firstFile());
  }

  @Test
  void randomTxtFile_success(@TempDir Path tempDir) throws Exception {
    File txtFile = tempDir.resolve("document.txt").toFile();
    java.nio.file.Files.write(txtFile.toPath(), "hello world".getBytes());

    analysisService = new AnalysisService(reportRepository, tempDir.toString());

    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName("document.txt");
    report.setStatus("принято");
    report.setFileFormat(".txt");
    report.setFileSizeBytes(11L);
    report.setIssues(List.of());

    when(reportRepository.findAll()).thenReturn(List.of(report));

    var result = analysisService.randomTxtFile();

    assertTrue(result.isPresent());
    assertTrue(result.get().getFileName().endsWith("document.txt"));
  }

  @Test
  void randomTxtFile_noTxtFiles() {
    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName("document.pdf");
    report.setStatus("принято");
    report.setFileFormat(".pdf");
    report.setFileSizeBytes(1000L);
    report.setIssues(List.of());

    when(reportRepository.findAll()).thenReturn(List.of(report));

    var result = analysisService.randomTxtFile();

    assertTrue(result.isEmpty());
  }

  @Test
  void randomTxtFile_noAcceptedFiles() {
    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName("document.txt");
    report.setStatus("требуется доработка");
    report.setFileFormat(".txt");
    report.setFileSizeBytes(1000L);
    report.setIssues(List.of("issue"));

    when(reportRepository.findAll()).thenReturn(List.of(report));

    var result = analysisService.randomTxtFile();

    assertTrue(result.isEmpty());
  }

  @Test
  void randomTxtFile_fileNotOnDisk(@TempDir Path tempDir) throws Exception {
    analysisService = new AnalysisService(reportRepository, tempDir.toString());

    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName("missing.txt");
    report.setStatus("принято");
    report.setFileFormat(".txt");
    report.setFileSizeBytes(0L);
    report.setIssues(List.of());

    when(reportRepository.findAll()).thenReturn(List.of(report));

    var result = analysisService.randomTxtFile();

    assertTrue(result.isEmpty());
  }

  @Test
  void randomTxtFile_nullFileName() {
    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName(null);
    report.setStatus("принято");
    report.setFileFormat(".txt");
    report.setFileSizeBytes(0L);
    report.setIssues(List.of());

    when(reportRepository.findAll()).thenReturn(List.of(report));

    var result = analysisService.randomTxtFile();

    assertTrue(result.isEmpty());
  }
}
