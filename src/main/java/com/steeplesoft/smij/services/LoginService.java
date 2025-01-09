package com.steeplesoft.smij.services;

import static com.steeplesoft.smij.Constants.CLAIM_TENANT;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.steeplesoft.smij.exception.InvalidTokenException;
import com.steeplesoft.smij.exception.LockedAccountException;
import com.steeplesoft.smij.model.JwtMetadata;
import com.steeplesoft.smij.model.PasswordRecovery;
import com.steeplesoft.smij.payload.LoginInput;
import com.steeplesoft.smij.repository.JwtMetadataRepository;
import com.steeplesoft.smij.repository.PasswordRecoverRepository;
import com.steeplesoft.smij.user.UserFacade;
import com.steeplesoft.smij.user.UserSupplier;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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

//    @Inject
//    protected PasswordRecoveryDao passwordRecoveryDao;
//
//    @Inject
//    protected JwtMetadataDao jwtMetadataDao;

    @Inject
    protected PasswordValidator passwordValidator;

    @Inject
    protected MessageService messageService;

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://steeplesoft.com/smij")
    protected String issuer;
    @ConfigProperty(name = "smij.token.duration", defaultValue = "1440")
    protected int tokenDuration;
    @ConfigProperty(name = "smij.maxAttempts", defaultValue = "5")
    protected int maxAttempts;
    @ConfigProperty(name = "smij.lockDuration", defaultValue = "5")
    protected int lockDuration;
    @ConfigProperty(name = "smij.jwt.revocation.support", defaultValue = "false")
    protected boolean revocationSupport;

    @Inject
    @Claim(CLAIM_TENANT)
    protected Long tenantId;

    @Inject
    JwtMetadataRepository jwtMetadataRepository;

    @Inject
    PasswordRecoverRepository prRepository;

    @PostConstruct
    public void init() {
        if (userSupplierInstance.isResolvable()) {
            userSupplier = userSupplierInstance.get();
        } else {
            throw new RuntimeException("UserSupplier is not resolvable");
        }
    }

    @Transactional
    public String login(@Valid LoginInput loginInfo) {
        UserFacade user = userSupplier.fetchOptionalByUserName(loginInfo.userName)
                .orElseThrow(AuthenticationFailedException::new);
        checkIfUserLocked(user);

        if (!userSupplier.hashString(loginInfo.password).equals(user.getPassword())) {
            int failAttempts = user.getFailAttempts() + 1;
            OffsetDateTime lockedUntil = (failAttempts >= maxAttempts) ?
                OffsetDateTime.now().plusMinutes(lockDuration) : null;

            userSupplier.updateLockAttempts(user, failAttempts, lockedUntil);
            return null;
        } else {
            jwtMetadataRepository.deleteTokensForUser(loginInfo.userName);
            return generateJwt(user);
        }
    }

    @Transactional
    public void generatePasswordRecoveryCode(String emailAddress) {
        final String recoveryCode = generateRecoveryCode();

        messageService.sendEmail(emailAddress, "Password Recovery", "Your code is " + recoveryCode);
        prRepository.addRecoveryToken(emailAddress, recoveryCode, OffsetDateTime.now().plusMinutes(5));
    }

    @Transactional
    public void recoverPassword(String emailAddress,
                                @NotNull String recoveryCode,
                                String newPassword1,
                                String newPassword2) {
        if (!newPassword1.equals(newPassword2)) {
            throw new BadRequestException("Passwords do not match");
        }

        Optional<PasswordRecovery> optionalPr = prRepository.findByEmailAddress(emailAddress);

        if (optionalPr.isEmpty()) {
            throw new InvalidTokenException();
        }
        PasswordRecovery pr = optionalPr.get();
        if (!Objects.equals(pr.recoveryToken, recoveryCode)) {
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
                .claim(CLAIM_TENANT, user.getTenantId())
                .groups(Set.of(getOrDefault(user.getRoles(), "").split(",")))
                .sign();

        if (revocationSupport) {
            jwtMetadataRepository.addToken(jti, userName, expiresAt, false);
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
