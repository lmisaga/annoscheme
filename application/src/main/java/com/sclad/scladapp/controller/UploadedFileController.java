package com.sclad.scladapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import com.sclad.scladapp.service.UploadedFileService;

@RestController
@RequestMapping(value = "/api/uploadedFile")
public class UploadedFileController {

    private final UploadedFileService uploadedFileService;

    public UploadedFileController(UploadedFileService uploadedFileService) {
        this.uploadedFileService = uploadedFileService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Long create(@RequestBody @Valid MultipartFile fileToUpload) {
        return this.uploadedFileService.create(fileToUpload);
    }

    @RequestMapping(value = "/downloadByFileName/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadByFileName(@PathVariable String fileName) {
        return uploadedFileService.downloadByFileName(fileName);
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable Long id) {
        uploadedFileService.remove(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getById(@PathVariable Long id) {
        return uploadedFileService.downloadById(id);
    }
}
