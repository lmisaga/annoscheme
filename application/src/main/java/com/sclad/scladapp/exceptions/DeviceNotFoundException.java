package com.sclad.scladapp.exceptions;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(Long id) {
        super("Could not find device " + id);
    }
}
