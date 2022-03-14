package com.sclad.scladapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "File was not found.")
public class UploadedFileNotFoundException extends RuntimeException {
}
