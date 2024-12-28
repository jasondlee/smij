package com.steeplesoft.smij;

import static com.steeplesoft.smij.Constants.CACHE_TOKENS;

import java.time.OffsetDateTime;

import com.steeplesoft.smij.exception.InvalidTokenException;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheName;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import com.steeplesoft.smij.model.JwtMetadata;

@Dependent
public class TokenValidator {
    @Inject
    @CacheName(CACHE_TOKENS)
    Cache cache;

    public void validate(@CacheKey String jti) {
        JwtMetadata metadata = JwtMetadata.fetchOptionalById(jti)
            .orElseThrow(InvalidTokenException::new);

        // TZ issues?
        if (Boolean.TRUE.equals(metadata.revoked) || OffsetDateTime.now().isAfter(metadata.expiryDate)) {
            JwtMetadata.deleteById(metadata.id);
            cache.invalidate(jti);
            throw new InvalidTokenException();
        }
    }
}
