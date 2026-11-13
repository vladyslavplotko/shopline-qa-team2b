package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести для класу DiscountCalculator за вимогами BL-50 (обсягова знижка)
 * та BL-60 (вартість доставки).
 * Автор: Прохоренко М.П. (КТ)
 */
@Tag("unit")
@DisplayName("DiscountCalculator — обсягова знижка (BL-50) та вартість доставки (BL-60)")
class DiscountCalculatorTest {

    private DiscountCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new DiscountCalculator();
    }

    @AfterEach
    void tearDown() {
        calc = null;
    }

    // ==================== BL-50: Обсягова знижка ====================

    // Дефект модуля v1: BUG-014 (на межі 5000.00 грн повертає 5% замість 10% через '>' замість '>=')
    @ParameterizedTest(name = "BL-50: subtotal={0} грн -> очікується знижка {1}%")
    @CsvSource({
            "1999.99, 0",
            "2000.00, 5",
            "2000.01, 5",
            "4999.99, 5",
            "5000.00, 10",
            "5000.01, 10",
            "9999.99, 10",
            "10000.00, 15",
            "10000.01, 15"
    })
    @DisplayName("BL-50: відсоток обсягової знижки на всіх граничних значеннях порогів (2000, 5000, 10000)")
    void volumeDiscountPercent_respectsAllThresholds(String subtotalStr, int expectedPercent) {
        BigDecimal subtotal = new BigDecimal(subtotalStr);
        int actualPercent = calc.volumeDiscountPercent(subtotal);
        assertEquals(expectedPercent, actualPercent,
                "Невірний відсоток знижки для суми " + subtotalStr + " грн");
    }

    @ParameterizedTest(name = "BL-50: subtotal={0} грн -> сума знижки 0.00 грн")
    @ValueSource(strings = {"0.00", "0.01", "150.00", "1999.99"})
    @DisplayName("BL-50: для суми нижче 2000.00 грн сума знижки дорівнює 0.00 грн")
    void volumeDiscount_belowThresholdReturnsZero(String subtotalStr) {
        BigDecimal subtotal = new BigDecimal(subtotalStr);
        BigDecimal actualDiscount = calc.volumeDiscount(subtotal);
        assertEquals(new BigDecimal("0.00"), actualDiscount);
    }

    // Дефект модуля v1: BUG-015 (використано RoundingMode.DOWN замість RoundingMode.HALF_UP: 116.66 замість 116.67)
    @Test
    @DisplayName("BL-50: сума знижки округлюється до копійок за правилом HALF_UP (2333.33 * 5% = 116.67)")
    void volumeDiscount_roundsHalfUp() {
        BigDecimal subtotal = new BigDecimal("2333.33");
        BigDecimal expectedDiscount = new BigDecimal("116.67");
        BigDecimal actualDiscount = calc.volumeDiscount(subtotal);
        assertEquals(expectedDiscount, actualDiscount);
    }

    @Test
    @DisplayName("BL-50: застосовується лише найвищий рівень знижки (15% для 10000.00 грн), рівні не сумуються")
    void volumeDiscount_appliesOnlyHighestTier() {
        BigDecimal subtotal = new BigDecimal("10000.00");
        BigDecimal expectedDiscount = new BigDecimal("1500.00");
        BigDecimal actualDiscount = calc.volumeDiscount(subtotal);
        assertEquals(expectedDiscount, actualDiscount);
    }

    @Test
    @DisplayName("BL-50: від'ємна сума або null у volumeDiscount кидає IllegalArgumentException")
    void volumeDiscount_negativeSubtotalThrows() {
        BigDecimal negativeSubtotal = new BigDecimal("-0.01");
        assertAll("Некоректний вхід для volumeDiscount",
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calc.volumeDiscount(negativeSubtotal)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calc.volumeDiscount(null))
        );
    }

    // ==================== BL-60: Вартість доставки ====================

    @ParameterizedTest(name = "BL-60: сума після знижки={0} грн -> доставка {1} грн")
    @CsvSource({
            "1499.99, 120.00",
            "1500.00, 0.00",
            "1500.01, 0.00"
    })
    @DisplayName("BL-60: граничні значення порогу безкоштовної доставки (1499.99, 1500.00, 1500.01)")
    void shippingFee_respects2000Threshold(String subtotalAfterDiscountStr, String expectedFeeStr) {
        BigDecimal subtotalAfterDiscount = new BigDecimal(subtotalAfterDiscountStr);
        BigDecimal expectedFee = new BigDecimal(expectedFeeStr);
        BigDecimal actualFee = calc.shippingFee(subtotalAfterDiscount);
        assertEquals(expectedFee, actualFee);
    }

    @Test
    @DisplayName("BL-60: для сум нижче порогу безкоштовної доставки нараховується 120.00 грн")
    void shippingFee_returns120BelowThreshold() {
        BigDecimal expectedFee = new BigDecimal("120.00");
        assertAll("Вартість доставки нижче порогу",
                () -> assertEquals(expectedFee, calc.shippingFee(new BigDecimal("0.00"))),
                () -> assertEquals(expectedFee, calc.shippingFee(new BigDecimal("500.00"))),
                () -> assertEquals(expectedFee, calc.shippingFee(new BigDecimal("1499.99")))
        );
    }

    @Test
    @DisplayName("BL-60: від'ємна сума або null у shippingFee кидає IllegalArgumentException")
    void shippingFee_negativeSubtotalThrows() {
        BigDecimal negativeSubtotal = new BigDecimal("-0.01");
        assertAll("Некоректний вхід для shippingFee",
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calc.shippingFee(negativeSubtotal)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calc.shippingFee(null))
        );
    }
}
