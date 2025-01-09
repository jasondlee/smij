package com.steeplesoft.smij;

import static com.steeplesoft.smij.Constants.CLAIM_TENANT;

import com.steeplesoft.smij.payload.LoginInput;
import com.steeplesoft.smij.payload.PasswordRecoveryInput;
import com.steeplesoft.smij.services.LoginService;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {
    @Inject
    protected LoginService loginService;
    @Inject
    JsonWebToken jwt;

    @Inject
    protected TokenValidator tokenValidator;

    @GET
    @Path("checkToken")
    @PermitAll
    public Response checkLogin() {
        try {
            tokenValidator.validate(jwt.getTokenID());
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @RunOnVirtualThread
    @Path("login")
    @POST
    public String login(@Valid LoginInput loginInfo) {
        String token = loginService.login(loginInfo);
        if (token == null) {
            throw new AuthenticationFailedException();
        }
        return token;
    }

    @POST
    @Path("forgotPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(LoginInput loginInfo) {
        loginService.generatePasswordRecoveryCode(loginInfo.userName);
        return Response.noContent().build();
    }

    @POST
    @Path("recoverPassword")
    public Response recoverPassword(PasswordRecoveryInput input) {
        loginService.recoverPassword(input.emailAddress, input.recoveryToken, input.newPassword1, input.newPassword2);

        return Response.ok().build();
    }

}
