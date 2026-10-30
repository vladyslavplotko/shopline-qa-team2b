package ua.kpi.tzyapz.shopline;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PromoCode(String code, Type type, BigDecimal value, LocalDate expiresOn, boolean oneTime) {
    public enum Type { PERCENT, FIXED }
}
