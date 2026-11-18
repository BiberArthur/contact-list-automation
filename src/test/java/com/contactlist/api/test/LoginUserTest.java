package com.contactlist.api.test;

import com.contactlist.api.model.LoginUser;
import com.contactlist.api.model.NewUser;
import com.contactlist.api.specs.UserApi;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest extends UserApi {

    private final Faker faker = new Faker();
    private String token;
    private String randomEmail;
    private String randomPassword;


    @BeforeEach
    public void setUp() {
        randomEmail = faker.internet().emailAddress();
        randomPassword = faker.internet().password();

        NewUser newUser = NewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(randomEmail)
                .password(randomPassword).build();

        ValidatableResponse response = createUser(newUser);
        response.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("token", notNullValue());

        token = response.extract().path("token");

    }


    @Test
    @DisplayName("Should log in the user")
    public void loginUserSuccess() {
        LoginUser loginUser1 = new LoginUser(randomEmail, randomPassword);

        ValidatableResponse response = loginUser(loginUser1);
        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue());


      token = response.extract().path("token");
    }

    @Test
    @DisplayName("Should throw an error when logging in a user without a password")
    public void loginUserWithoutPasswordShowsError() {
        LoginUser loginUser1 = LoginUser.builder()
                .email(randomEmail).build();

        ValidatableResponse response = loginUser(loginUser1);

        response.assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @AfterEach
    public void tearDown() {
        if (token != null) {
            deleteUser(token);
        }
    }

}

