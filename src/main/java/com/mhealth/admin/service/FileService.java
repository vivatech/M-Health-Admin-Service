package com.mhealth.admin.service;

import com.mhealth.admin.constants.Messages;
import com.mhealth.admin.dto.ValidateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class FileService {

    @Autowired
    private MessageSource messageSource;

    @Value("${storage.location}")
    private String uploadDirectoryLocation;

    public ValidateResult validateFile(Locale locale, MultipartFile file, List<String> allowedExtensions, long maxSize) throws Exception {
        String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!allowedExtensions.contains(extension)) {
            return new ValidateResult(false, messageSource.getMessage(Messages.SELECT_PROFILE_PICTURE, null, locale));
        }
        if (file.getSize() > maxSize) {
            return new ValidateResult(false, messageSource.getMessage(Messages.DOCTOR_ID_SIZE_LIMIT, null, locale));
        }
        return new ValidateResult(true, null);
    }

    public String saveFile(MultipartFile file, String directory, String fileName) throws IOException {

        // Ensure the directory exists
        Path dirPath = Paths.get(uploadDirectoryLocation + "/" + directory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Save the file
        Path filePath = dirPath.resolve(fileName);
        file.transferTo(filePath.toFile());

        // Return the relative path of the saved file
        return filePath.toString();
    }


    public void deleteFile(String directory, String fileName) throws IOException {
        Path path = Paths.get(uploadDirectoryLocation + "/" + directory + "/" + fileName);
        if (Files.exists(path)) {
            Files.delete(path); // Delete the file
        } else {
            throw new IOException("File not found: " + directory + "/" + fileName);
        }
    }



    public String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
