package com.steeplesoft.smij.app;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.steeplesoft.smij.app.user.UserFacade;
import com.steeplesoft.smij.app.user.UserSupplier;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class DummyUserSupplier implements UserSupplier {
    public static final String PASSWORD_GOOD = "#1@2NowIsTheTime";
    public static final String PASSWORD_BAD = "bad password";
    private long idSequence = 0;
    private Map<Long, UserFacade> users = new HashMap<>();

    @PostConstruct
    public void init() {
        saveUser(new DummyUser("admin@example.com", hashString(PASSWORD_GOOD), "ADMIN"));
        saveUser(new DummyUser("admin2@example.com", hashString(PASSWORD_GOOD), "ADMIN,USER"));
    }

    @Override
    public Optional<UserFacade> fetchOptionalById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<UserFacade> fetchOptionalByUserName(String userName) {
        return users.values().stream().filter(u -> {
            return u.getUserName().equals(userName);
        }).findFirst();
    }

    @Override
    public void updateLockAttempts(UserFacade user, int failAttempts, OffsetDateTime lockedUntil) {
        fetchOptionalById(user.getId()).ifPresent(u -> {
            ((DummyUser)u).setFailAttempts(failAttempts);
            ((DummyUser)u).setLockedUntil(lockedUntil);
        });
    }

    @Override
    public void resetPassword(String emailAddress, String password, int failAttempts, OffsetDateTime lockedUntil) {
        fetchOptionalByUserName(emailAddress).ifPresent(u -> {
            DummyUser d = (DummyUser)u;
            d.setPassword(hashString(password));
            d.setFailAttempts(failAttempts);
            d.setLockedUntil(lockedUntil);
        });
    }

    public UserFacade saveUser(DummyUser userFacade) {
        userFacade.setId(idSequence++);
        users.put(userFacade.getId(), userFacade);
        return userFacade;
    }

    public Optional<UserFacade> getUser(String emailAddress) {
        return fetchOptionalByUserName(emailAddress);
    }
}
