package com.sclad.scladapp.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED, reason = "Device cannot be deleted! Relations found in other tables.")
public class DeviceCouldNotBeDeletedException extends RuntimeException {
}
