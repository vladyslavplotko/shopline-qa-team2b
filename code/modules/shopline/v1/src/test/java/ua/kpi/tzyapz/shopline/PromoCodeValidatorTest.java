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
 * Рецензент: Прохоренко М. (КТ)
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

    @ParameterizedTest(name = "BL-70: код ''{0}'' (довжина) -> valid={1}")
    @CsvSource({
            "ABC12, false",        // 5 символів (менше нижньої межі 6)
            "ABC123, true",        // 6 символів (нижня межа)
            "ABCDE12345, true",    // 10 символів (верхня межа)
            "ABCDE123456, false"   // 11 символів (більше верхньої межі 10)
    })
    @DisplayName("BL-70: межі довжини промокоду (5, 6, 10, 11 символів)")
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

    // BUG-012 (Issue #22): падає на v1, бо !today.isBefore(expiresOn) відхиляє код в останній день дії (today == expiresOn)
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
    @DisplayName("BL-70: межі відсоткової знижки PERCENT (0, 1, 50, 51)")
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
            "1000.01, false"  // перевищує subtotal (subtotal + 0.01)
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

    // BUG-013 (Issue #23): падає на v1, бо validate() ігнорує прапорці singleUse=true та used=true
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
        PromoCode promo = new PromoCode(
                "SALE05", PromoCode.Type.PERCENT, new BigDecimal("5"),
                today.plusDays(5), false, false
        );
        BigDecimal subtotal = new BigDecimal("2333.33");

        BigDecimal discount = validator.apply(promo, today, subtotal);

        assertEquals(new BigDecimal("116.67"), discount);
    }

    @Test
    @DisplayName("BL-80: фіксована знижка повертає суму коду на межах (0.01, проміжне значення, рівно subtotal)")
    void apply_fixedAmountDiscount() {
        PromoCode minFixed = new PromoCode(
                "FIX001", PromoCode.Type.FIXED, new BigDecimal("0.01"),
                today.plusDays(5), false, false
        );
        PromoCode midFixed = new PromoCode(
                "FIX250", PromoCode.Type.FIXED, new BigDecimal("250.00"),
                today.plusDays(5), false, false
        );
        PromoCode maxFixed = new PromoCode(
                "FIXMAX", PromoCode.Type.FIXED, new BigDecimal("1000.00"),
                today.plusDays(5), false, false
        );

        assertAll(
                () -> assertEquals(new BigDecimal("0.01"), validator.apply(minFixed, today, defaultSubtotal)),
                () -> assertEquals(new BigDecimal("250.00"), validator.apply(midFixed, today, defaultSubtotal)),
                () -> assertEquals(new BigDecimal("1000.00"), validator.apply(maxFixed, today, defaultSubtotal))
        );
    }

    @Test
    @DisplayName("BL-80: застосування недійсного коду або суми > subtotal (subtotal + 0.01) кидає IllegalArgumentException")
    void apply_invalidCodeThrowsWithReason() {
        PromoCode shortCode = new PromoCode(
                "BAD", PromoCode.Type.PERCENT, new BigDecimal("10"),
                today.plusDays(5), false, false
        );
        PromoCode aboveSubtotalFixed = new PromoCode(
                "OVER99", PromoCode.Type.FIXED, new BigDecimal("1000.01"),
                today.plusDays(5), false, false
        );

        assertAll(
                () -> {
                    IllegalArgumentException ex = assertThrows(
                            IllegalArgumentException.class,
                            () -> validator.apply(shortCode, today, defaultSubtotal)
                    );
                    assertFalse(ex.getMessage().isBlank());
                },
                () -> {
                    IllegalArgumentException ex = assertThrows(
                            IllegalArgumentException.class,
                            () -> validator.apply(aboveSubtotalFixed, today, defaultSubtotal)
                    );
                    assertFalse(ex.getMessage().isBlank());
                }
        );
    }

    // BUG-013 (Issue #23): падає на v1, бо apply() не відхиляє вже використаний одноразовий промокод
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
