package сom.contactlist.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CreateUserPages {
    private WebDriver driver;

    public CreateUserPages (WebDriver driver){
        this.driver = driver;
    }
    private final By eddForm = By.id("add-user");

    public boolean addUserFormIsDisplayed(){
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(eddForm));
            try {
                return driver.findElement(eddForm).isDisplayed();
            } catch (org.openqa.selenium.NoSuchElementException e) {
                return false;
            }


    }

}
