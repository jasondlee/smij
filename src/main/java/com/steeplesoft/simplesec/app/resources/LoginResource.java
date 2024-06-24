package com.steeplesoft.simplesec.app.resources;


import java.util.List;

import com.steeplesoft.simplesec.app.services.PasswordValidator;
import com.steeplesoft.simplesec.app.model.User;
import com.steeplesoft.simplesec.app.payload.LoginFormInput;
import com.steeplesoft.simplesec.app.payload.PassowrdRecoveryFormInput;
import com.steeplesoft.simplesec.app.services.UserService;
import io.smallrye.common.annotation.RunOnVirtualThread;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
    public String login(LoginFormInput loginInfo) {
        return userService.login(loginInfo);
    }

    @RunOnVirtualThread
    @Path("/register")
    @POST
    public User register(LoginFormInput registration) {
        return userService.saveUser(new User(registration.userName, registration.password));
    }

    @POST
    @Path("/forgotPassword")
    @Produces(MediaType.TEXT_PLAIN)
    public String forgotPassword(LoginFormInput loginInfo) {
        return userService.generatePasswordRecoveryCode(loginInfo.userName);
    }

    @POST
    @Path("/recoverPassword")
    public Response recoverPassword(PassowrdRecoveryFormInput input) {
        userService.recoverPassword(input.emailAddress, input.recoveryToken, input.newPassword1, input.newPassword2);

        return Response.ok().build();
    }

}
