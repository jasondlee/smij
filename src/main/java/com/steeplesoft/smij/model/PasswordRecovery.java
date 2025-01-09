package com.steeplesoft.smij.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class PasswordRecovery  {
    @Id
    @GeneratedValue
    public Long id;
    public String emailAddress;
    public String recoveryToken;
    public OffsetDateTime expiryDate;
}
