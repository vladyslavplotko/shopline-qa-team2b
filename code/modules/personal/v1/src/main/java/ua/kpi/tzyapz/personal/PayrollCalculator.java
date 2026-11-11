package ua.kpi.tzyapz.personal;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Розрахунок нарахувань продукту «Персонал+»: сума «на руки», годинна ставка
 * і оплата понаднормових (вимоги BL-50 … BL-70 специфікації).
 * Усі суми — у гривнях, {@link BigDecimal}, округлення HALF_UP до 2 знаків.
 */
public final class PayrollCalculator {

    /** Ставка податку на доходи фізичних осіб, частка (BL-50). */
    public static final BigDecimal PIT_RATE = new BigDecimal("0.18");
    /** Ставка військового збору, частка (BL-50). */
    public static final BigDecimal MILITARY_RATE = new BigDecimal("0.05");
    /** Норма робочих годин на місяць (BL-60). */
    public static final BigDecimal HOURS_PER_MONTH = new BigDecimal("168");
    /** Коефіцієнт оплати понаднормових годин (BL-70). */
    public static final BigDecimal OVERTIME_COEF = new BigDecimal("1.5");

    private static final int SCALE = 2;

    /**
     * Сума «на руки» після утримання ПДФО та військового збору (BL-50).
     *
     * @param gross нарахована сума, не менше 0
     * @return сума після утримань, округлена до копійок
     * @throws IllegalArgumentException якщо gross відсутня або від'ємна
     */
    public BigDecimal net(BigDecimal gross) {
        requireNonNegative(gross, "gross");
        BigDecimal afterPit = gross.subtract(gross.multiply(PIT_RATE));
        BigDecimal military = afterPit.multiply(MILITARY_RATE);
        return afterPit.subtract(military).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Годинна ставка: місячний оклад, поділений на норму годин (BL-60).
     *
     * @param monthly місячний оклад, не менше 0
     * @return ставка за годину, округлена до копійок
     * @throws IllegalArgumentException якщо monthly відсутній або від'ємний
     */
    public BigDecimal hourlyRate(BigDecimal monthly) {
        requireNonNegative(monthly, "monthly");
        return monthly.divide(HOURS_PER_MONTH, SCALE, RoundingMode.DOWN);
    }

    /**
     * Оплата понаднормових: годинна ставка × 1,5 × кількість годин (BL-70).
     *
     * @param monthly місячний оклад, не менше 0
     * @param hours   понаднормові години (можуть бути дробовими), не менше 0
     * @return сума оплати, округлена до копійок
     * @throws IllegalArgumentException якщо аргумент відсутній або від'ємний
     */
    public BigDecimal overtimePay(BigDecimal monthly, BigDecimal hours) {
        requireNonNegative(hours, "hours");
        return hourlyRate(monthly).multiply(OVERTIME_COEF).multiply(hours)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    private static void requireNonNegative(BigDecimal value, String name) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException(name + " має бути >= 0");
        }
    }
}
