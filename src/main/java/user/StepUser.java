package user;

import base.ApiUrl;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class StepUser {

    @Step("Создание нового пользователя")
    public static Response createUser(UserData user, RequestSpecification spec) {
        return given(spec)
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(ApiUrl.CREATE_USER);
    }

    @Step("Логин пользователя")
    public static Response loginUser(UserData user, RequestSpecification spec) {
        return given(spec)
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(ApiUrl.LOGIN_USER);
    }

    @Step("Изменение данных пользователя")
    public static Response updateUser(String accessToken, UserData updatedData, RequestSpecification spec) {
        return given(spec)
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(updatedData)
                .when()
                .patch(ApiUrl.USER);
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String accessToken, RequestSpecification spec) {

        return given(spec)
                .header("Authorization", accessToken)
                .delete(ApiUrl.USER);
    }
}