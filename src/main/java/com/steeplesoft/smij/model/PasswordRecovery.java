package com.steeplesoft.smij.model;

import java.time.OffsetDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;

@Entity
public class PasswordRecovery extends PanacheEntity {
    public String emailAddress;
    public String recoveryToken;
    public OffsetDateTime expiryDate;

    public static void addRecoveryToken(String emailAddress, String recoveryCode, OffsetDateTime expirationDate) {
        deleteCodesByUserName(emailAddress);

        PasswordRecovery pr = new PasswordRecovery();
        pr.emailAddress = emailAddress;
        pr.recoveryToken = recoveryCode;
        pr.expiryDate = expirationDate;

        pr.persist();
    }

    public static void deleteCodesByUserName(String emailAddress) {
        PasswordRecovery.stream("emailAddress", emailAddress)
            .forEach(PanacheEntityBase::delete);
    }
}
