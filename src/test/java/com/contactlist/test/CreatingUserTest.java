package com.contactlist.test;

import com.contactlist.api.specs.UserApi;
import com.contactlist.api.model.NewUser;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class CreatingUserTest extends UserApi {
    private Faker faker = new Faker();
    private String token;


    @Before
    public void setUp() {
        token = null;
    }

    @Test
    @DisplayName("Should throw an error when creating a duplicate user")
    public void createUserDuplicateDataShowsError() {

        NewUser firstUser = new NewUser(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress(), "q1w2e3r4t5");

        ValidatableResponse response1 = createUser(firstUser);
        response1.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("token", notNullValue());

        token = response1.extract().path("token");

        ValidatableResponse response2 = createUser(firstUser);
        response2.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Email address is already in use"));
    }

    @Test
    @DisplayName("Should throw an error when creating a user without a first name")
    public void createUserWithoutFirstNameShowsError() {
        NewUser userWitchFisName = new NewUser("", faker.name().lastName(), faker.internet().emailAddress(), "q1w2e3r4t5");

        ValidatableResponse response = createUser(userWitchFisName);

        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString("Path `firstName` is required."));

    }

    @Test
    @DisplayName("Should throw an error when creating a user without a last name")
    public void createUserWithoutLastNameShowsError() {
        NewUser userWitchLastName = new NewUser(faker.name().firstName(), "", faker.internet().emailAddress(), "q1w2e3r4t5");

        ValidatableResponse response = createUser(userWitchLastName);

        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString("Path `lastName` is required."));
    }

    @Test
    @DisplayName("Should throw an error when creating a user without a email")
    public void createUserWithoutEmailShowsError() {
        NewUser userWitchEmail = new NewUser(faker.name().firstName(), faker.name().lastName(), "", "q1w2e3r4t5");

        ValidatableResponse response = createUser(userWitchEmail);

        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString("Email is invalid"));
    }

    @Test
    @DisplayName("Should throw an error when creating a user without a password")
    public void createUserWithoutPasswordShowsError() {
        NewUser userWitchPassword = new NewUser(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress(), "");

        ValidatableResponse response = createUser(userWitchPassword);

        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString("password: Path `password` is required."));
    }

    @After
    public void tearDown() {
        if (token != null) {
            ValidatableResponse deleteResponse = deleteUser(token);
        }
    }

}