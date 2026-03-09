package user;

import base.ApiUrl;
import base.BaseTest;
import io.restassured.response.Response;
import order.StepOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrdersUserTest extends BaseTest {

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrdersForAuthorizedUserSuccess() {

        UserData user = UserData.generateRandom();
        Response createResponse = StepUser.createUser(user, requestSpec);
        createResponse.then().statusCode(200);
        accessToken = createResponse.path("accessToken");

        Response ingredientsResponse = StepOrder.getIngredients(requestSpec);
        ingredientsResponse.then().statusCode(200);
        List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");

        String bunId = ingredientIds.get(0);
        String fillingId = ingredientIds.size() > 2 ? ingredientIds.get(2) : bunId;
        List<String> orderIngredients = Arrays.asList(bunId, fillingId, bunId);

        StepOrder.createOrderWithToken(accessToken, orderIngredients, requestSpec);

        Response ordersResponse = StepOrder.getUserOrders(accessToken,requestSpec);

        ordersResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders.size()", greaterThanOrEqualTo(1))
                .body("orders[0].ingredients.size()", greaterThanOrEqualTo(1))
                .body("orders[0]._id", org.hamcrest.Matchers.not(""))
                .body("orders[0].status", org.hamcrest.Matchers.not(""))
                .body("orders[0].number", notNullValue())
                .body("orders[0].createdAt", org.hamcrest.Matchers.not(""))
                .body("orders[0].updatedAt", org.hamcrest.Matchers.not(""))
                .body("total", greaterThanOrEqualTo(1))
                .body("totalToday", greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Получение заказов без авторизации пользователя")
    public void getOrdersWithoutAuthShouldFail() {

        Response ordersResponse = given()
                .get(ApiUrl.ORDER);

        ordersResponse.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}