package com.steeplesoft.simplesec.app.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_recovery")
public class PasswordRecovery extends PanacheEntity {
    public String userName;
    public String recoveryToken;
    public LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(5);

    public PasswordRecovery() {
    }

    public PasswordRecovery(String userName, String recoveryToken) {
        this.userName = userName;
        this.recoveryToken = recoveryToken;
    }
}
