package com.steeplesoft.simplesec.app.exception;

import io.quarkus.security.AuthenticationFailedException;
import jakarta.ws.rs.NotAuthorizedException;

public class LockedAccountException extends NotAuthorizedException {
    public LockedAccountException() {
        super("User account is locked. Please try waiting or resetting password");
    }
}
