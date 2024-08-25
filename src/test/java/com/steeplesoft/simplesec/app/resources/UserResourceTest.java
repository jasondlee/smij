package com.steeplesoft.simplesec.app.resources;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steeplesoft.simplesec.app.model.jooq.tables.pojos.UserAccount;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
public class UserResourceTest {
    @Inject
    protected ObjectMapper mapper;

    @Test
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    public void getAllUsersAsAdmin() {
        getAllUsers(200);
    }

    @Test
    @TestSecurity(user = "admin@example.com", roles = {"USER"})
    public void getAllUsersAsUser() {
        getAllUsers(403);
    }

    @Test
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    public void getUserAsAdmin() {
        getUser(200);
    }

    @Test
    @TestSecurity(user = "user@example.com", roles = {"USER"})
    public void getUserAsNonAdmin() {
        getUser(403);
    }

    @Test
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    public void updateUserAsAdmin() throws JsonProcessingException {
        updateUser(200);
    }

    @Test
    @TestSecurity(user = "user@example.com", roles = {"USER"})
    public void updateUserAsNonAdmin() throws JsonProcessingException {
        updateUser(403);
    }

    @Test
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    public void deleteUserAsAdmin() {
        deleteUser(-4, 200);
    }

    @Test
    @TestSecurity(user = "user@example.com", roles = {"USER"})
    public void deleteUserAsNonAdmin() {
        deleteUser(-5,403);
    }

    private void updateUser(int expected) throws JsonProcessingException {
        UserAccount user = new UserAccount();
        user.setId(-3L);
        user.setUserName("user@example.com");
        user.setCity("new city");
        given().when()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(user))
                .post("" + user.getId())
                .then()
                .statusCode(expected);
    }

    private void getAllUsers(int expected) {
        given().when()
                .contentType(ContentType.JSON)
                .get("/")
                .then()
                .statusCode(expected);
    }

    private void getUser(int expected) {
        given().when()
                .get("/-2")
                .then()
                .statusCode(expected);
    }

    private void deleteUser(int id, int expected) {
        given().when()
                .delete("" + id)
                .then()
                .statusCode(expected);
        given().when()
                .get("" + id)
                .then()
                .statusCode((expected == 200) ? 404 : 403);
    }
}
