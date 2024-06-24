package com.steeplesoft.simplesec.app.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_recovery")
public class PasswordRecovery extends PanacheEntity {
    public String username;
    public String recoveryToken;
    public LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(5);

    public PasswordRecovery() {
    }

    public PasswordRecovery(String username, String recoveryToken) {
        this.username = username;
        this.recoveryToken = recoveryToken;
    }
}
