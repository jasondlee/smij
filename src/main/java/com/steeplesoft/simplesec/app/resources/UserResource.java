package com.steeplesoft.simplesec.app.resources;

import com.steeplesoft.simplesec.app.model.jooq.tables.pojos.UserAccount;
import com.steeplesoft.simplesec.app.services.UserService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
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
import java.util.List;

@Path("/_sec/users")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    protected UserService userService;

    @Path("/")
    @GET
    @RolesAllowed("ADMIN")
    public List<UserAccount> getUsers() {
        return userService.getUsers();
    }

    @Path("/me")
    @GET
    @RolesAllowed({"ADMIN","USER"})
    public UserAccount getUser() {
        return userService.getCurrentUser();
    }

    @Path("/{id}")
    @GET
    @RolesAllowed("ADMIN")
    public UserAccount getUser(@PathParam("id") Long id) {
        return userService.getUser(id).orElseThrow(NotFoundException::new);
    }

    @Path("/{id}")
    @POST
    @RolesAllowed("ADMIN")
    public Response updateUser(@PathParam("id") Long id, UserAccount userAccount) {
        userService.updateUser(id, userAccount);

        return Response.ok().build();
    }

    @Path("/{id}")
    @DELETE
    @RolesAllowed("ADMIN")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteUser(id);

        return Response.ok().build();
    }
}
