package ua.kpi.tzyapz.shopline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class PromoCodeValidator {
    public record ValidationResult(boolean valid, String reason) {}
    private final Set<String> used = new HashSet<>();

    public ValidationResult validate(PromoCode promo, LocalDate today, BigDecimal subtotal) {
        if (promo == null || today == null || subtotal == null || subtotal.signum() < 0)
            return new ValidationResult(false, "Invalid input");
        String code = promo.code();
        if (code == null || code.length() < 6 || code.length() > 10 || !code.matches("[A-Za-z0-9]+"))
            return new ValidationResult(false, "Invalid code format");
        if (promo.expiresOn() == null || promo.expiresOn().isBefore(today))
            return new ValidationResult(false, "Expired code");
        if (promo.oneTime() && used.contains(code))
            return new ValidationResult(false, "Already used");
        if (promo.type() == null || promo.value() == null) return new ValidationResult(false, "Invalid discount");
        if (promo.type() == PromoCode.Type.PERCENT &&
                (promo.value().compareTo(BigDecimal.ONE) < 0 || promo.value().compareTo(new BigDecimal("50")) > 0))
            return new ValidationResult(false, "Percent must be 1..50");
        if (promo.type() == PromoCode.Type.FIXED &&
                (promo.value().signum() <= 0 || promo.value().compareTo(subtotal) > 0))
            return new ValidationResult(false, "Invalid fixed discount");
        return new ValidationResult(true, "");
    }

    public BigDecimal apply(PromoCode promo, LocalDate today, BigDecimal subtotal) {
        ValidationResult result = validate(promo, today, subtotal);
        if (!result.valid()) throw new IllegalArgumentException(result.reason());
        BigDecimal discount = promo.type() == PromoCode.Type.PERCENT
                ? subtotal.multiply(promo.value()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                : promo.value().setScale(2, RoundingMode.HALF_UP);
        // seeded defect: one-time codes are not marked used after applying
        return discount;
    }
}
