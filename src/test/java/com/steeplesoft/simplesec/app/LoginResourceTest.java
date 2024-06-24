package com.steeplesoft.simplesec.app;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steeplesoft.simplesec.app.payload.LoginInput;
import com.steeplesoft.simplesec.app.resources.LoginResource;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.ext.mail.MailMessage;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.List;

@QuarkusTest
@TestHTTPEndpoint(LoginResource.class)
public class LoginResourceTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Inject
    MockMailbox mailbox;

    @BeforeEach
    void init()  {
        mailbox.clear();
    }

    @Test
    public void testLogin() throws JsonProcessingException {
        LoginInput form = new LoginInput();
        form.userName = "admin2@example.com";
        form.password = "password";

        given().when()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(form))
                .post("/login")
                .then()
                .statusCode(200);
    }

    @Test
    public void testInvalidLogin() throws JsonProcessingException {
        LoginInput form = new LoginInput();
        form.userName = "admin2@example.com";
        form.password = "bad password";

        given().when()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(form))
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void testRegistration() throws JsonProcessingException {
        LoginInput form = new LoginInput();
        form.userName = "newuser@example.com";
        form.password = "wwRmqdr#Kuw93thkfzWaz";
        given().when()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(form))
                .post("/register")
                .then()
                .statusCode(200)
                .body(containsString("address1"));
    }

    @Test
    public void testDuplicateRegistration() throws JsonProcessingException {
        LoginInput form = new LoginInput();
        form.userName = "admin@example.com";
        form.password = "wwRmqdr#Kuw93thkfzWaz";
        given().when()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(form))
                .post("/register")
                .then()
                .statusCode(400);
    }

    @Test
    public void testForgotPassword() throws JsonProcessingException {
        LoginInput form = new LoginInput();
        form.userName = "admin@example.com";
        given().when()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(form))
                .post("/forgotPassword")
                .then()
                .statusCode(204);

        List<MailMessage> sent = mailbox.getMailMessagesSentTo(form.userName);
        assertThat(sent.size(), CoreMatchers.is(1));

        MailMessage first = sent.getFirst();
        assertThat(first.getSubject(), CoreMatchers.containsString("Password Recovery"));
        assertThat(first.getText(), CoreMatchers.containsString("Your code is"));
    }
}
