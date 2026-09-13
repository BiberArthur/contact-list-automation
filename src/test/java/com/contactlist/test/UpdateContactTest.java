package com.contactlist.test;

import com.contactlist.api.model.AddContact;
import com.contactlist.api.model.NewUser;
import com.contactlist.api.specs.BaseApi;
import com.contactlist.api.specs.ContactApi;
import com.contactlist.api.specs.UserApi;
import com.github.javafaker.Faker;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UpdateContactTest extends BaseApi {
    Faker faker = new Faker();
    private String token;
    private String contactId;
    private final ContactApi contactApi = new ContactApi();
    private final UserApi userApi = new UserApi();
    private String contactFirstName;
    private String contactLastName;


    @Before
    public void setUp() {
        contactFirstName = faker.name().firstName();
        contactLastName = faker.name().lastName();

        NewUser newUser = new NewUser(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress(), "q1w2e3r4");

        ValidatableResponse response = userApi.createUser(newUser);
        response.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("token", notNullValue());
        token = response.extract().path("token");

        AddContact contact1 = AddContact.builder()
                .firstName(contactFirstName)
                .lastName(contactLastName)
                .city("Ulm")
                .build();

        ValidatableResponse response1 = contactApi.addContact(token, contact1)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("_id", notNullValue());
        contactId = response1.extract().path("_id");

    }

    @Test
    public void updateContactCityReturnsSuccess() {
        AddContact updatedContact = AddContact.builder()
                .firstName(contactFirstName)
                .lastName(contactLastName)
                .city("Berlin")
                .build();
        contactApi.updateContact(token, contactId, updatedContact)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("city", equalTo("Berlin"));
    }

    @After
    public void tearDown() {
        if (token != null) {
            userApi.deleteUser(token);
        }
    }

}
