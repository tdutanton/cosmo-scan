package storingService.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  private final Path storageLocation;

  public FileStorageService(@Value("${file.storage.path:./uploads}") String storagePath) {
    this.storageLocation = Paths.get(storagePath).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.storageLocation);
    } catch (IOException e) {
      throw new RuntimeException("Не удалось создать директорию для хранения файлов", e);
    }
  }

  public String store(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    String extension = originalFilename != null && originalFilename.contains(".")
        ? originalFilename.substring(originalFilename.lastIndexOf("."))
        : "";
    String storedName = UUID.randomUUID() + extension;

    try {
      Path targetLocation = this.storageLocation.resolve(storedName);
      Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      return storedName;
    } catch (IOException e) {
      throw new RuntimeException("Не удалось сохранить файл: " + originalFilename, e);
    }
  }

  public byte[] loadFile(String fileName) {
    try {
      Path filePath = this.storageLocation.resolve(fileName).normalize();
      return Files.readAllBytes(filePath);
    } catch (IOException e) {
      throw new RuntimeException("Не удалось загрузить файл: " + fileName, e);
    }
  }

  public boolean fileExists(String fileName) {
    Path filePath = this.storageLocation.resolve(fileName).normalize();
    return Files.exists(filePath);
  }

  public void deleteFile(String fileName) {
    try {
      Path filePath = this.storageLocation.resolve(fileName).normalize();
      Files.deleteIfExists(filePath);
    } catch (IOException e) {
      throw new RuntimeException("Не удалось удалить файл: " + fileName, e);
    }
  }
}
