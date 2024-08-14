package com.steeplesoft.simplesec.app.services;

import static io.quarkus.runtime.util.HashUtil.sha256;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.steeplesoft.simplesec.app.model.JwtMetadata;
import com.steeplesoft.simplesec.app.model.PasswordRecovery;
import com.steeplesoft.simplesec.app.model.User;
import com.steeplesoft.simplesec.app.payload.LoginInput;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;

// TODOs:
// - JWT caching and validation
//   - In-memory
//   - Database ?
// - JWT invalidation
// - Override JWT verification to check cache

@ApplicationScoped
public class UserService {
    @Inject
    JsonWebToken jwt;

    @Inject
    protected PasswordValidator passwordValidator;
    @Inject
    protected MessageService messageService;

    @ConfigProperty(name = "simplesec.salt", defaultValue = "default")
    protected String passwordSalt;
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://steeplesoft.com/simplesec")
    protected String issuer;
    @ConfigProperty(name = "simplesec.token.duration", defaultValue = "1440")
    protected int tokenDuration;
    @ConfigProperty(name = "simplesec.maxAttempts", defaultValue = "5")
    protected int maxAttempts;
    @ConfigProperty(name = "simplesec.lockDuration", defaultValue = "5")
    protected int lockDuration;
    @ConfigProperty(name = "simplesec.jwt.revocation.support", defaultValue = "false")
    protected boolean revocationSupport;

    public List<User> getUsers() {
        return User.listAll();
    }

    public Optional<User> getUser(long id) {
        return User.findByIdOptional(id);
    }

    public User getCurrentUser() {
        User user = getUser(jwt.getSubject())
                .orElseThrow(() -> new ServerErrorException("Unable to determine current user",
                        Response.Status.INTERNAL_SERVER_ERROR));
        checkIfUserLocked(user);

        return user;
    }

    @Transactional
    public User saveUser(User user) {
        // TODO: Check to see if password has changed
        validatePassword(user);
        user.persist();

        return user;
    }

    private void validatePassword(User user) {
        if (user.password != null) {
            List<String> results = passwordValidator.validatePassword(user.password);
            if (results.isEmpty()) {
                user.password = hashString(user.password);
            } else {
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity("Invalid password:\n" + String.join("\n", results))
                                .build());
            }
        }
    }

    @Transactional
    public void updateUser(Long id, User userAccount) {
        User existing = getUser(id).orElseThrow(NotFoundException::new);

        if (!userAccount.id.equals(existing.id)) {
            throw new BadRequestException();
        }

        validatePassword(userAccount);
        User.getEntityManager().merge(userAccount);
    }

    @Transactional
    public User createUser(User user) {
        if (User.find("select u from User u where u.userName = ?1", user.userName).count() != 0) {
            throw new BadRequestException("User already exists");
        }

        return saveUser(user);
    }

    @Transactional
    public String login(LoginInput loginInfo) {
        User user = getUser(loginInfo.userName)
                .orElseThrow(AuthenticationFailedException::new);
        checkIfUserLocked(user);

        if (!hashString(loginInfo.password).equals(user.password)) {
            user.failAttempts += 1;
            if (user.failAttempts >= maxAttempts) {
                user.lockedUntil = LocalDateTime.now().plusMinutes(lockDuration);
            }
            user.persistAndFlush();
            return null;
        } else {
            return generateJwt(user);
        }
    }

    private static Optional<User> getUser(String userName) {
        Optional<User> user = Optional.empty();
        try {
            user = User.find("select u from User u where u.userName = ?1", userName)
                    .firstResultOptional();
        } catch (NotFoundException e) {
            System.out.println("hi");
        }
        return user;
    }

    @Transactional
    public void generatePasswordRecoveryCode(String emailAddress) {
        String recoveryCode = generateRecoveryCode();

        messageService.sendEmail(emailAddress, "Password Recovery", "Your code is " + recoveryCode);

        new PasswordRecovery(emailAddress, recoveryCode).persist();
    }

    @Transactional
    public void recoverPassword(String emailAddress, String recoveryCode, String newPassword1, String newPassword2) {
        if (!newPassword1.equals(newPassword2)) {
            throw new BadRequestException("Passwords do not match");
        }

        PasswordRecovery
                .find("select pr from PasswordRecovery pr where pr.userName = ?1 and pr.recoveryToken = ?2 and expiryDate > ?3",
                        emailAddress, recoveryCode, LocalDateTime.now())
                .firstResultOptional()
                .orElseThrow(BadRequestException::new);

        User user = (User) User.find("select u from User u where u.userName = ?1", emailAddress)
                .firstResultOptional()
                .orElseThrow(NotFoundException::new);

        user.password = hashString(newPassword1);
        user.persist();
    }

    @Transactional
    public void deleteUser(Long id) {
        User.deleteById(id);
    }

    private String generateJwt(User user) {
        String token = Jwt.issuer(issuer)
                .subject(user.userName)
                .upn(user.userName)
                .expiresIn(Duration.ofMinutes(tokenDuration))
                .groups(Set.of(user.roles.split(",")))
                .sign();

        if (revocationSupport) {
            try {
                JwtClaims claims = JwtClaims.parse(new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8));
                JwtMetadata metadata = new JwtMetadata();
                metadata.id = claims.getJwtId();
                metadata.expiresAt = claims.getExpirationTime().getValueInMillis();
                metadata.persist();
            } catch (InvalidJwtException | MalformedClaimException e) {
                throw new RuntimeException(e);
            }
        }

        return token;
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

    private void checkIfUserLocked(User user) {
        if (user.lockedUntil != null) {
            throw new AuthenticationFailedException("User account is locked. Please try waiting or resetting password");
        }
    }
}
