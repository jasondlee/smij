package com.steeplesoft.smij.model;

import java.time.OffsetDateTime;
import java.util.Optional;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class JwtMetadata extends PanacheEntityBase {
    @Id
    public String id;
    public String emailAddress;
    public OffsetDateTime expiryDate;
    public boolean revoked;

    public static Optional<JwtMetadata> fetchOptionalById(String id) {
        return find("id", id).firstResultOptional();
    }

    public static void addToken(String jti, String emailAddress, OffsetDateTime expiresAt, boolean revoked) {
        JwtMetadata metadata = new JwtMetadata();
        metadata.id = jti;
        metadata.emailAddress = emailAddress;
        metadata.expiryDate = expiresAt;
        metadata.revoked = revoked;
        metadata.persist();
    }

    public static void deleteTokensForUser(String emailAddress) {
        JwtMetadata.stream("emailAddress", emailAddress)
                .forEach(PanacheEntityBase::delete);
    }
}
