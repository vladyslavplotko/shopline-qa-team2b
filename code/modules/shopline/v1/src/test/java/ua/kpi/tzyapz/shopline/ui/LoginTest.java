
package ua.kpi.tzyapz.shopline.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1280,900");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("TC-004 / F-20: Успішний вхід зареєстрованого покупця")
    void successfulCustomerLogin() {
        driver.get("https://practicesoftwaretesting.com/auth/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")))
                .sendKeys("customer@practicesoftwaretesting.com");

        driver.findElement(By.id("password")).sendKeys("welcome01");
        driver.findElement(By.cssSelector("[data-test='login-submit']")).click();

        wait.until(ExpectedConditions.urlContains("/account"));

        assertTrue(
                driver.getCurrentUrl().contains("/account"),
                "Customer should be redirected to the account page"
        );
    }

    @Test
    @DisplayName("TC-005 / F-20: Відображення помилки при вході з неправильним паролем")
    void loginWithWrongPasswordShowsError() {
        driver.get("https://practicesoftwaretesting.com/auth/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")))
                .sendKeys("customer@practicesoftwaretesting.com");

        driver.findElement(By.id("password")).sendKeys("WrongPass!2026");
        driver.findElement(By.cssSelector("[data-test='login-submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));

        assertFalse(
                driver.getCurrentUrl().contains("/account"),
                "User must not enter the account with an incorrect password"
        );

        assertTrue(
                driver.findElement(By.id("email")).isDisplayed(),
                "Login form should remain visible"
        );

        assertTrue(
                driver.findElement(By.id("password")).isDisplayed(),
                "Password field should remain visible"
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
