package com.sclad.scladapp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UploadedFileService {

    Long create(MultipartFile multipartFile);

    ResponseEntity<byte[]> downloadByFileName(String fileName);

    ResponseEntity<byte[]> downloadById(Long id);

    void remove(Long id);
}
