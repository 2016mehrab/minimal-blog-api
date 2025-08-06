package com.samurai74.minimalblog.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String save(MultipartFile file, String filename);
}
