package com.steeplesoft.simplesec.app.resources;


import com.steeplesoft.simplesec.app.model.User;
import com.steeplesoft.simplesec.app.payload.LoginInput;
import com.steeplesoft.simplesec.app.payload.PasswordRecoveryInput;
import com.steeplesoft.simplesec.app.services.UserService;
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

@Path("/_sec")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {
    @Inject
    protected UserService userService;

    @RunOnVirtualThread
    @Path("/login")
    @POST
    public String login(LoginInput loginInfo) {
        String token = userService.login(loginInfo);
        if (token == null) {
            throw new AuthenticationFailedException();
        }
        return token;
    }

    @RunOnVirtualThread
    @Path("/register")
    @POST
    public User register(LoginInput registration) {
        return userService.createUser(new User(registration.userName, registration.password));
    }

    @POST
    @Path("/forgotPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(LoginInput loginInfo) {
        userService.generatePasswordRecoveryCode(loginInfo.userName);
        return Response.noContent().build();
    }

    @POST
    @Path("/recoverPassword")
    public Response recoverPassword(PasswordRecoveryInput input) {
        userService.recoverPassword(input.emailAddress, input.recoveryToken, input.newPassword1, input.newPassword2);

        return Response.ok().build();
    }

}
