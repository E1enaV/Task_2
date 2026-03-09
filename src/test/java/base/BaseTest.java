package base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import user.StepUser;

public abstract class BaseTest {

    protected static RequestSpecification requestSpec;
    protected String accessToken;

    @BeforeAll
    public static void setupClass() {

        RestAssured.baseURI = ApiUrl.BASE_URL;
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(ApiUrl.BASE_URL)
                .addFilter(new AllureRestAssured())
                .build();
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            StepUser.deleteUser(accessToken, requestSpec);
        }
    }
}