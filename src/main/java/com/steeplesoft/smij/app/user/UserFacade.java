package com.steeplesoft.smij.app.user;

import java.time.OffsetDateTime;

public interface UserFacade {
    Long getId();
    Long getTenantId();
    String getUserName();
    String getPassword();
    String getRoles();
    int getFailAttempts();
    OffsetDateTime getLockedUntil();
}
