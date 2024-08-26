package com.steeplesoft.simplesec.app.dao;

import static com.steeplesoft.simplesec.app.model.jooq.Tables.JWT_METADATA;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata;
import com.steeplesoft.simplesec.app.model.jooq.tables.records.JwtMetadataRecord;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class JwtMetadataDao extends AbstractDao<JwtMetadataRecord, JwtMetadata, String> {
    public JwtMetadataDao() {
        super(JWT_METADATA, JwtMetadata.class);
    }

    @Override
    public String getId(JwtMetadata object) {
        return object.getId();
    }

    public Optional<JwtMetadata> fetchOptionalById(String id) {
        return fetchOptional(JWT_METADATA.ID, id);
    }

    public void addToken(String jti, String userName, OffsetDateTime expiresAt, boolean b) {
        insert(new JwtMetadata(jti, userName, expiresAt, false));
    }

    public void deleteTokensForUser(String emailAddress) {
        ctx().deleteFrom(JWT_METADATA)
                .where(JWT_METADATA.USER_NAME.eq(emailAddress))
                .execute();
    }
}
