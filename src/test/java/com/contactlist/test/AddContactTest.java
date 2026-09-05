package com.contactlist.test;

import com.contactlist.api.model.NewUser;
import com.contactlist.api.specs.UserApi;
import com.github.javafaker.Faker;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.Before;

import static org.hamcrest.CoreMatchers.notNullValue;

public class AddContactTest extends UserApi {
    private Faker faker = new Faker();
    private String token;

    @Before
    public void setUp() {

        NewUser newUser = new NewUser(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress(), faker.internet().password());

        ValidatableResponse response = createUser(newUser);
        response.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("token", notNullValue());

        token = response.extract().path("token");


    }


}
