package user;

import base.BaseTest;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest extends BaseTest {

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUserSuccess() {

        UserData uniqueUser = UserData.generateRandom();
        Response response = StepUser.createUser(uniqueUser, requestSpec);

        response.then()
                .body("success", equalTo(true))
                .body("user.email", equalTo(uniqueUser.getEmail()))
                .body("user.name", equalTo(uniqueUser.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя без Email")
    public void createUserWithoutEmailShouldFail() {
        UserData invalidUser = new UserData("", "test77777", "Test");

        StepUser.createUser(invalidUser, requestSpec)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без Password")
    public void createUserWithoutPasswordShouldFail() {
        UserData invalidUser = new UserData("test@example.com", "", "Test777");

        StepUser.createUser(invalidUser, requestSpec)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без Name")
    public void createUserWithoutNameShouldFail() {
        UserData invalidUser = new UserData("test@example.com", "password777", "");

        StepUser.createUser(invalidUser, requestSpec)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    public void createExistingUserShouldFail() {

        UserData existingUser = UserData.generateRandom();
        Response firstResponse = StepUser.createUser(existingUser, requestSpec);
        firstResponse.then().statusCode(200);

        accessToken = firstResponse.path("accessToken");

        Response secondResponse = StepUser.createUser(existingUser, requestSpec);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", Matchers.equalTo("User already exists"));
    }
}