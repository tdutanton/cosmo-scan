package storingService.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

  @TempDir
  Path tempDir;

  private FileStorageService fileStorageService;

  @BeforeEach
  void setUp() {
    fileStorageService = new FileStorageService(tempDir.toString());
  }

  @Test
  void store_savesFileAndReturnsName() throws IOException {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "document.pdf",
        "application/pdf",
        "test content".getBytes()
    );

    String storedName = fileStorageService.store(file);

    assertNotNull(storedName);
    assertTrue(storedName.endsWith(".pdf"));
    Path targetPath = tempDir.resolve(storedName);
    assertTrue(Files.exists(targetPath));
  }

  @Test
  void store_nullFilename() {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        null,
        "application/pdf",
        "test content".getBytes()
    );

    String storedName = fileStorageService.store(file);

    assertNotNull(storedName);
    assertFalse(storedName.contains("."));
  }

  @Test
  void store_fileWithoutExtension() {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "document",
        "application/octet-stream",
        "test content".getBytes()
    );

    String storedName = fileStorageService.store(file);

    assertNotNull(storedName);
    assertFalse(storedName.contains("."));
  }

  @Test
  void loadFile_returnsContent() throws IOException {
    Path testFile = tempDir.resolve("test.txt");
    Files.writeString(testFile, "test content");
    String fileName = testFile.getFileName().toString();

    byte[] content = fileStorageService.loadFile(fileName);

    assertEquals("test content", new String(content));
  }

  @Test
  void loadFile_fileNotFound() {
    assertThrows(RuntimeException.class,
        () -> fileStorageService.loadFile("nonexistent.txt"));
  }

  @Test
  void fileExists_existingFile() throws IOException {
    Path testFile = tempDir.resolve("existing.txt");
    Files.writeString(testFile, "content");
    String fileName = testFile.getFileName().toString();

    assertTrue(fileStorageService.fileExists(fileName));
  }

  @Test
  void fileExists_nonexistentFile() {
    assertFalse(fileStorageService.fileExists("nonexistent.txt"));
  }

  @Test
  void deleteFile_existingFile() throws IOException {
    Path testFile = tempDir.resolve("toDelete.txt");
    Files.writeString(testFile, "content");
    String fileName = testFile.getFileName().toString();

    assertDoesNotThrow(() -> fileStorageService.deleteFile(fileName));
    assertFalse(Files.exists(testFile));
  }

  @Test
  void deleteFile_nonexistentFile() {
    assertDoesNotThrow(() -> fileStorageService.deleteFile("nonexistent.txt"));
  }

  @Test
  void store_invalidDirectory() {
    String invalidPath = System.getProperty("os.name").startsWith("Windows")
        ? "X:\\invalid\\path\\that\\does\\not\\exist" : "/invalid/path/that/does/not/exist";

    assertThrows(RuntimeException.class, () -> new FileStorageService(invalidPath));
  }
}
