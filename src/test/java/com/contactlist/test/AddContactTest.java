package com.contactlist.test;

import com.contactlist.api.model.AddContact;
import com.contactlist.api.model.NewUser;
import com.contactlist.api.specs.BaseApi;
import com.contactlist.api.specs.UserApi;
import com.contactlist.api.specs.ContactApi;
import com.github.javafaker.Faker;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class AddContactTest extends BaseApi {
    private Faker faker = new Faker();
    private String token;

    private final UserApi userApi = new UserApi();
    private final ContactApi contactApi = new ContactApi();

    @Before
    public void setUp() {

        NewUser newUser = new NewUser(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress(), faker.internet().password());

        ValidatableResponse response = userApi.createUser(newUser);
        response.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("token", notNullValue());

        token = response.extract().path("token");

    }

    @Test
    public void addContactWithValidDataReturnsSuccess() {
        AddContact contact = AddContact.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .city("Frankfurt am Main")
                .build();

        ValidatableResponse response = contactApi.addContact(token, contact);
        response.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("_id", notNullValue());

    }

    @Test
    public void addContactWithoutFirstNameShowsError() {
        AddContact contact = AddContact.builder()
                .firstName("")
                .lastName(faker.name().lastName())
                .city("Frankfurt am Main")
                .build();

        ValidatableResponse response = contactApi.addContact(token, contact);
        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("_message", equalTo("Contact validation failed"));
    }

    @After
    public void tearDown() {
        if (token != null) {
            ValidatableResponse deleteResponse = userApi.deleteUser(token);
            deleteResponse.assertThat()
                    .statusCode(HttpStatus.SC_OK);
        }
    }

}
