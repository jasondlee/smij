package com.steeplesoft.simplesec.app;

import com.steeplesoft.simplesec.app.model.User;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Resource {
    @Inject
    JsonWebToken jwt;

    @GET
    @PermitAll
    @RunOnVirtualThread
    public String demo(@Context SecurityContext ctx) {
        return getResponseString(ctx);
    }

    @GET
    @Path("secured")
    @RolesAllowed("Admin")
    public String secured(@Context SecurityContext ctx) {
        return getResponseString(ctx) + ", birthdate: " + jwt.getClaim("birthdate");
    }

    @GET
    @Path("/users")
    @PermitAll
    @RunOnVirtualThread
    public List<User> getUsers() {
        return  User.listAll();
    }


    private String getResponseString(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        return "hello "  + name +
                ", isHttps: " + ctx.isSecure() +
                ", authScheme: " + ctx.getAuthenticationScheme() +
                " hasJWT: " + hasJwt();
    }

    private boolean hasJwt() {
        return jwt.getClaimNames() != null;
    }
}
