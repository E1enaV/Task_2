package user;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

public class LoginUserTest extends BaseTest {

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void loginWithValidCredentialsSuccess() {

        UserData user = UserData.generateRandom();
        Response registerResponse = StepUser.createUser(user, requestSpec);
        registerResponse.then().statusCode(200);

        accessToken = registerResponse.path("accessToken");

        Response loginResponse = StepUser.loginUser(user, requestSpec);

        loginResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user", notNullValue())
                .body("user.email", not(emptyString()))
                .body("user.name", not(emptyString()));
    }

    @Test
    @DisplayName("Логин с неверным Email")
    public void loginWithWrongEmailShouldFail() {

        UserData realUser = UserData.generateRandom();
        Response registerResponse = StepUser.createUser(realUser, requestSpec);
        registerResponse.then().statusCode(200);

        accessToken = registerResponse.path("accessToken");

        UserData wrongEmailUser = new UserData(
                "wrong_" + realUser.getEmail(),
                realUser.getPassword(),
                ""
        );

        Response loginResponse = StepUser.loginUser(wrongEmailUser, requestSpec);

        loginResponse.then()
                .statusCode(401) // Unauthorized
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным Password")
    public void loginWithWrongPasswordShouldFail() {

        UserData realUser = UserData.generateRandom();
        Response registerResponse = StepUser.createUser(realUser, requestSpec);
        registerResponse.then().statusCode(200);

        accessToken = registerResponse.path("accessToken");

        UserData wrongPasswordUser = new UserData(
                realUser.getEmail(),
                "wrong_" + realUser.getPassword(),
                ""
        );

        Response loginResponse = StepUser.loginUser(wrongPasswordUser, requestSpec);

        loginResponse.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}