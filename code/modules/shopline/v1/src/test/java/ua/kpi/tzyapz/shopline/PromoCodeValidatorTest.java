package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести правил BL-70 (валідація промокоду) та BL-80 (розрахунок знижки за промокодом).
 * Автор: Пінчук А. (СМ)
 */
@Tag("unit")
@DisplayName("PromoCodeValidator — перевірка та застосування промокодів (BL-70, BL-80)")
class PromoCodeValidatorTest {

    private PromoCodeValidator validator;
    private LocalDate today;
    private BigDecimal defaultSubtotal;

    @BeforeEach
    void setUp() {
        validator = new PromoCodeValidator();
        today = LocalDate.of(2026, 10, 26);
        defaultSubtotal = new BigDecimal("1000.00");
    }

    @ParameterizedTest(name = "BL-70: код ''{0}'' -> valid={1}")
    @CsvSource({
            "ABC12, false",        // 5 символів (менше нижньої межі 6)
            "ABC123, true",        // 6 символів (нижня межа)
            "ABCDE12345, true",    // 10 символів (верхня межа)
            "ABCDE123456, false",  // 11 символів (більше верхньої межі 10)
            "abc123, false",       // малі літери заборонені форматом A-Z0-9
            "ABC-123, false"       // спецсимволи заборонені
    })
    @DisplayName("BL-70: межі довжини (6..10) та формат символів (A-Z, 0-9)")
    void validate_codeLengthBoundaries(String code, boolean expectedValid) {
        PromoCode promo = new PromoCode(
                code, PromoCode.Type.PERCENT, new BigDecimal("10"),
                today.plusDays(5), false, false
        );

        PromoCodeValidator.ValidationResult result = validator.validate(promo, today, defaultSubtotal);

        assertAll(
                () -> assertEquals(expectedValid, result.valid()),
                () -> {
                    if (expectedValid) {
                        assertEquals("", result.reason());
                    } else {
                        assertFalse(result.reason().isBlank(), "Для невалідного коду має бути вказана причина");
                    }
                }
        );
    }

    // Падає на v1: виявляє закладений дефект модуля (промокод відхиляється в останній день дії today == expiresOn)
    @Test
    @DisplayName("BL-70: граничні значення дати закінчення дії (вчора, сьогодні включно, завтра)")
    void validate_expirationDateBoundary() {
        PromoCode expiredYesterday = new PromoCode(
                "PROMO1", PromoCode.Type.PERCENT, new BigDecimal("10"),
                today.minusDays(1), false, false
        );
        PromoCode expiresToday = new PromoCode(
                "PROMO2", PromoCode.Type.PERCENT, new BigDecimal("10"),
                today, false, false
        );
        PromoCode expiresTomorrow = new PromoCode(
                "PROMO3", PromoCode.Type.PERCENT, new BigDecimal("10"),
                today.plusDays(1), false, false
        );

        assertAll(
                () -> assertFalse(validator.validate(expiredYesterday, today, defaultSubtotal).valid(),
                        "Прострочений код (вчора) має бути відхилений"),
                () -> assertTrue(validator.validate(expiresToday, today, defaultSubtotal).valid(),
                        "Код із датою завершення today (останній день включно) має бути дійсним"),
                () -> assertTrue(validator.validate(expiresTomorrow, today, defaultSubtotal).valid(),
                        "Код із датою завершення завтра має бути дійсним")
        );
    }

    @ParameterizedTest(name = "BL-70: відсоток {0}% -> valid={1}")
    @CsvSource({
            "0, false",   // нижче мінімуму
            "1, true",    // нижня межа 1%
            "50, true",   // верхня межа 50%
            "51, false"   // вище максимуму
    })
    @DisplayName("BL-70: межі відсоткової знижки PERCENT (1..50)")
    void validate_percentageBoundaries(String percent, boolean expectedValid) {
        PromoCode promo = new PromoCode(
                "PERC10", PromoCode.Type.PERCENT, new BigDecimal(percent),
                today.plusDays(5), false, false
        );

        PromoCodeValidator.ValidationResult result = validator.validate(promo, today, defaultSubtotal);

        assertEquals(expectedValid, result.valid());
    }

