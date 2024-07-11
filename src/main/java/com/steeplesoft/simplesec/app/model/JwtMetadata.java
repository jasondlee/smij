package com.steeplesoft.simplesec.app.model;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="jwt_metadata")
public class JwtMetadata  extends PanacheEntityBase {
    @Id
    public String id;
    public long expiresAt;
    public boolean revoked = false;
}
