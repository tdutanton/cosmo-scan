package storingService.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import storingService.client.AnalysisServiceClient;
import storingService.domain.entity.readable.Homework;
import storingService.domain.entity.uploader.Student;
import storingService.dto.HomeworkResponse;
import storingService.dto.StudentResponse;
import storingService.exception.GlobalExceptionHandler;
import storingService.service.CentralService;
import storingService.service.FileStorageService;

@ExtendWith(MockitoExtension.class)
class FileStoringControllerTest {

  private MockMvc mockMvc;

  @Mock
  private CentralService centralService;

  @Mock
  private FileStorageService fileStorageService;

  @Mock
  private AnalysisServiceClient analysisServiceClient;

  @BeforeEach
  void setUp() {
    FileStoringController controller = new FileStoringController(centralService, fileStorageService,
        analysisServiceClient);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void addStudent_success() throws Exception {
    Student student = new Student("Иванов Иван Иванович");
    student.setId(1L);
    when(centralService.createStudent(anyString())).thenReturn(student);

    mockMvc.perform(post("/api/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content("\"Иванов Иван Иванович\""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Иванов Иван Иванович"));
  }

  @Test
  void getAllStudents_success() throws Exception {
    List<StudentResponse> students = List.of(
        new StudentResponse(1L, "Студент 1"),
        new StudentResponse(2L, "Студент 2")
    );
    when(centralService.getAllStudents()).thenReturn(students);

    mockMvc.perform(get("/api/students"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("Студент 1"));
  }

  @Test
  void getAllStudents_empty() throws Exception {
    when(centralService.getAllStudents()).thenReturn(List.of());

    mockMvc.perform(get("/api/students"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getStudent_success() throws Exception {
    StudentResponse student = new StudentResponse(1L, "Иванов Иван Иванович");
    when(centralService.getStudentById(1L)).thenReturn(student);

    mockMvc.perform(get("/api/students/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Иванов Иван Иванович"));
  }

  @Test
  void getStudent_notFound() throws Exception {
    when(centralService.getStudentById(99L))
        .thenThrow(new IllegalArgumentException("Студент не найден по id: 99"));

    mockMvc.perform(get("/api/students/99"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Студент не найден по id: 99"));
  }

  @Test
  void deleteStudent_success() throws Exception {
    doNothing().when(centralService).deleteStudentById(1L);

    mockMvc.perform(delete("/api/students/1"))
        .andExpect(status().isNoContent());

    verify(centralService).deleteStudentById(1L);
  }

  @Test
  void deleteStudent_notFound() throws Exception {
    doThrow(new IllegalArgumentException("Студент не найден по id: 99"))
        .when(centralService).deleteStudentById(99L);

    mockMvc.perform(delete("/api/students/99"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Студент не найден по id: 99"));
  }

  @Test
  void uploadFile_success() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "document.pdf",
        "application/pdf",
        "test content".getBytes()
    );

    Student student = new Student("Иванов Иван Иванович");
    student.setId(1L);
    when(centralService.safetyGetStudentFromRepo("Иванов Иван Иванович")).thenReturn(student);

    Homework homework = new Homework("document.pdf");
    homework.setId(1L);
    homework.setStudent(student);
    doNothing().when(centralService).saveHomework(any(Homework.class));

    when(fileStorageService.store(any())).thenReturn("uuid-document.pdf");
    doNothing().when(analysisServiceClient).triggerAnalysis(any(), any());

    mockMvc.perform(multipart("/api/files/upload")
            .file(file)
            .param("studentName", "Иванов Иван Иванович"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fileName").value("uuid-document.pdf"));

    verify(fileStorageService).store(any());
    verify(analysisServiceClient).triggerAnalysis(any(), any());
  }

  @Test
  void uploadFile_studentNotFound() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "document.pdf",
        "application/pdf",
        "test content".getBytes()
    );

    when(centralService.safetyGetStudentFromRepo("Неизвестный"))
        .thenThrow(new IllegalArgumentException("Студент не найден"));

    mockMvc.perform(multipart("/api/files/upload")
            .file(file)
            .param("studentName", "Неизвестный"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Студент не найден"));
  }

  @Test
  void getAllHomeworks_success() throws Exception {
    List<HomeworkResponse> homeworks = List.of(
        new HomeworkResponse(1L, "Работа 1", null, null),
        new HomeworkResponse(2L, "Работа 2", null, null)
    );
    when(centralService.getAllHomeworks()).thenReturn(homeworks);

    mockMvc.perform(get("/api/files"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].fileName").value("Работа 1"));
  }

  @Test
  void getAllHomeworks_empty() throws Exception {
    when(centralService.getAllHomeworks()).thenReturn(List.of());

    mockMvc.perform(get("/api/files"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getHomework_success() throws Exception {
    HomeworkResponse homework = new HomeworkResponse(1L, "Домашняя работа 1", null, null);
    when(centralService.getHomeworkById(1L)).thenReturn(homework);

    mockMvc.perform(get("/api/files/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.fileName").value("Домашняя работа 1"));
  }

  @Test
  void getHomework_notFound() throws Exception {
    when(centralService.getHomeworkById(99L))
        .thenThrow(new IllegalArgumentException("Работа не найдена по id: 99"));

    mockMvc.perform(get("/api/files/99"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Работа не найдена по id: 99"));
  }

  @Test
  void downloadFile_success() throws Exception {
    HomeworkResponse homework = new HomeworkResponse(1L, "document.pdf", null, null);
    when(centralService.getHomeworkById(1L)).thenReturn(homework);
    when(fileStorageService.loadFile("document.pdf")).thenReturn("test content".getBytes());

    mockMvc.perform(get("/api/files/1/download"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"document.pdf\""))
        .andExpect(content().bytes("test content".getBytes()));
  }

  @Test
  void downloadFile_notFound() throws Exception {
    when(centralService.getHomeworkById(99L))
        .thenThrow(new IllegalArgumentException("Работа не найдена по id: 99"));

    mockMvc.perform(get("/api/files/99/download"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Работа не найдена по id: 99"));
  }

  @Test
  void downloadFile_fileNotFound() throws Exception {
    HomeworkResponse homework = new HomeworkResponse(1L, "missing.pdf", null, null);
    when(centralService.getHomeworkById(1L)).thenReturn(homework);
    when(fileStorageService.loadFile("missing.pdf"))
        .thenThrow(new RuntimeException("Не удалось загрузить файл: missing.pdf"));

    mockMvc.perform(get("/api/files/1/download"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Не удалось загрузить файл: missing.pdf"));
  }

  @Test
  void deleteHomework_success() throws Exception {
    HomeworkResponse homework = new HomeworkResponse(1L, "document.pdf", null, null);
    when(centralService.getHomeworkById(1L)).thenReturn(homework);
    doNothing().when(fileStorageService).deleteFile("document.pdf");
    doNothing().when(centralService).deleteHomeworkById(1L);

    mockMvc.perform(delete("/api/files/1"))
        .andExpect(status().isNoContent());

    verify(fileStorageService).deleteFile("document.pdf");
    verify(centralService).deleteHomeworkById(1L);
  }

  @Test
  void deleteHomework_notFound() throws Exception {
    when(centralService.getHomeworkById(99L))
        .thenThrow(new IllegalArgumentException("Работа не найдена по id: 99"));

    mockMvc.perform(delete("/api/files/99"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Работа не найдена по id: 99"));
  }
}
