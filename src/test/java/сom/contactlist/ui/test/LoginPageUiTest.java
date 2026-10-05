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
import сom.contactlist.ui.pages.ContactListPage;
import сom.contactlist.ui.pages.CreateUserPages;
import сom.contactlist.ui.pages.LoginPage;

import org.openqa.selenium.chrome.ChromeOptions;


public class LoginPageUiTest extends UserApi {
    private WebDriver driver;
    private LoginPage loginPage;
    private ContactListPage contactListPage;
    private CreateUserPages createUserPages;
    Faker faker = new Faker();
    private String token;
    String userEmail;
    String userPassword;

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
        contactListPage = new ContactListPage(driver);
        createUserPages = new CreateUserPages(driver);
    }

    @Test
    public void loginUserSuccess() {
        userEmail = faker.internet().emailAddress();
        userPassword = faker.internet().password();
        NewUser newUser = NewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(userEmail)
                .password(userPassword).build();
        ValidatableResponse response = createUser(newUser);

        token = response.extract().path("token");

        loginPage.login(userEmail, userPassword);
        Assert.assertTrue("Button is not displayed", contactListPage.logoutButtonIsDisplayed());

    }

    @Test
    public void loginWithoutEmailShowsError() {
        loginPage.login("", faker.internet().password());

        String actualError = loginPage.getErrorMessageText();
        String expectedError = "Incorrect username or password";

        Assert.assertEquals("The error message text is incorrect!", expectedError, actualError);

    }

    @Test
    public void loginWithoutPasswordShowsError() {
        userEmail = faker.internet().emailAddress();
        NewUser newUser = NewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(userEmail)
                .password(faker.internet().password()).build();
        ValidatableResponse response = createUser(newUser);
        token = response.extract().path("token");

        loginPage.login(userEmail, "");

        String actualError = loginPage.getErrorMessageText();
        String expectedError = "Incorrect username or password";

        Assert.assertEquals("The error message text is incorrect!", expectedError, actualError);

    }
    @Test
    public void clickSignUpButtonOpensAddUserPage(){
        loginPage.clickSignUpButton();
        Assert.assertTrue("Sing up button opens add user page", createUserPages.addUserFormIsDisplayed());

    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (token != null) {
            deleteUser(token);
        }

    }
}
