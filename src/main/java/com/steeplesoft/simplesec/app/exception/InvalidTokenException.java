package com.steeplesoft.simplesec.app.exception;

import jakarta.ws.rs.NotAuthorizedException;

public class InvalidTokenException extends NotAuthorizedException {
    public InvalidTokenException() {
        super("Invalid token");
    }
}
