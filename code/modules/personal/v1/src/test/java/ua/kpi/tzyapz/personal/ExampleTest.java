package ua.kpi.tzyapz.personal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Зразок юніт-тестів модуля «Персонал+» (стиль для власних тестів команди). */
@DisplayName("Персонал+ — зразок юніт-тестів модуля")
class ExampleTest {

    private LeaveCalculator leave;
    private EmployeeValidator validator;

    @BeforeEach
    void setUp() {
        leave = new LeaveCalculator();
        validator = new EmployeeValidator();
    }

    @ParameterizedTest(name = "BL-20: carryOver({0}) = {1}")
    @CsvSource({"0,0", "3,3", "5,5", "9,5"})
    void carryOverIsCappedAtFive(int unused, int expected) {
        assertEquals(expected, leave.carryOver(unused));
    }

    @Test
    @DisplayName("BL-80: адреса електронної пошти має формат локальна@домен.зона")
    void emailFormat() {
        assertAll(
            () -> assertTrue(validator.isValidEmail("ivan.petrenko@personal.plus")),
            () -> assertFalse(validator.isValidEmail("ivan.petrenko@")),
            () -> assertFalse(validator.isValidEmail("ivan@localhost"))
        );
    }
}
