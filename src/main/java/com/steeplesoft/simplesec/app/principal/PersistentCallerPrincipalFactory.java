package com.steeplesoft.simplesec.app.principal;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.steeplesoft.simplesec.app.model.JwtMetadata;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipalFactory;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;

//@ApplicationScoped
//@Alternative
//@Priority(1000)
public class PersistentCallerPrincipalFactory extends JWTCallerPrincipalFactory {
    @Override
    @Transactional
    public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo) throws ParseException {
        try {
            // Token has already been verified, parse the token claims only
            JwtClaims claims = JwtClaims.parse(
                    new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8));
            String jti = (String) claims.getClaimValue(Claims.jti.name());
            JwtMetadata metadata = JwtMetadata.findById(jti);
            // TZ issues?
            if (metadata == null || metadata.revoked || metadata.expiresAt < System.currentTimeMillis()) {
                throw new NotAuthorizedException("Invalid token");
            }
            return new DefaultJWTCallerPrincipal(claims);
        } catch (InvalidJwtException ex) {
            throw new ParseException(ex.getMessage());
        }
    }
}
