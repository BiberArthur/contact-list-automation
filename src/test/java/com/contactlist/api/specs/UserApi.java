package com.contactlist.api.specs;

import com.contactlist.api.model.LoginUser;
import com.contactlist.api.model.NewUser;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserApi extends BaseApi {


    public ValidatableResponse createUser(NewUser newUser) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .body(newUser)
                .header("Content-Type", "application/json")
                .post(CREATE_USER)
                .then()
                .log().all();
    }

    public ValidatableResponse deleteUser(String token) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .header("Authorization", token)
                .delete(DELETE_USER)
                .then()
                .log().all();
    }

    public ValidatableResponse loginUser(LoginUser loginUser) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .body(loginUser)
                .header("Content-Type", "application/json")
                .post(LOGIN_USER)
                .then()
                .log().all();
    }


}
