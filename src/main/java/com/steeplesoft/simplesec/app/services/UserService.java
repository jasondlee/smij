package com.steeplesoft.simplesec.app.services;

import static com.steeplesoft.simplesec.app.model.jooq.Sequences.USER_ACCOUNT_SEQ;
import static io.quarkus.runtime.util.HashUtil.sha256;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.steeplesoft.simplesec.app.DSLContextProvider;
import com.steeplesoft.simplesec.app.exception.LockedAccountException;
import com.steeplesoft.simplesec.app.exception.UserExistsException;
import com.steeplesoft.simplesec.app.model.jooq.Sequences;
import com.steeplesoft.simplesec.app.model.jooq.tables.daos.JwtMetadataDao;
import com.steeplesoft.simplesec.app.model.jooq.tables.daos.PasswordRecoveryDao;
import com.steeplesoft.simplesec.app.model.jooq.tables.daos.UserAccountDao;
import com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata;
import com.steeplesoft.simplesec.app.model.jooq.tables.pojos.PasswordRecovery;
import com.steeplesoft.simplesec.app.model.jooq.tables.pojos.UserAccount;
import com.steeplesoft.simplesec.app.payload.LoginInput;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.PostConstruct;
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
import org.jooq.DSLContext;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;

@ApplicationScoped
public class UserService {
    @Inject
    JsonWebToken jwt;

    @Inject
    protected JwtMetadataDao jwtMetadataDao;

    @Inject
    protected UserAccountDao userAccountDao;

    @Inject
    protected PasswordRecoveryDao passwordRecoveryDao;

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
    private DSLContext context;

    @PostConstruct
    public void init() {
        this.context = DSLContextProvider.getContext();
    }

    public List<UserAccount> getUsers() {
        List<UserAccount> all = userAccountDao.findAll();
        all.sort(Comparator.comparing(UserAccount::getId));
        return all;
    }

    public Optional<UserAccount> getUser(long id) {
        return userAccountDao.fetchOptionalById(id);
    }

    public Optional<UserAccount> getUser(String userName) {
        return userAccountDao.fetchOptionalByUserName(userName);
    }

    public UserAccount getCurrentUser() {
        UserAccount user = userAccountDao.fetchOptionalById(Long.parseLong(jwt.getSubject()))
                .orElseThrow(() -> new ServerErrorException("Unable to determine current user",
                        Response.Status.INTERNAL_SERVER_ERROR));
        checkIfUserLocked(user);

        return user;
    }

    @Transactional
    public UserAccount saveUser(UserAccount user) {
        if (userAccountDao.fetchOptionalByUserName(user.getUserName()).isPresent()) {
            throw new UserExistsException();
        }

        // TODO: Check to see if password has changed
        validatePassword(user);
        userAccountDao.insert(user);

        return user;
    }

    @Transactional
    public void updateUser(Long id, UserAccount userAccount) {
        UserAccount existing = userAccountDao.fetchOptionalById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(userAccount.getId(), existing.getId())) {
            throw new BadRequestException();
        }

        if (!existing.getUserName().equals(userAccount.getUserName()) &&
                userAccountDao.fetchOptionalByUserName(userAccount.getUserName()).isPresent()) {
            throw new UserExistsException();
        }

        if (!Objects.equals(userAccount.getPassword(), existing.getPassword())) {
            validatePassword(userAccount);
        }
        userAccountDao.update(userAccount);
    }

    @Transactional
    public String login(LoginInput loginInfo) {
        UserAccount user = userAccountDao.fetchOptionalByUserName(loginInfo.userName)
                .orElseThrow(AuthenticationFailedException::new);
        checkIfUserLocked(user);

        if (!hashString(loginInfo.password).equals(user.getPassword())) {
            System.err.println("user = " + user);
            System.err.println("user.getFailAttempts() = " + user.getFailAttempts());
            Integer failAttempts = user.getFailAttempts();
            failAttempts = (failAttempts != null) ? failAttempts + 1 : 1;
            user.setFailAttempts(failAttempts);
            boolean locked = failAttempts >= maxAttempts;
            if (locked) {
                user.setLockedUntil(OffsetDateTime.now().plusMinutes(lockDuration).toEpochSecond());
            }
            userAccountDao.update(user);
            System.out.println(userAccountDao.fetchOptionalByUserName(loginInfo.userName).get());
            return null;
        } else {
            jwtMetadataDao.fetchByUserName(loginInfo.userName)
                    .forEach(m -> jwtMetadataDao.delete(m));
            return generateJwt(user);
        }
    }

    @Transactional
    public void generatePasswordRecoveryCode(String emailAddress) {
        String recoveryCode = generateRecoveryCode();

        messageService.sendEmail(emailAddress, "Password Recovery", "Your code is " + recoveryCode);

        PasswordRecovery pr = new PasswordRecovery();
        pr.setUserName(emailAddress);
        pr.setRecoveryToken(recoveryCode);
        pr.setExpiryDate(OffsetDateTime.now().plusMinutes(5).toEpochSecond());
        passwordRecoveryDao.insert(pr);
    }

    @Transactional
    public void recoverPassword(String emailAddress, String recoveryCode, String newPassword1, String newPassword2) {
        if (!newPassword1.equals(newPassword2)) {
            throw new BadRequestException("Passwords do not match");
        }

        List<PasswordRecovery> tokens = passwordRecoveryDao.fetchByUserName(emailAddress);
        if (tokens != null) {
            tokens.sort(Comparator.comparing(PasswordRecovery::getExpiryDate));
            String token = tokens.getLast().getRecoveryToken();
            if (!token.equals(recoveryCode)) {
                throw new BadRequestException();
            }
        }

        UserAccount user = userAccountDao.fetchOptionalByUserName(emailAddress)
                .orElseThrow(NotFoundException::new);
        user.setPassword(hashString(newPassword1));
        user.setLockedUntil(null);
        user.setFailAttempts(0);
        userAccountDao.update(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userAccountDao.deleteById(id);
    }

    private void validatePassword(UserAccount user) {
        String password = user.getPassword();
        if (password != null) {
            List<String> results = passwordValidator.validatePassword(password);
            if (results.isEmpty()) {
                user.setPassword(hashString(password));
            } else {
                System.err.println("password = " + password);
                System.err.println("results = " + results);
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity("Invalid password:\n" + String.join("\n", results))
                                .build());
            }
        }
    }

    private String generateJwt(UserAccount user) {
        String userName = user.getUserName();
        String token = Jwt.issuer(issuer)
                .subject(userName)
                .upn(userName)
                .expiresIn(Duration.ofMinutes(tokenDuration))
                .groups(Set.of(user.getRoles().split(",")))
                .sign();

        if (revocationSupport) {
            try {
                JwtClaims claims = JwtClaims.parse(new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]),
                        StandardCharsets.UTF_8));
                jwtMetadataDao.insert(
                        new JwtMetadata(claims.getJwtId(),
                                userName,
                                claims.getExpirationTime().getValueInMillis(),
                                false));
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

    private void checkIfUserLocked(UserAccount user) {
        Long lockedUntil = user.getLockedUntil();
        if (lockedUntil != null && lockedUntil > System.currentTimeMillis()) {
            throw new LockedAccountException();
        }
    }
}