    @ParameterizedTest(name = "BL-70: фіксована сума {0} грн при subtotal=1000.00 -> valid={1}")
    @CsvSource({
            "-0.01, false",   // від'ємна сума
            "0.00, false",    // нульова сума (має бути > 0)
            "0.01, true",     // мінімальна додатна сума
            "1000.00, true",  // рівно subtotal (верхня межа)
            "1000.01, false"  // перевищує subtotal
    })
    @DisplayName("BL-70: межі фіксованої знижки FIXED (> 0 та <= subtotal)")
    void validate_fixedAmountBoundaries(String amount, boolean expectedValid) {
        PromoCode promo = new PromoCode(
                "FIXED1", PromoCode.Type.FIXED, new BigDecimal(amount),
                today.plusDays(5), false, false
        );

        PromoCodeValidator.ValidationResult result = validator.validate(promo, today, defaultSubtotal);

        assertEquals(expectedValid, result.valid());
    }

    // Падає на v1: виявляє закладений дефект модуля (validate ігнорує прапорці singleUse=true та used=true)
    @Test
    @DisplayName("BL-70: використаний одноразовий промокод відхиляється")
    void validate_usedOneTimeCodeIsRejected() {
        PromoCode unusedSingleUse = new PromoCode(
                "SINGLE01", PromoCode.Type.PERCENT, new BigDecimal("15"),
                today.plusDays(5), true, false
        );
        PromoCode usedSingleUse = new PromoCode(
                "SINGLE02", PromoCode.Type.PERCENT, new BigDecimal("15"),
                today.plusDays(5), true, true
        );

        PromoCodeValidator.ValidationResult unusedResult = validator.validate(unusedSingleUse, today, defaultSubtotal);
        PromoCodeValidator.ValidationResult usedResult = validator.validate(usedSingleUse, today, defaultSubtotal);

        assertAll(
                () -> assertTrue(unusedResult.valid(), "Невикористаний одноразовий код має бути дійсним"),
                () -> assertFalse(usedResult.valid(), "Вже використаний одноразовий код має бути відхилений"),
                () -> assertFalse(usedResult.reason().isBlank(), "Має повертатися причина відмови для використаного коду")
        );
    }

    @Test
    @DisplayName("BL-80: відсоткова знижка обчислюється та округлюється за HALF_UP")
    void apply_percentageDiscountRoundsHalfUp() {
        // Arrange: 2333.33 * 5% = 116.6665 -> за HALF_UP очікуємо 116.67
        PromoCode promo = new PromoCode(
                "SALE05", PromoCode.Type.PERCENT, new BigDecimal("5"),
                today.plusDays(5), false, false
        );
        BigDecimal subtotal = new BigDecimal("2333.33");

        // Act
        BigDecimal discount = validator.apply(promo, today, subtotal);

        // Assert
        assertEquals(new BigDecimal("116.67"), discount);
    }

    @Test
    @DisplayName("BL-80: фіксована знижка повертає суму коду зі шкалою 2 знаки")
    void apply_fixedAmountDiscount() {
        PromoCode promo = new PromoCode(
                "FIX250", PromoCode.Type.FIXED, new BigDecimal("250.00"),
                today.plusDays(5), false, false
        );

        BigDecimal discount = validator.apply(promo, today, defaultSubtotal);

        assertEquals(new BigDecimal("250.00"), discount);
    }

    @Test
    @DisplayName("BL-80: застосування недійсного промокоду кидає IllegalArgumentException із причиною")
    void apply_invalidCodeThrowsWithReason() {
        PromoCode invalidPromo = new PromoCode(
                "BAD", PromoCode.Type.PERCENT, new BigDecimal("10"),
                today.plusDays(5), false, false
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.apply(invalidPromo, today, defaultSubtotal)
        );
        assertFalse(ex.getMessage().isBlank());
    }

    // Падає на v1: виявляє закладений дефект модуля (apply застосовує вже використаний одноразовий код)
    @Test
    @DisplayName("BL-80: спроба застосувати використаний одноразовий промокод кидає IllegalArgumentException")
    void apply_usedOneTimeCodeIsRejected() {
        PromoCode usedSingleUse = new PromoCode(
                "ONCE99", PromoCode.Type.FIXED, new BigDecimal("100.00"),
                today.plusDays(5), true, true
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.apply(usedSingleUse, today, defaultSubtotal)
        );
        assertFalse(ex.getMessage().isBlank());
    }
}
