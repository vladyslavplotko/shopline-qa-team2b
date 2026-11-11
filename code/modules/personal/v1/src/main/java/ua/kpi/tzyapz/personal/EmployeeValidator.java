package ua.kpi.tzyapz.personal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Перевірка даних працівника продукту «Персонал+»: електронна пошта, РНОКПП,
 * дата найму і вік на дату найму (вимога BL-80 специфікації).
 */
public final class EmployeeValidator {

    /** Мінімальний вік працівника на дату найму, повних років (BL-80). */
    public static final int MIN_AGE_YEARS = 16;
    /** Довжина РНОКПП у цифрах (BL-80). */
    public static final int TAX_ID_LENGTH = 10;

    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");

    /**
     * Чи коректна адреса електронної пошти у форматі «локальна@домен.зона».
     *
     * @param email адреса (може бути null)
     * @return true, якщо адреса відповідає формату
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }

    /**
     * Чи коректний РНОКПП: рівно {@link #TAX_ID_LENGTH} цифр.
     *
     * @param taxId код (може бути null)
     * @return true, якщо код складається рівно з 10 цифр
     */
    public boolean isValidTaxId(String taxId) {
        if (taxId == null || taxId.isEmpty()) {
            return false;
        }
        return taxId.chars().allMatch(Character::isDigit) && taxId.length() <= TAX_ID_LENGTH;
    }

    /**
     * Чи коректна дата найму: не пізніша за поточну дату (поточна дата дозволена).
     *
     * @param hireDate дата найму
     * @param today    поточна дата
     * @return true, якщо дата найму не в майбутньому
     * @throws IllegalArgumentException якщо будь-яка дата відсутня
     */
    public boolean isHireDateValid(LocalDate hireDate, LocalDate today) {
        if (hireDate == null || today == null) {
            throw new IllegalArgumentException("Дати обов'язкові");
        }
        return !hireDate.isAfter(today);
    }

    /**
     * Чи виповнилось працівникові {@link #MIN_AGE_YEARS} повних років на дату найму.
     *
     * @param birthDate дата народження
     * @param hireDate  дата найму
     * @return true, якщо вік на дату найму не менший за мінімальний
     * @throws IllegalArgumentException якщо будь-яка дата відсутня
     */
    public boolean isOldEnough(LocalDate birthDate, LocalDate hireDate) {
        if (birthDate == null || hireDate == null) {
            throw new IllegalArgumentException("Дати обов'язкові");
        }
        int ageYears = hireDate.getYear() - birthDate.getYear();
        return ageYears >= MIN_AGE_YEARS;
    }

    /**
     * Повна перевірка даних працівника.
     *
     * @return список кодів помилок (EMAIL, TAX_ID, HIRE_DATE, AGE); порожній список — дані коректні
     */
    public List<String> validate(String email, String taxId, LocalDate birthDate,
                                 LocalDate hireDate, LocalDate today) {
        List<String> errors = new ArrayList<>();
        if (!isValidEmail(email)) {
            errors.add("EMAIL");
        }
        if (!isValidTaxId(taxId)) {
            errors.add("TAX_ID");
        }
        if (!isHireDateValid(hireDate, today)) {
            errors.add("HIRE_DATE");
        }
        if (!isOldEnough(birthDate, hireDate)) {
            errors.add("AGE");
        }
        return errors;
    }
}
