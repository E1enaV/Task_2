package order;

import base.ApiUrl;
import base.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.UserData;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static user.StepUser.createUser;

public class CreateOrderTest extends BaseTest {

    private List<String> getIngredient() {
        Response response = StepOrder.getIngredients(requestSpec);
        response.then().statusCode(200);
        return response.jsonPath().getList("data._id");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void createOrderWithAuthAndIngredientsSuccess() {

        UserData user = UserData.generateRandom();

        Response createResponse = createUser(user, requestSpec);
        createResponse.then().statusCode(200);
        accessToken = createResponse.path("accessToken");

        List<String> ingredientIds = getIngredient();
        assertFalse(ingredientIds.isEmpty());

        List<String> orderIngredients = Arrays.asList(
                ingredientIds.get(0),
                ingredientIds.get(2),
                ingredientIds.get(7)
        );

        Response orderResponse = StepOrder.createOrderWithToken(accessToken, orderIngredients, requestSpec);

        orderResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthSuccess() {

        List<String> ingredientIds = getIngredient();
        String bunId = ingredientIds.get(0);
        String fillingId = ingredientIds.get(2);

        Map<String, Object> orderData = Map.of(
                "ingredients", Arrays.asList(bunId, fillingId, bunId)
        );

        Response orderResponse = given()
                .header("Content-type", "application/json")
                .body(orderData)
                .post(ApiUrl.ORDER);

        orderResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithEmptyIngredientsShouldFail() {

        UserData user = UserData.generateRandom();
        Response createResponse = createUser(user, requestSpec);
        createResponse.then().statusCode(200);
        accessToken = createResponse.path("accessToken");

        List<String> emptyIngredients = Collections.emptyList();

        Response orderResponse = StepOrder.createOrderWithToken(accessToken, emptyIngredients, requestSpec);

        orderResponse.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientHashShouldFail() {

        UserData user = UserData.generateRandom();
        Response createResponse = createUser(user, requestSpec);
        createResponse.then().statusCode(200);
        accessToken = createResponse.path("accessToken");

        String invalidHash = "invalid_hash_777777777";
        List<String> invalidIngredients = Arrays.asList(invalidHash, invalidHash, invalidHash);

        Response orderResponse = StepOrder.createOrderWithToken(accessToken, invalidIngredients, requestSpec);

        orderResponse.then()
                .statusCode(500);
    }
}