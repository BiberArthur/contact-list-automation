package com.contactlist.test;

import com.contactlist.api.model.LoginUser;
import com.contactlist.api.model.NewUser;
import com.contactlist.api.specs.UserApi;
import com.github.javafaker.Faker;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest extends UserApi {

    private Faker faker = new Faker();
    private String token;
    private String randomEmail;
    private String randomPassword;


    @Before
    public void setUp() {
        String randomFirstName = faker.name().firstName();
        String randomLastName = faker.name().lastName();
        randomEmail = faker.internet().emailAddress();
        randomPassword = faker.internet().password();

        NewUser newUser = new NewUser(randomFirstName, randomLastName, randomEmail, randomPassword);

        ValidatableResponse response = createUser(newUser);
        response.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("token", notNullValue());

        token = response.extract().path("token");

    }


    @Test
    public void loginUserSuccess() {
        LoginUser loginUser1 = new LoginUser(randomEmail, randomPassword);

        ValidatableResponse response = loginUser(loginUser1);
        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue());


      token = response.extract().path("token");
    }

    @Test
    public void loginUserWithoutPasswordShowsError() {
        LoginUser loginUser1 = new LoginUser(randomEmail, "");

        ValidatableResponse response = loginUser(loginUser1);

        response.assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @After
    public void tearDown() {
        if (token != null) {
            ValidatableResponse deleteResponse = deleteUser(token);
        }
    }

}

