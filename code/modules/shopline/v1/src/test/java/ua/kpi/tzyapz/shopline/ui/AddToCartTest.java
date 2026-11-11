
package ua.kpi.tzyapz.shopline.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class AddToCartTest {

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
    void addProductToCart() {
        driver.get("https://practicesoftwaretesting.com");

        // Відкриваємо перший товар.
        WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[data-test^='product-']")
                )
        );

        firstProduct.click();

        // Чекаємо завантаження сторінки товару.
        WebElement productName = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='product-name']")
                )
        );

        String expectedProductName = productName.getText().trim();

        assertFalse(
                expectedProductName.isEmpty(),
                "Product name should not be empty"
        );

        // Додаємо одну одиницю товару.
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("[data-test='add-to-cart']")
                )
        ).click();

        // Перевіряємо лічильник кошика.
        WebElement cartQuantity = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='cart-quantity']")
                )
        );

        assertEquals(
                "1",
                cartQuantity.getText().trim(),
                "Cart should contain exactly one product"
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
