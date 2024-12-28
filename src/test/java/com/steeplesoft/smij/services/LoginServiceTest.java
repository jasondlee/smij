package com.steeplesoft.smij.services;

import static com.steeplesoft.smij.DummyUserSupplier.PASSWORD_BAD;
import static com.steeplesoft.smij.DummyUserSupplier.PASSWORD_GOOD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.steeplesoft.smij.DummyUser;
import com.steeplesoft.smij.DummyUserSupplier;
import com.steeplesoft.smij.payload.LoginInput;
import com.steeplesoft.smij.user.UserFacade;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LoginServiceTest {
    @Inject
    protected LoginService loginService;

    @Inject
    protected DummyUserSupplier userSupplier;

    @ConfigProperty(name = "smij.maxAttempts", defaultValue = "5")
    protected int maxAttempts;

    private DummyUser newUser(String userName, String password) {
        return new DummyUser(userName, password, "USER");
    }

    @Test
    public void loginValidUser() {
        LoginInput loginInput = new LoginInput("admin@example.com", PASSWORD_GOOD);
        assertThat(loginService.login(loginInput), notNullValue());
    }

    @Test
    public void loginValidUserWrongPassword() {
        LoginInput loginInput = new LoginInput("admin@example.com", PASSWORD_BAD);
        assertNull(loginService.login(loginInput));
    }

    @Test
    public void lockUserAfterFailAttempts() {
        UserFacade user = userSupplier.saveUser(newUser(UUID.randomUUID() + "@example.com", PASSWORD_GOOD));

        LoginInput loginInput = new LoginInput(user.getUserName(), PASSWORD_BAD);
        for (int i = 0; i < maxAttempts; i++) {
            try {
                loginService.login(loginInput);
            } catch (AuthenticationFailedException e) {
                //
            }
        }

        user = userSupplier.getUser(loginInput.userName).get();
        assertNotNull(user.getLockedUntil());
    }

    @Test
    public void loginLockedUser() {
        UserFacade user = userSupplier.saveUser(newUser(UUID.randomUUID() + "@example.com", PASSWORD_GOOD));
        userSupplier.updateLockAttempts(user, Integer.MAX_VALUE, OffsetDateTime.MAX);

        LoginInput loginInput = new LoginInput(user.getUserName(), PASSWORD_GOOD);

        user = userSupplier.getUser(loginInput.userName).get();
        assertNotNull(user.getLockedUntil());
    }
}
