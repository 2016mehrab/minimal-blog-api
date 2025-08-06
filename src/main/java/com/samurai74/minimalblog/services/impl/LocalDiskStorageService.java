package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalDiskStorageService implements StorageService {
    @Value("${image-storage-directory}")
    private String imageStorageDirectory;

    @Override
    public String save(MultipartFile file, String filename) {
       try{
           Path path = Paths.get(imageStorageDirectory);
           Files.createDirectories(path);
           Path filePath = path.resolve(filename);
           Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
           return "images/" + filename;
       }
       catch(IOException ex){
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file.", ex);
       }
    }
}
