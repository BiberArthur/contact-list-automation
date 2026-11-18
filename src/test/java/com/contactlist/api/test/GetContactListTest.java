package com.contactlist.api.test;

import com.contactlist.api.model.AddContact;
import com.contactlist.api.model.NewUser;
import com.contactlist.api.specs.BaseApi;
import com.contactlist.api.specs.ContactApi;
import com.contactlist.api.specs.UserApi;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetContactListTest extends BaseApi {
    private Faker faker = new Faker();
    private String token;

    private final UserApi userApi = new UserApi();
    private final ContactApi contactApi = new ContactApi();

    @BeforeEach
    public void setUp() {

        NewUser newUser = NewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password()).build();

        ValidatableResponse userResponse = userApi.createUser(newUser);
        userResponse.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("token", notNullValue());

        token = userResponse.extract().path("token");

        AddContact contact1 = AddContact.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .city("Frankfurt am Main")
                .build();

        contactApi.addContact(token, contact1)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);

        AddContact contact2 = AddContact.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .city("Berlin")
                .build();

        contactApi.addContact(token, contact2)
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    @DisplayName("Should successfully retrieve the contact list")
    public void getContactListSuccess() {

        ValidatableResponse response = contactApi.getContactList(token);
        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("$", hasSize(2))
                .body("city", hasItems("Frankfurt am Main", "Berlin"));
    }

    @AfterEach
    public void tearDown() {
        if (token != null) {
            ValidatableResponse deleteResponse = userApi.deleteUser(token);
        }
    }

}
