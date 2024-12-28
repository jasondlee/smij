package com.steeplesoft.smij.app.user;

import static io.quarkus.runtime.util.HashUtil.sha256;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;

public interface UserSupplier {
    Optional<UserFacade> fetchOptionalById(Long id);
    Optional<UserFacade> fetchOptionalByUserName(String userName);
    void updateLockAttempts(UserFacade user, int failAttempts, OffsetDateTime lockedUntil);
    void resetPassword(String emailAddress, String password, int failAttempts, OffsetDateTime lockedUntil);

    default String hashString(String password) {
        String passwordSalt = ConfigProvider.getConfig()
                .getOptionalValue("smij.salt", String.class)
                .orElse("default");
        return sha256(password + passwordSalt);
    }
}
