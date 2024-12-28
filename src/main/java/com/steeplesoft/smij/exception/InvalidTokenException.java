package com.steeplesoft.smij.exception;

import jakarta.ws.rs.NotAuthorizedException;

public class InvalidTokenException extends NotAuthorizedException {
    public InvalidTokenException() {
        super("Invalid token");
    }
}
