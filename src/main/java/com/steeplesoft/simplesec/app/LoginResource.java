package com.steeplesoft.simplesec.app;


import java.time.Duration;
import java.util.List;
import java.util.Set;

import com.steeplesoft.simplesec.app.model.User;
import com.steeplesoft.simplesec.app.payload.LoginFormInput;
import com.steeplesoft.simplesec.app.payload.PassowrdRecoveryFormInput;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.common.annotation.RunOnVirtualThread;
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

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/_sec")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {
    @Inject
    protected PasswordValidator passwordValidator;
    @Inject
    protected UserService userService;

    @Inject
    @ConfigProperty(name="simplesec.issuer", defaultValue = "https://simplesec")
    protected String issuer;
    @Inject
    @ConfigProperty(name="simplesec.duration", defaultValue = "1440")
    protected int durationInMinutes;

    @RunOnVirtualThread
    @Path("/login")
    @POST
    public String login(LoginFormInput loginInfo) {
        User userAccount = userService.getUser(loginInfo.userName, loginInfo.password)
                .orElseThrow(AuthenticationFailedException::new);

        return generateJwt(userAccount);
    }

    @RunOnVirtualThread
    @Path("/users")
    @GET
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @Path("/users/{id}")
    @POST
    public Response updateUser(@PathParam("id") Long id, User userAccount) {
        User existing = userService.getUser(id).orElseThrow(NotFoundException::new);

        if (!userAccount.userName.equals(existing.userName)) {
            throw new BadRequestException();
        }

        userService.saveUser(userAccount);

        return Response.ok().build();
    }

    @Path("/users/{id}")
    @DELETE
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteUser(id);

        return Response.ok().build();
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

    private String generateJwt(User user) {
        return Jwt.issuer(issuer)
                .upn(user.userName)
                .expiresIn(Duration.ofMinutes(durationInMinutes))
                .groups(Set.of(user.roles.split(",")))
                .sign();
    }

}
