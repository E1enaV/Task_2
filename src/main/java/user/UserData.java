package user;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;
@Getter
@AllArgsConstructor
public class UserData {

    private final String email;
    private final String password;
    private final String name;

    private static final Faker faker = new Faker(new Locale("ru"));
    public static UserData generateRandom() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 10);
        String name = faker.name().firstName();

        return new UserData(email, password, name);
    }
}