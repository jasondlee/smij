package com.steeplesoft.simplesec.app.user;

import java.time.OffsetDateTime;

public interface UserFacade {
    Long getId();
    String getUserName();
    String getPassword();
    String getRoles();
    int getFailAttempts();
    OffsetDateTime getLockedUntil();
}
