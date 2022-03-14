package com.sclad.scladapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import com.sclad.scladapp.entity.UploadedFile;
import com.sclad.scladapp.exceptions.FileStorageException;
import com.sclad.scladapp.exceptions.UploadedFileNotFoundException;
import com.sclad.scladapp.repository.UploadedFileRepository;

@Service
public class UploadedFileServiceImpl implements UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;

    @Autowired
    public UploadedFileServiceImpl(UploadedFileRepository uploadedFileRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
    }

    @Override
    public Long create(MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Provided file has invalid filename!");
            }
            UploadedFile file = new UploadedFile(fileName, multipartFile.getContentType(), multipartFile.getBytes());
            return uploadedFileRepository.save(file).getId();
        } catch (IOException exception) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again.");
        }
    }

    @Override
    public void remove(Long id) {
        UploadedFile uploadedFile = getById(id);
        if (uploadedFile != null) {
            uploadedFileRepository.delete(uploadedFile);
        }
    }

    @Override
    public ResponseEntity<byte[]> downloadByFileName(String fileName) {
        UploadedFile uploadedFile = getByFileName(fileName);
        if (uploadedFile != null) {
            return getFileResponseEntity(uploadedFile);
        }
        return null;
    }

    @Override
    public ResponseEntity<byte[]> downloadById(Long id) {
        UploadedFile uploadedFile = getById(id);
        if (uploadedFile != null) {
            return getFileResponseEntity(uploadedFile);
        }
        return null;
    }

    public ResponseEntity<byte[]> getFileResponseEntity(UploadedFile uploadedFile) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(uploadedFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + uploadedFile.getFileName() + "\"")
                .body(uploadedFile.getData());
    }

    public UploadedFile getById(Long id) {
        UploadedFile uploadedFile = uploadedFileRepository.findById(id).orElse(null);
        if (uploadedFile != null) {
            return uploadedFile;
        } else {
            throw new UploadedFileNotFoundException();
        }
    }

    public UploadedFile getByFileName(String fileName) {
        UploadedFile uploadedFile = uploadedFileRepository.findByFileName(fileName).orElse(null);
        if (uploadedFile != null) {
            return uploadedFile;
        } else {
            throw new UploadedFileNotFoundException();
        }
    }
}
