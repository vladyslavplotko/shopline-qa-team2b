
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
public class SearchHammerTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final By searchField =
            By.cssSelector("[data-test='search-query']");

    private final By searchButton =
            By.cssSelector("[data-test='search-submit']");

    private final By productNames =
            By.cssSelector("[data-test='product-name']");

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1280,900");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    void searchHammerAndClearSearch() {
        driver.get("https://practicesoftwaretesting.com");

        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(searchField)
        );

        field.sendKeys("Hammer");

        driver.findElement(searchButton).click();

        // Чекаємо на 6 результатів, кожен з яких містить Hammer.
        wait.until(d -> {
            List<WebElement> names = d.findElements(productNames);

            return names.size() == 6
                    && names.stream().allMatch(name ->
                    name.getText().toLowerCase().contains("hammer"));
        });

        List<WebElement> results = driver.findElements(productNames);

        assertEquals(6, results.size(),
                "Search should return 6 Hammer products");

        for (WebElement product : results) {
            assertTrue(
                    product.getText().toLowerCase().contains("hammer"),
                    "Every search result should contain Hammer"
            );
        }

        // Очищуємо поле пошуку.
        field = driver.findElement(searchField);
        field.clear();

        assertEquals(
                "",
                field.getAttribute("value"),
                "Search field should be empty after clearing"
        );

        // Повторно запускаємо пошук із порожнім полем.
        driver.findElement(searchButton).click();

        assertEquals(
                "",
                driver.findElement(searchField).getAttribute("value"),
                "Search field should remain empty"
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
