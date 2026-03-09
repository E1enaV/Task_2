package user;

import base.ApiUrl;
import base.BaseTest;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest extends BaseTest {
    protected UserData uniqueUser;

    @BeforeEach
    public void setUpUser() {
        uniqueUser = UserData.generateRandom();
        Response createResponse = StepUser.createUser(uniqueUser, requestSpec);
        createResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        accessToken = createResponse.path("accessToken");
    }

    @Test
    @DisplayName("Изменение Email с авторизацией")
    public void updateEmailWithAuthorizationSuccess () {

        UserData updatedData = new UserData(
                "updated_" + System.currentTimeMillis() + "@yandex.ru",
                uniqueUser.getPassword(),
                uniqueUser.getName()
        );

        Response updateResponse = StepUser.updateUser(accessToken, updatedData, requestSpec);

        updateResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user", Matchers.notNullValue())
                .body("user.email", not(emptyString()))
                .body("user.name", not(emptyString()));

        StepUser.loginUser(updatedData, requestSpec)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение Password с авторизацией")
    public void updatePasswordWithAuthorizationSuccess () {

        String newPassword = "newpassword_" + System.currentTimeMillis();
        UserData updatedData = new UserData(
                uniqueUser.getEmail(),
                newPassword,
                uniqueUser.getName()
        );

        Response updateResponse = StepUser.updateUser(accessToken, updatedData, requestSpec);

        updateResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user", Matchers.notNullValue())
                .body("user.email", not(emptyString()))
                .body("user.name", not(emptyString()));

        StepUser.loginUser(updatedData, requestSpec)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение Name с авторизацией")
    public void updateNameWithAuthorizationSuccess () {

        String newName = "UpdatedName_" + System.currentTimeMillis();
        UserData updatedData = new UserData(
                uniqueUser.getEmail(),
                uniqueUser.getPassword(),
                newName
        );

        Response updateResponse = StepUser.updateUser(accessToken, updatedData, requestSpec);

        updateResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user", Matchers.notNullValue())
                .body("user.email", not(emptyString()))
                .body("user.name", not(emptyString()));

        StepUser.loginUser(uniqueUser, requestSpec)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение всех данных пользователя")
    public void updateAllFieldsWithAuthorizationSuccess () {

        UserData updatedData = new UserData(
                "allnew_" + System.currentTimeMillis() + "@example.com",
                "newpassword_" + System.currentTimeMillis(),
                "NewName_" + System.currentTimeMillis()
        );

        Response updateResponse = StepUser.updateUser(accessToken, updatedData, requestSpec);

        updateResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user", Matchers.notNullValue())
                .body("user.email", equalTo(updatedData.getEmail()))
                .body("user.name", equalTo(updatedData.getName()));

        StepUser.loginUser(updatedData, requestSpec)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение данных без авторизации")
    public void updateUserWithoutAuthorizationShouldFail () {

        UserData updatedData = new UserData(
                "test@" + System.currentTimeMillis() + ".ru",
                "testPassword",
                "NameTest"
        );

        Response updateResponse = given()
                .header("Content-type", "application/json")
                .body(updatedData)
                .when()
                .patch(ApiUrl.USER);

        updateResponse.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}