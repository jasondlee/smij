package com.steeplesoft.smij;

import static com.steeplesoft.smij.Constants.CACHE_TOKENS;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import com.steeplesoft.smij.exception.InvalidTokenException;
import com.steeplesoft.smij.model.JwtMetadata;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheName;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;

@Dependent
public class TokenValidator {
    @Inject
    @CacheName(CACHE_TOKENS)
    Cache cache;

    public void validate(@CacheKey String jti) {
        try {
            if (!CompletableFuture.supplyAsync(() -> validateToken(jti)).get()) {
                throw new InvalidTokenException();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @ActivateRequestContext
    protected Boolean validateToken(String jti) {
        Optional<JwtMetadata> optional = JwtMetadata.fetchOptionalById(jti);
        if (optional.isEmpty()) {
            return false;
        }
        JwtMetadata metadata = optional.get();
        // TZ issues?
        if (metadata.revoked ||
            OffsetDateTime.now().isAfter(metadata.expiryDate)) {
            JwtMetadata.deleteById(metadata.id);
            cache.invalidate(jti).await().atMost(Duration.ofMillis(500));
            return false;
        }

        return true;
    }
}
