package com.steeplesoft.simplesec.app;

import com.steeplesoft.simplesec.app.payload.LoginInput;
import com.steeplesoft.simplesec.app.payload.PasswordRecoveryInput;
import com.steeplesoft.simplesec.app.services.LoginService;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {
    @Inject
    protected LoginService loginService;

    @RunOnVirtualThread
    @Path("/login")
    @POST
    public String login(LoginInput loginInfo) {
        String token = loginService.login(loginInfo);
        if (token == null) {
            throw new AuthenticationFailedException();
        }
        return token;
    }

    @POST
    @Path("/forgotPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(LoginInput loginInfo) {
        loginService.generatePasswordRecoveryCode(loginInfo.userName);
        return Response.noContent().build();
    }

    @POST
    @Path("/recoverPassword")
    public Response recoverPassword(PasswordRecoveryInput input) {
        loginService.recoverPassword(input.emailAddress, input.recoveryToken, input.newPassword1, input.newPassword2);

        return Response.ok().build();
    }

}
