package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.services.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageUploadController {
    private final StorageService storageService;
    @PostMapping
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty() || !Objects.requireNonNull(file.getContentType()). startsWith("image")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file type. Only images are allowed.");
        }
        String originalName = Objects.requireNonNull(file.getOriginalFilename()).trim();
        int dotIndex = originalName.lastIndexOf('.');
        String fileExtension ="";
        if(dotIndex >0) fileExtension = originalName.substring(dotIndex);
        String uniqueName = UUID.randomUUID() + fileExtension;
        String imageUrl = storageService.save(file, uniqueName);
        Map<String, String> map = new HashMap<>();
        map.put("url", imageUrl);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
}
