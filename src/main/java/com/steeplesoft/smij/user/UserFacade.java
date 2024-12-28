package com.steeplesoft.smij.user;

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
