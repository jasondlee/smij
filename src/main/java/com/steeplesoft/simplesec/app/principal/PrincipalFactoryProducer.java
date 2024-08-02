package com.steeplesoft.simplesec.app.principal;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipalFactory;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;

public class PrincipalFactoryProducer {
    @ConfigProperty(name = "simplesec.jwt.revocation.support", defaultValue = "false")
    protected boolean revocationSupport;

    @Produces
    @ApplicationScoped
    @Alternative
    @Priority(1000)
    public JWTCallerPrincipalFactory produce() {
        if (revocationSupport) {
            return new PersistentCallerPrincipalFactory();
        } else {
            return new JWTCallerPrincipalFactory() {

                @Override
                public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo) {
                    String json = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
                    try {
                        JwtClaims claims = JwtClaims.parse(json);
                        return new DefaultJWTCallerPrincipal(claims);
                    } catch (InvalidJwtException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }
}
