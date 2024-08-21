/*
 * This file is generated by jOOQ.
 */
package com.steeplesoft.simplesec.app.model.jooq.tables.daos;


import com.steeplesoft.simplesec.app.model.jooq.tables.JwtMetadata;
import com.steeplesoft.simplesec.app.model.jooq.tables.records.JwtMetadataRecord;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;

import javax.annotation.processing.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JwtMetadataDao extends DAOImpl<JwtMetadataRecord, com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata, String> {

    /**
     * Create a new JwtMetadataDao without any configuration
     */
    public JwtMetadataDao() {
        super(JwtMetadata.JWT_METADATA, com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata.class);
    }

    /**
     * Create a new JwtMetadataDao with an attached configuration
     */
    public JwtMetadataDao(Configuration configuration) {
        super(JwtMetadata.JWT_METADATA, com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata.class, configuration);
    }

    @Override
    @Nonnull
    public String getId(com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    @Nonnull
    public List<com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata> fetchRangeOfId(String lowerInclusive, String upperInclusive) {
        return fetchRange(JwtMetadata.JWT_METADATA.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    @Nonnull
    public List<com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata> fetchById(String... values) {
        return fetch(JwtMetadata.JWT_METADATA.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    @Nullable
    public com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata fetchOneById(String value) {
        return fetchOne(JwtMetadata.JWT_METADATA.ID, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    @Nonnull
    public Optional<com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata> fetchOptionalById(String value) {
        return fetchOptional(JwtMetadata.JWT_METADATA.ID, value);
    }

    /**
     * Fetch records that have <code>user_name BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    @Nonnull
    public List<com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata> fetchRangeOfUserName(String lowerInclusive, String upperInclusive) {
        return fetchRange(JwtMetadata.JWT_METADATA.USER_NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>user_name IN (values)</code>
     */
    @Nonnull
    public List<com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata> fetchByUserName(String... values) {
        return fetch(JwtMetadata.JWT_METADATA.USER_NAME, values);
    }

    /**
     * Fetch records that have <code>expiry_date BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    @Nonnull
    public List<com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata> fetchRangeOfExpiryDate(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(JwtMetadata.JWT_METADATA.EXPIRY_DATE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>expiry_date IN (values)</code>
     */
    @Nonnull
    public List<com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata> fetchByExpiryDate(Long... values) {
        return fetch(JwtMetadata.JWT_METADATA.EXPIRY_DATE, values);
    }

    /**
     * Fetch records that have <code>revoked BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    @Nonnull
    public List<com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata> fetchRangeOfRevoked(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(JwtMetadata.JWT_METADATA.REVOKED, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>revoked IN (values)</code>
     */
    @Nonnull
    public List<com.steeplesoft.simplesec.app.model.jooq.tables.pojos.JwtMetadata> fetchByRevoked(Boolean... values) {
        return fetch(JwtMetadata.JWT_METADATA.REVOKED, values);
    }
}