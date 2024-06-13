package com.steeplesoft.simplesec.app;

import static io.quarkus.runtime.util.HashUtil.sha256;

import com.steeplesoft.simplesec.app.model.PasswordRecovery;
import com.steeplesoft.simplesec.app.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

// TODOs:
// - JWT caching and validation
//   - In-memory
//   - Database ?
// - JWT invalidation
// - Override JWT verification to check cache

@ApplicationScoped
public class UserService {
    @Inject
    protected PasswordValidator passwordValidator;
    @Inject
    protected MessageService messageService;

    @ConfigProperty(name = "simplesec.salt", defaultValue = "default")
    protected String passwordSalt;

    public List<User> getUsers() {
        return User.listAll();
    }

    public Optional<User> getUser(long id) {
        return User.findByIdOptional(id);
    }

    public Optional<User> getUser(String userName, String password) {
        return User.find("select u from User u where u.userName = ?1 and u.password = ?2",
                        userName, sha256(password))
                .firstResultOptional();
    }

    @Transactional
    public User saveUser(User user) {
        List<String> results = passwordValidator.validatePassword(user.password);
        if (results.isEmpty()) {
            user.password = hashString(user.password);
            user.persist();
        } else {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Invalid password:\n" + String.join("\n", results))
                            .build());
        }

        return user;
    }

    @Transactional
    public String generatePasswordRecoveryCode(String emailAddress) {
        String recoveryCode = generateRecoveryCode();

        messageService.sendEmail(emailAddress, "Password Recovery",
                "Your codes is " + recoveryCode);

        new PasswordRecovery(emailAddress, recoveryCode).persist();

        return recoveryCode;
    }

    @Transactional
    public void recoverPassword(String emailAddress, String recoveryCode, String newPassword1, String newPassword2) {
        if (!newPassword1.equals(newPassword2)) {
            throw new BadRequestException("Passwords do not match");
        }

        PasswordRecovery pr = (PasswordRecovery) PasswordRecovery
                .find("select p from ValidRecoveryToken pr where pr.emailAddress = ?1 and pr.recovertyToken = ?2 and expiryDate <= ?3",
                        emailAddress, recoveryCode, LocalDateTime.now())
                .firstResultOptional()
                .orElseThrow(BadRequestException::new);

        User user = (User) User.find("select u from User a where a.userName = ?1", newPassword1)
                .firstResultOptional()
                .orElseThrow(NotFoundException::new);

        user.password = hashString(newPassword1);
        user.persistAndFlush();
    }

    public void deleteUser(Long id) {
        User.deleteById(id);
    }

    private String hashString(String password) {
        return sha256(password + passwordSalt);
    }

    private String generateRecoveryCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;

        return new Random()
                .ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
                .toUpperCase();
    }
}
