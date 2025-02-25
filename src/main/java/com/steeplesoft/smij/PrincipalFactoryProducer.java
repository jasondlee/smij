package com.steeplesoft.smij;

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
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;

public class PrincipalFactoryProducer {
    @ConfigProperty(name = "smij.jwt.revocation.support", defaultValue = "false")
    protected boolean revocationSupport;

    @Inject
    protected TokenValidator tokenValidator;

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
                        return new DefaultJWTCallerPrincipal(JwtClaims.parse(json));
                    } catch (InvalidJwtException e) {
                        throw new NotAuthorizedException("Invalid token");
                    }
                }
            };
        }
    }

    public class PersistentCallerPrincipalFactory extends JWTCallerPrincipalFactory {

        @Override
        public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo) throws ParseException {
            try {
                // Token has already been verified, parse the token claims only
                JwtClaims claims = JwtClaims.parse(
                        new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8));
                String jti = (String) claims.getClaimValue(Claims.jti.name());
                tokenValidator.validate(jti);

                return new DefaultJWTCallerPrincipal(claims);
            } catch (InvalidJwtException ex) {
                throw new ParseException(ex.getMessage());
            }
        }


    }

}
