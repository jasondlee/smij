package com.steeplesoft.simplesec.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import com.steeplesoft.simplesec.app.model.User;
import com.steeplesoft.simplesec.app.payload.LoginInput;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserServiceTest {
    public static final String PASSWORD_GOOD = "#1@2NowIsTheTime";
    public static final String PASSWORD_BAD = "bad password";
    @Inject
    protected UserService userService;
    @ConfigProperty(name = "simplesec.maxAttempts", defaultValue = "5")
    protected int maxAttempts;


    @Test
    public void testSaveValidUser() {
        User savedUser = userService.createUser(new User("testUser", PASSWORD_GOOD));
        assertThat(savedUser.id, notNullValue());
    }

    @Test
    public void testSaveInvalidUser() {
        assertThrows(WebApplicationException.class,
                () -> userService.createUser(new User("testUser", PASSWORD_BAD)));
    }

    @Test
    public void createNewUser() {
        User newUser = userService.createUser(new User(UUID.randomUUID() + "@example.com", PASSWORD_GOOD));
        assertThat(newUser.id, notNullValue());
    }

    @Test
    public void createNewUserBadPassword() {
        assertThrows(WebApplicationException.class,
                () -> userService.createUser(new User(UUID.randomUUID() + "@example.com", PASSWORD_BAD)));
    }

    @Test
    public void createNewUserExistingUser() {
        assertThrows(WebApplicationException.class,
                () -> userService.createUser(new User("admin@example.com", PASSWORD_GOOD)));
    }

    @Test
    public void loginValidUser() {
        LoginInput loginInput = new LoginInput("admin@example.com", PASSWORD_GOOD);
        assertThat(userService.login(loginInput), notNullValue());
    }

    @Test
    public void loginValidUserWrongPassword() {
        LoginInput loginInput = new LoginInput("admin@example.com", PASSWORD_BAD);
        assertNull(userService.login(loginInput));
    }

    @Test
    public void lockUserAfterFailAttempts() {
        LoginInput loginInput = new LoginInput("user@example.com", PASSWORD_BAD);
        for (int i = 0; i < maxAttempts; i++) {
            userService.login(loginInput);
        }
        assertThrows(AuthenticationFailedException.class, () -> userService.login(loginInput));
    }

    @Test
    public void loginLockedUser() {
        LoginInput loginInput = new LoginInput("locked@example.com", PASSWORD_GOOD);
        assertThrows(AuthenticationFailedException.class, () -> userService.login(loginInput));
    }
}
