package base;

public class ApiUrl {
    public static final String BASE_URL = "https://stellarburgers.education-services.ru";
    // Создание (регистрация) пользователя
    public static final String CREATE_USER = "api/auth/register";
    // Авторизация (логин) пользоваетля
    public static final String LOGIN_USER = "api/auth/login";
    // Изменение данных пользователя, удаление пользователя
    public static final String USER = "api/auth/user";
    // Получение ингредиентов для создания заказа
    public static final String INGREDIENTS = "api/ingredients";
    // Получение заказов пользователя, создание заказа
    public static final String ORDER = "api/orders";
}
