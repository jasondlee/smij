package com.steeplesoft.simplesec.app.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.steeplesoft.simplesec.app.model.jooq.tables.pojos.UserAccount;
import com.steeplesoft.simplesec.app.payload.LoginInput;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
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

    private UserAccount newUser(String userName, String password) {
        UserAccount user = new UserAccount();
        user.setUserName(userName);
        user.setPassword(password);
        
        return user;
    }

    @Test
    public void testSaveValidUser() {
        UserAccount savedUser = userService.saveUser(newUser("testUser", PASSWORD_GOOD));
        assertThat(savedUser.getId(), notNullValue());
    }

    @Test
    public void testSaveInvalidUser() {
        assertThrows(WebApplicationException.class,
                () -> userService.saveUser(newUser("testUser", PASSWORD_BAD)));
    }

    @Test
    public void createNewUser() {
        UserAccount newUser = userService.saveUser(newUser(UUID.randomUUID() + "@example.com", PASSWORD_GOOD));
        assertThat(newUser.getId(), notNullValue());
    }

    @Test
    public void createNewUserBadPassword() {
        assertThrows(WebApplicationException.class,
                () -> userService.saveUser(newUser(UUID.randomUUID() + "@example.com", PASSWORD_BAD)));
    }

    @Test
    public void createNewUserExistingUser() {
        assertThrows(WebApplicationException.class,
                () -> userService.saveUser(newUser("admin@example.com", PASSWORD_GOOD)));
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
        UserAccount user = userService.saveUser(newUser(UUID.randomUUID() + "@example.com", PASSWORD_GOOD));

        LoginInput loginInput = new LoginInput(user.getUserName(), PASSWORD_BAD);
        for (int i = 0; i < maxAttempts; i++) {
            try {
                userService.login(loginInput);
            } catch (AuthenticationFailedException e) {
                //
            }
        }

        user = userService.getUser(loginInput.userName).get();
        assertNotNull(user.getLockedUntil());
    }

    @Test
    public void loginLockedUser() {
        UserAccount user = userService.saveUser(newUser(UUID.randomUUID() + "@example.com", PASSWORD_GOOD));
        user.setLockedUntil(OffsetDateTime.MAX.toEpochSecond());
        userService.updateUser(user.getId(), user);

        LoginInput loginInput = new LoginInput(user.getUserName(), PASSWORD_GOOD);

        user = userService.getUser(loginInput.userName).get();
        assertNotNull(user.getLockedUntil());
    }
}
