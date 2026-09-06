package com.contactlist.api.specs;

import com.contactlist.api.model.AddContact;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class ContactApi extends BaseApi {


    public ValidatableResponse addContact(String token, AddContact addContact) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(addContact)
                .post(ADD_CONTACT)
                .then()
                .log().all();
    }

    public ValidatableResponse getContactList(String token) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .get(GET_CONTACT_LIST)
                .then()
                .log().all();
    }
}
