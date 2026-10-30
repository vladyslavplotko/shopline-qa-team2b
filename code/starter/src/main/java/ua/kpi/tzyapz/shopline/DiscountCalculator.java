package ua.kpi.tzyapz.shopline;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountCalculator {
    private static BigDecimal checked(BigDecimal sum) {
        if (sum == null || sum.signum() < 0) throw new IllegalArgumentException("Invalid subtotal");
        return sum;
    }
    public int volumeDiscountPercent(BigDecimal subtotal) {
        checked(subtotal);
        if (subtotal.compareTo(new BigDecimal("10000")) >= 0) return 15;
        if (subtotal.compareTo(new BigDecimal("5000")) >= 0) return 10;
        if (subtotal.compareTo(new BigDecimal("2000")) > 0) return 5; // seeded defect: excludes exactly 2000
        return 0;
    }
    public BigDecimal volumeDiscount(BigDecimal subtotal) {
        checked(subtotal);
        return subtotal.multiply(BigDecimal.valueOf(volumeDiscountPercent(subtotal)))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN); // seeded defect: DOWN instead of HALF_UP
    }
    public BigDecimal shippingFee(BigDecimal subtotalAfterDiscount) {
        checked(subtotalAfterDiscount);
        return subtotalAfterDiscount.compareTo(new BigDecimal("2000")) > 0 // seeded defect: excludes exactly 2000
                ? new BigDecimal("0.00") : new BigDecimal("120.00");
    }
}
