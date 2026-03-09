package order;

import base.ApiUrl;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class   StepOrder {

    @Step("Получение списка ингредиентов")
    public static Response getIngredients(RequestSpecification spec) {
        return given(spec)
                .get(ApiUrl.INGREDIENTS);
    }

    @Step("Создание заказа с авторизацией")
    public static Response createOrderWithToken(String accessToken, List<String> ingredientIds, RequestSpecification spec) {
        Map<String, Object> orderData = Map.of("ingredients", ingredientIds);

        return given(spec)
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(orderData)
                .when()
                .post(ApiUrl.ORDER);
    }

    @Step("Получение заказов пользователя")
    public static Response getUserOrders(String accessToken, RequestSpecification spec) {
        return given(spec)
                .header("Authorization", accessToken)
                .when()
                .get(ApiUrl.ORDER);
    }
}