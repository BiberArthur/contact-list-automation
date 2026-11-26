package com.contactlist.api.test;

import com.contactlist.api.specs.UserApi;
import com.contactlist.api.model.NewUser;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.*;

public class CreatingUserTest extends UserApi {
    private Faker faker = new Faker();
    private String token;


    @BeforeEach
    public void setUp() {
        token = null;
    }

    @Test
    @DisplayName("Should throw an error when creating a duplicate user")
    public void createUserDuplicateDataShowsError() {

        NewUser firstUser = NewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password()).build();

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
       NewUser userWitchFisName = NewUser.builder()
               .lastName(faker.name().lastName())
               .email(faker.internet().emailAddress())
               .password(faker.internet().password()).build();

        ValidatableResponse response = createUser(userWitchFisName);

        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString("Path `firstName` is required."));

    }

    @Test
    @DisplayName("Should throw an error when creating a user without a last name")
    public void createUserWithoutLastNameShowsError() {
        NewUser userWitchLastName = NewUser.builder()
                .firstName(faker.name().firstName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password()).build();

        ValidatableResponse response = createUser(userWitchLastName);

        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString("Path `lastName` is required."));
    }

    @Test
    @DisplayName("Should throw an error when creating a user without a email")
    public void createUserWithoutEmailShowsError() {
        NewUser userWitchEmail = NewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                //.email("")
                .password(faker.internet().password()).build();

        ValidatableResponse response = createUser(userWitchEmail);

        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString("Email is invalid"));
    }

    @Test
    @DisplayName("Should throw an error when creating a user without a password")
    public void createUserWithoutPasswordShowsError() {
        NewUser userWitchPassword = NewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress()).build();

        ValidatableResponse response = createUser(userWitchPassword);

        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString("password: Path `password` is required."));
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "S",
            "St",
            "StenStenSt",
            "StenStenStenStenSte",
            "StenStenStenStenSten",
            " Sten ",
            "St en",
            "Иван-123!@#"
    } )
    @DisplayName("Should create user when first name is valid")
    public void createUserWithValidFirstName(String validFirstName) {
        NewUser user = NewUser.builder()
                .firstName(validFirstName)
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password()).build();

        ValidatableResponse response = createUser(user);
            response.assertThat()
                    .statusCode(HttpStatus.SC_CREATED)
                    .body("token",notNullValue());

            token = response.extract().path("token");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "StenStenStenStenStenS",
            "StenStenStenStenStenSt",
            " ",
    })
    @DisplayName("Should throw an error if first name is not valid")
    public void createUserWithTooNotValidFirstNameTest(String notValidFirstName){
        NewUser user = NewUser.builder()
                .firstName(notValidFirstName)
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password()).build();

        ValidatableResponse response = createUser(user);
        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", containsString("firstName"));
    }


    @AfterEach
    public void tearDown() {
        if (token != null) {
            ValidatableResponse deleteResponse = deleteUser(token);
        }
    }

}