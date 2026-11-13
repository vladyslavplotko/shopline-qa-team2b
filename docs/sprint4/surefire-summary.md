# Протокол запуску автотестів (Maven Surefire) — Спринт 4

**Продукт:** «ШопЛайн» (`shopline-module` v1.0.0)  
**Дата виконання:** 13.11.2026  
**Відповідальний:** Прохоренко М.П. (Керівник тестування / КТ)  
**Середовище:** Windows 11, JDK 17 (Eclipse Temurin), Apache Maven 3.9, JUnit Jupiter 5.10.2, Selenium WebDriver 4.21.0, Google Chrome 140.0 (headless `1280×900`).

---

## 1. Команди запуску

Робочий Maven-проєкт модуля розташований у директорії `code/modules/shopline/v1/`. Команди для запуску з кореня репозиторію:

- Чистий запуск юніт-тестів бізнес-логіки (група `ui` виключена за замовчуванням):
  `mvn -f code/modules/shopline/v1/pom.xml clean test`
- Повний запуск (юніт-тести + UI-тести Selenium WebDriver):
  `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none`
- Запуск виключно UI-тестів:
  `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui`

---

## 2. Зведений результат прогону юніт-тестів (`mvn clean test`)

На версії модуля `v1` юніт-тести JUnit 5 виявили **всі 6 закладених дефектів бізнес-логіки** (`BUG-010` – `BUG-015`), а також **1 додатковий дефект валідації назви товару** (`BUG-016` за вимогою `BL-10` з `SRS-1.1`). Відповідні тести залишено у наборі «червоними» згідно з розділом 4.9 методички для верифікації виправлень у версії `v2` (Спринт 5).

| Тест-клас | Вимоги SRS-1.1 | Тест-методів | Автори | Пов'язані баг-репорти |
| :--- | :--- | :---: | :--- | :--- |
| `ExampleTest` | BL-40, BL-50, BL-60 | 2 | Стартовий модуль | — |
| `CartTest` | BL-10, BL-20, BL-30, BL-40 | 17 | Плотко В. (6), Касян С. (11) | `BUG-010`, `BUG-011`, `BUG-016` |
| `DiscountCalculatorTest` | BL-50, BL-60 | 8 | Прохоренко М. (8) | `BUG-014`, `BUG-015` |
| `PromoCodeValidatorTest` | BL-70, BL-80 | 9 | Пінчук А. (9) | `BUG-012`, `BUG-013` |
| **Разом (Unit)** | **BL-10 .. BL-80** | **36** | **Уся команда (34 авторські + 2 базові)** | **`BUG-010` .. `BUG-016`** |

---

## 3. Перелік дефектів модуля `v1`, зафіксованих тестами зі статусом `FAILURE`

| ID дефекту | Вимога | Тест-клас і метод | Очікуваний результат (за SRS-1.1) | Фактичний результат (модуль v1) | Причина в коді `v1` |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`BUG-010`** | BL-20, BL-30 | `CartTest` (повторне додавання наявного `sku`) | Кількість наявного рядка збільшується на `qty` | Новий виклик перезаписує стару кількість | У `Cart.addItem` викликається `items.put(...)` без додавання `existing.qty() + qty` |
| **`BUG-011`** | BL-10, BL-40 | `CartTest` (перевірка верхньої межі `qty = 100`) | `IllegalArgumentException` (максимум 99 од.) | Позиція додається / оновлюється з `qty = 100` | У `Cart.java` константа `MAX_QTY = 100` замість `99` |
| **`BUG-012`** | BL-70 | `PromoCodeValidatorTest#validate_expirationDateBoundary` | `valid = true` (останній день дії включно) | `valid = false` («Термін дії промокоду минув») | У `PromoCodeValidator.validate` умова `!today.isBefore(promo.expiresOn())` відхиляє рівні дати |
| **`BUG-013`** | BL-70, BL-80 | `PromoCodeValidatorTest#validate_usedOneTimeCodeIsRejected` | `valid = false` / `IllegalArgumentException` у `apply` | `valid = true`, промокод застосовується повторно | У `PromoCodeValidator.validate` відсутня перевірка `promo.singleUse() && promo.used()` |
| **`BUG-014`** | BL-50 | `DiscountCalculatorTest#volumeDiscountPercent_respectsAllThresholds` (`5000.00`) | `10` (10% знижки від 5000.00 грн включно) | `5` (`expected: <10> but was: <5>`) | У `DiscountCalculator.volumeDiscountPercent` стоїть `compareTo(TIER_10) > 0` замість `>= 0` |
| **`BUG-015`** | BL-50 | `DiscountCalculatorTest#volumeDiscount_roundsHalfUp` (`2333.33`) | `116.67` (округлення `116.6665` за `HALF_UP`) | `116.66` (`expected: <116.67> but was: <116.66>`) | У `DiscountCalculator.volumeDiscount` використано `RoundingMode.DOWN` замість `HALF_UP` |
| **`BUG-016`** | BL-10 | `CartTest#addItem_rejectsBlankName` | `IllegalArgumentException` при порожньому `name` (`null`, `""`, `"   "`) | Позиція додається до кошика без винятку | У `Cart.addItem` відсутня валідація параметра `name` |

---

## 4. Результат прогону UI-тестів Selenium WebDriver (`@Tag("ui")`)

Команда запуску: `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui`

| Клас UI-тесту | Тест-кейс Спринту 3 | Сценарій | Статус |
| :--- | :--- | :--- | :---: |
| `ui.LoginTest` | `TC-004`, `TC-005` | Успішний вхід покупця та відмова при невірному паролі | `PASSED` (2/2) |
| `ui.SearchHammerTest` | `TC-010` | Пошук товару за словом «Hammer» та очищення пошуку | `PASSED` (1/1) |
| `ui.AddToCartTest` | `TC-012` | Додавання товару до кошика з кількістю 1 та перевірка лічильника | `PASSED` (1/1) |
| `ui.AddToCart99Test` | `TC-013` | Додавання товару з граничною кількістю 99 одиниць | `PASSED` (1/1) |
| `ui.ContactFormTest` | `TC-018` | Перевірка обов'язкових полів порожньої форми зворотного зв'язку | `PASSED` (1/1) |
| **Разом (UI WebDriver)** | **6 сценаріїв** | **Усі 6 UI-сценаріїв проходять стабільно** | **`Tests run: 6, Failures: 0, Errors: 0`** |
