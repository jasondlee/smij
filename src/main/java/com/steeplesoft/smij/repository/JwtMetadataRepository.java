package com.steeplesoft.smij.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.steeplesoft.smij.model.JwtMetadata;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.hibernate.Session;

@RequestScoped
public class JwtMetadataRepository {
    @Inject
    protected Session session;

    public void addToken(String jti, String emailAddress, OffsetDateTime expiresAt, boolean revoked) {
        JwtMetadata metadata = new JwtMetadata();
        metadata.id = jti;
        metadata.emailAddress = emailAddress;
        metadata.expiryDate = expiresAt;
        metadata.revoked = revoked;
        session.persist(metadata);
    }

    public void deleteTokensForUser(String emailAddress) {
        session.createQuery("select j from JwtMetadata j where j.emailAddress = :emailAddress", JwtMetadata.class)
            .setParameter("emailAddress", emailAddress)
            .getResultList().forEach(session::remove);
    }

    public Optional<JwtMetadata> fetchOptionalById(String id) {
        return session.createQuery("select j from JwtMetadata j where id = :id", JwtMetadata.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public void deleteById(String id) {
        fetchOptionalById(id).ifPresent(session::remove);
    }
}
