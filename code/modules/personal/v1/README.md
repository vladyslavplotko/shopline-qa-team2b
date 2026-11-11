# Персонал+ — модуль бізнес-логіки, версія 1.0.0

Maven-проєкт продукту П3 «Персонал+» (пакет `ua.kpi.tzyapz.personal`). Реалізує правила розділу 5
«Вимоги до бізнес-логіки модуля» специфікації `SRS_03_Персонал_плюс.html`.

## Як зібрати і запустити тести

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS; на Windows/Linux вкажіть шлях до JDK 17
mvn -q -B test
```

Звіти Surefire з'являються у `target/surefire-reports/`. Власні тести додавайте у
`src/test/java/ua/kpi/tzyapz/personal/` (зразок стилю — `ExampleTest.java`).

## Що всередині

| Клас | Методи | Правила SRS |
|---|---|---|
| `LeaveCalculator` | `accrued(monthsWorked)`, `carryOver(unused)`, `durationDays(start, end)`, `canRequest(balance, duration)` | BL-10, BL-20, BL-30, BL-40 |
| `PayrollCalculator` | `net(gross)`, `hourlyRate(monthly)`, `overtimePay(monthly, hours)` | BL-50, BL-60, BL-70 |
| `EmployeeValidator` | `isValidEmail(email)`, `isValidTaxId(taxId)`, `isHireDateValid(hireDate, today)`, `isOldEnough(birthDate, hireDate)`, `validate(...)` | BL-80 |

Угоди: дати — `java.time.LocalDate`; гроші — `java.math.BigDecimal` з округленням `HALF_UP`
до 2 знаків; параметри (ставки податків, норма годин, ліміти днів) — публічні константи класів.
Некоректні аргументи (від'ємні суми, відсутні дати, початок пізніше за кінець) —
`IllegalArgumentException`.
