package com.sclad.scladapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Provided file is invalid or has invalid filename.")
public class FileStorageException extends RuntimeException {

    public FileStorageException(String exception) {
        super(exception);
    }
}
