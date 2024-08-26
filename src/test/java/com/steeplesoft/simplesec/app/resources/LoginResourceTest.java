package com.steeplesoft.simplesec.app.resources;

import static com.steeplesoft.simplesec.app.DummyUserSupplier.PASSWORD_GOOD;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steeplesoft.simplesec.app.LoginResource;
import com.steeplesoft.simplesec.app.payload.LoginInput;
import com.steeplesoft.simplesec.app.payload.PasswordRecoveryInput;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.ext.mail.MailMessage;
import jakarta.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(LoginResource.class)
public class LoginResourceTest {
    @Inject
    protected ObjectMapper mapper;

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
        form.password = PASSWORD_GOOD;

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
    public void testForgotPassword() throws JsonProcessingException {
        // Request reset code
        LoginInput login = new LoginInput("admin@example.com");

        given().when()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(login))
                .post("/forgotPassword")
                .then()
                .statusCode(204);

        List<MailMessage> sent = mailbox.getMailMessagesSentTo(login.userName);
        assertThat(sent.size(), CoreMatchers.is(1));

        MailMessage first = sent.getFirst();
        assertThat(first.getSubject(), CoreMatchers.containsString("Password Recovery"));
        assertThat(first.getText(), CoreMatchers.containsString("Your code is"));
        String recoveryCode = first.getText().split(" ")[3];

        // Reset password using code
        PasswordRecoveryInput recovery = new PasswordRecoveryInput();
        recovery.emailAddress = login.userName;
        recovery.recoveryToken = recoveryCode;
        recovery.newPassword1 = PASSWORD_GOOD;
        recovery.newPassword2 = PASSWORD_GOOD;

        given().when()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(recovery))
                .post("/recoverPassword")
                .then()
                .statusCode(200);

        // Login with new password
        login.password = recovery.newPassword1;
        given().when()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(login))
                .post("/login")
                .then()
                .statusCode(200);
    }
}
