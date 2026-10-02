package сom.contactlist.ui.test;

import com.contactlist.api.model.NewUser;
import com.contactlist.api.specs.UserApi;
import com.github.javafaker.Faker;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import сom.contactlist.ui.pages.LoginPage;
import org.openqa.selenium.chrome.ChromeOptions;


public class LoginUiTest extends UserApi {
    private WebDriver driver;
    private LoginPage loginPage;
    Faker faker = new Faker();
    private String token;
     String randomEmail;

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        if (System.getenv("CI") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
        driver.get("https://thinking-tester-contact-list.herokuapp.com/");
        loginPage = new LoginPage(driver);
    }

    @Test
    public void testInvalidLoginShowsError() {
        loginPage.login("", faker.internet().password());

        String actualError = loginPage.getErrorMessageText();
        String expectedError = "Incorrect username or password";

        Assert.assertEquals("The error message text is incorrect!", expectedError, actualError);

    }
    @Test
    public void testInvalidPasswordShowsError() {
        randomEmail = faker.internet().emailAddress();
        NewUser newUser = NewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(randomEmail)
                .password(faker.internet().password()).build();
        ValidatableResponse response = createUser(newUser);
        token = response.extract().path("token");

        loginPage.login(randomEmail, "");

        String actualError = loginPage.getErrorMessageText();
        String expectedError = "Incorrect username or password";

        Assert.assertEquals("The error message text is incorrect!", expectedError, actualError);

    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (token != null) {
            ValidatableResponse deleteResponse = deleteUser(token);
        }

    }
}
