package com.steeplesoft.smij.app;

import static com.steeplesoft.smij.app.Constants.CACHE_TOKENS;

import java.time.OffsetDateTime;

import com.steeplesoft.smij.app.dao.JwtMetadataDao;
import com.steeplesoft.smij.app.exception.InvalidTokenException;
import com.steeplesoft.smij.app.model.jooq.tables.pojos.JwtMetadata;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheName;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class TokenValidator {
    @Inject
    protected JwtMetadataDao jwtMetadataDao;
    @Inject
    @CacheName(CACHE_TOKENS)
    Cache cache;

    public void validate(@CacheKey String jti) {
        JwtMetadata metadata = jwtMetadataDao.fetchOptionalById(jti)
            .orElseThrow(InvalidTokenException::new);

        // TZ issues?
        if (Boolean.TRUE.equals(metadata.getRevoked()) || OffsetDateTime.now().isAfter(metadata.getExpiryDate())) {
            jwtMetadataDao.delete(metadata);
            cache.invalidate(jti);
            throw new InvalidTokenException();
        }
    }
}
