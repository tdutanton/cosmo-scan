package analysisService.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

  @Test
  void testReportCreation() {
    Report report = new Report();
    report.setId(1L);
    report.setHomeworkId(1L);
    report.setFileName("test.pdf");
    report.setStatus("принято");
    report.setFileFormat(".pdf");
    report.setFileSizeBytes(1000L);
    report.setIssues(List.of());

    assertEquals(1L, report.getId());
    assertEquals("test.pdf", report.getFileName());
    assertEquals("принято", report.getStatus());
    assertEquals(".pdf", report.getFileFormat());
    assertEquals(1000L, report.getFileSizeBytes());
  }

  @Test
  void testReportWithIssues() {
    Report report = new Report();
    report.setHomeworkId(1L);
    report.setFileName("test.zip");
    report.setStatus("требуется доработка");
    report.setFileFormat(".zip");
    report.setFileSizeBytes(500L);
    report.setIssues(List.of("Недопустимый формат"));

    assertEquals("требуется доработка", report.getStatus());
    assertEquals(1, report.getIssues().size());
    assertTrue(report.getIssues().contains("Недопустимый формат"));
  }

  @Test
  void testReportEquals() {
    Report r1 = new Report();
    r1.setId(1L);
    r1.setHomeworkId(1L);
    r1.setFileName("test.pdf");

    Report r2 = new Report();
    r2.setId(1L);
    r2.setHomeworkId(1L);
    r2.setFileName("test.pdf");

    assertEquals(r1, r2);
  }
}
