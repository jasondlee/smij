package com.steeplesoft.simplesec.app;

import com.steeplesoft.simplesec.app.model.PasswordRecovery;
import com.steeplesoft.simplesec.app.model.UserAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@ApplicationScoped
public class UserService {
    @Inject
    protected PasswordValidator passwordValidator;
    @Inject
    protected MessageService messageService;

    public List<UserAccount> getUsers() {
        return UserAccount.listAll();
    }

    public Optional<UserAccount> getUser(long id) {
        return UserAccount.findByIdOptional(id);
    }

    public Optional<UserAccount> getUser(String userName, String password) {
        return UserAccount.find("select u from UserAccount a where u.userName = ?1 and u.password = ?2",
                userName, password).firstResultOptional();
    }

    @Transactional
    public UserAccount saveUser(UserAccount userAccount) {
        List<String> results = passwordValidator.validatePassword(userAccount.password);
        if (results.isEmpty()) {
            userAccount.password = encrypt(userAccount.password);
            userAccount.persist();
        } else {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Invalid password:\n" + String.join("\n", results))
                            .build());
        }

        return userAccount;
    }

    @Transactional
    public String recoverPassword(String emailAddress) {
        String recoveryCode = generateRecoveryCode();

        PasswordRecovery pr = new PasswordRecovery(emailAddress, recoveryCode);

        messageService.sendEmail(emailAddress, "Password Recovery",
                "Your codes is " + recoveryCode);

        pr.persist();

        return "";
    }

    private String encrypt(String password) {
        return password;
    }

    public void deleteUser(Long id) {
        UserAccount.deleteById(id);
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
