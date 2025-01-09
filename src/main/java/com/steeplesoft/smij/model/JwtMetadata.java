package com.steeplesoft.smij.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class JwtMetadata {
    @Id
    public String id;
    public String emailAddress;
    public OffsetDateTime expiryDate;
    public boolean revoked;
}
