package com.steeplesoft.simplesec.app.exception;

import jakarta.ws.rs.BadRequestException;

public class UserExistsException extends BadRequestException {
    public UserExistsException() {
        super("User already exists");
    }
}
