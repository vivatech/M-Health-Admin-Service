package com.mhealth.admin.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileSystemStorageService implements StorageService {
    private final Path rootLocation;

    @Value("${app.upload.permissions}")
    private String FILEPERMISSIONS;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }
    
    @Override
    public void store(MultipartFile file, String newfilename) {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (newfilename.contains("..")) {
                // This is a security check
                throw new StorageException("Cannot store file with relative path outside current directory " + newfilename);
            }
            Integer lastindex = newfilename.lastIndexOf(File.separator);
            if (lastindex != -1) {
                Path path = this.rootLocation.resolve(newfilename.substring(0, newfilename.lastIndexOf(File.separator)));

                Files.createDirectories(path);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(newfilename),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            Path path = this.rootLocation.resolve(newfilename);
//            logger.info("Store path : " + path);
            if (!FILEPERMISSIONS.isEmpty()) {
                log.info("store path : " + path);
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString(FILEPERMISSIONS));
            }

        } catch (IOException e) {
            throw new StorageException("Failed to store file " + newfilename, e);
        }
    }
//public String storing(MultipartFile file, String filename, String insideDirectory) {
//    try {
//        Files.createDirectories(this.rootLocation.resolve(insideDirectory));
//        Files.copy(file.getInputStream(), this.rootLocation.resolve(insideDirectory).resolve(filename));
//
//        return filename;
//    } catch (IOException e) {
//        throw new StorageException("Failed to store file " + filename, e);
//    }
//}

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        //FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
