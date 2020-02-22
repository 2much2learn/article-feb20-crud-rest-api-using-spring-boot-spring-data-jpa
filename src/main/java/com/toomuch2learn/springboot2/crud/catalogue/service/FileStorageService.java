package com.toomuch2learn.springboot2.crud.catalogue.service;

import com.toomuch2learn.springboot2.crud.catalogue.configuration.FileStorageProperties;
import com.toomuch2learn.springboot2.crud.catalogue.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) throws Exception {
        this.fileStorageLocation
            = Paths
                .get(fileStorageProperties.getUploadLocation())
                .toAbsolutePath()
                .normalize();
    }

    public String storeFile(MultipartFile file) throws FileStorageException {

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains any invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException(String.format("Filename %s contains invalid characters", fileName));
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException(String.format("Could not store file %s. Please try again!", fileName), ex);
        }
    }
}
