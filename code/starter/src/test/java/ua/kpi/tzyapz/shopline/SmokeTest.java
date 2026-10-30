package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Перевірка, що збірка і запуск JUnit працюють. Можна видалити, коли з'являться справжні тести. */
class SmokeTest {
    @Test
    @DisplayName("Smoke: JUnit 5 запускається")
    void junitRuns() {
        assertEquals(4, 2 + 2);
    }
}
