package com.steeplesoft.simplesec.app.services;

import static com.steeplesoft.simplesec.app.Constants.CLAIM_TENANT;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.steeplesoft.simplesec.app.dao.JwtMetadataDao;
import com.steeplesoft.simplesec.app.dao.PasswordRecoveryDao;
import com.steeplesoft.simplesec.app.exception.InvalidTokenException;
import com.steeplesoft.simplesec.app.exception.LockedAccountException;
import com.steeplesoft.simplesec.app.model.jooq.tables.pojos.PasswordRecovery;
import com.steeplesoft.simplesec.app.payload.LoginInput;
import com.steeplesoft.simplesec.app.user.UserFacade;
import com.steeplesoft.simplesec.app.user.UserSupplier;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

@RequestScoped
public class LoginService {
    @Inject
    protected Instance<UserSupplier> userSupplierInstance;
    protected UserSupplier userSupplier;

    @Inject
    protected PasswordRecoveryDao passwordRecoveryDao;

    @Inject
    protected JwtMetadataDao jwtMetadataDao;

    @Inject
    protected PasswordValidator passwordValidator;

    @Inject
    protected MessageService messageService;

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

    @Inject
    @Claim(CLAIM_TENANT)
    protected Long tenantId;

    @PostConstruct
    public void init() {
        if (userSupplierInstance.isResolvable()) {
            userSupplier = userSupplierInstance.get();
        } else {
            throw new RuntimeException("UserSupplier is not resolvable");
        }
    }

    @Transactional
    public String login(LoginInput loginInfo) {
        UserFacade user = userSupplier.fetchOptionalByUserName(loginInfo.userName)
                .orElseThrow(AuthenticationFailedException::new);
        checkIfUserLocked(user);

        if (!userSupplier.hashString(loginInfo.password).equals(user.getPassword())) {
            int failAttempts = user.getFailAttempts() + 1;
            OffsetDateTime lockedUntil = (failAttempts >= maxAttempts) ?
                lockedUntil = OffsetDateTime.now().plusMinutes(lockDuration) : null;

            userSupplier.updateLockAttempts(user, failAttempts, lockedUntil);
            return null;
        } else {
            jwtMetadataDao.deleteTokensForUser(loginInfo.userName);
            return generateJwt(user);
        }
    }

    @Transactional
    public void generatePasswordRecoveryCode(String emailAddress) {
        final String recoveryCode = generateRecoveryCode();

        messageService.sendEmail(emailAddress, "Password Recovery", "Your code is " + recoveryCode);
        passwordRecoveryDao.addRecoveryToken(emailAddress, recoveryCode, OffsetDateTime.now().plusMinutes(5));
    }

    @Transactional
    public void recoverPassword(String emailAddress,
                                @NotNull String recoveryCode,
                                String newPassword1,
                                String newPassword2) {
        if (!newPassword1.equals(newPassword2)) {
            throw new BadRequestException("Passwords do not match");
        }

        PasswordRecovery pr = passwordRecoveryDao.fetchByUserName(emailAddress)
                .orElseThrow(InvalidTokenException::new);
        if (!Objects.equals(pr.getRecoveryToken(), recoveryCode)) {
            throw new InvalidTokenException();
        }

        userSupplier.resetPassword(emailAddress, newPassword1, 0, null);
    }

/*
    private void validatePassword(UserAccount user) {
        String password = user.getPassword();
        if (password != null) {
            List<String> results = passwordValidator.validatePassword(password);
            if (results.isEmpty()) {
                user.setPassword(hashString(password));
            } else {
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity("Invalid password:\n" + String.join("\n", results))
                                .build());
            }
        }
    }
*/

    private String generateJwt(UserFacade user) {
        final String userName = user.getUserName();
        final OffsetDateTime issuedAt = OffsetDateTime.now();
        final OffsetDateTime expiresAt = issuedAt.plusMinutes(tokenDuration);
        final String jti = UUID.randomUUID().toString();

        String token = Jwt.issuer(issuer)
                .subject(userName)
                .upn(userName)
                .issuedAt(issuedAt.toEpochSecond())
                .expiresAt(expiresAt.toEpochSecond())
                .claim(Claims.jti, jti)
                .claim(CLAIM_TENANT, tenantId != null ? tenantId : "")
                .groups(Set.of(getOrDefault(user.getRoles(), "").split(",")))
                .sign();

        if (revocationSupport) {
            jwtMetadataDao.addToken(jti, userName, expiresAt, false);
        }

        return token;
    }

    private <T> T getOrDefault(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    private String generateRecoveryCode() {
        return new Random()
                .ints(48, 123)// '0' to 'z'
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(6) // six char length
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
                .toUpperCase();
    }

    private void checkIfUserLocked(UserFacade user) {
        long lockedUntil = (user.getLockedUntil() != null) ? user.getLockedUntil().toEpochSecond() : 0;
        if (lockedUntil > System.currentTimeMillis()) {
            throw new LockedAccountException();
        }
    }
}
