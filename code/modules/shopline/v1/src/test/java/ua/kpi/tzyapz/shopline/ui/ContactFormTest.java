
package ua.kpi.tzyapz.shopline.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
public class ContactFormTest {

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
    @DisplayName("Перевірка валідації порожньої контактної форми")
    void emptyContactFormValidation() {
        driver.get("https://practicesoftwaretesting.com/contact");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form")
        ));

        WebElement form = driver.findElement(By.cssSelector("form"));

        // Надсилаємо порожню форму.
        form.findElement(By.cssSelector("button[type='submit'], input[type='submit']")).click();

        // Перевіряємо, що браузер або сайт не прийняв порожню форму.
        boolean invalidField = driver.findElements(
                By.cssSelector("form :invalid")
        ).size() > 0;

        boolean validationMessage = driver.findElements(
                By.cssSelector(
                        "[class*='invalid'], [class*='error'], .alert-danger"
                )
        ).stream().anyMatch(WebElement::isDisplayed);

        assertTrue(
                invalidField || validationMessage,
                "Empty contact form should display validation"
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
