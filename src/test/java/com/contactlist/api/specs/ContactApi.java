package com.contactlist.api.specs;

import com.contactlist.api.model.EddContact;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class ContactApi extends BaseApi {


    public ValidatableResponse eddContact (String token, EddContact eddContact){
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Barer ", token)
                .body(eddContact)
                .post(ADD_CONTACT)
                .then()
                .log().all();
    }
}
