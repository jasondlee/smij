package com.steeplesoft.smij.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.steeplesoft.smij.model.PasswordRecovery;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.hibernate.Session;

@RequestScoped
public class PasswordRecoverRepository {
    @Inject
    protected Session session;

    public void addRecoveryToken(String emailAddress, String recoveryCode, OffsetDateTime expirationDate) {
        deleteCodesByUserName(emailAddress);

        PasswordRecovery pr = new PasswordRecovery();
        pr.emailAddress = emailAddress;
        pr.recoveryToken = recoveryCode;
        pr.expiryDate = expirationDate;

        session.persist(pr);
    }

    public void deleteCodesByUserName(String emailAddress) {
        session.createQuery("select p from PasswordRecovery p where p.emailAddress = :emailAddress", PasswordRecovery.class)
            .setParameter("emailAddress", emailAddress)
            .getResultList().forEach(session::remove);
    }

    public Optional<PasswordRecovery> findByEmailAddress(String emailAddress) {
        return session.createQuery("select p from PasswordRecovery p where p.emailAddress = :emailAddress", PasswordRecovery.class)
            .setParameter("emailAddress", emailAddress)
            .uniqueResultOptional();
    }
}
