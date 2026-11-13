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

На версії модуля `v1` юніт-тести JUnit 5 виявили **6 закладених дефектів бізнес-логіки** (`BUG-010` – `BUG-015`). Відповідні тести залишено у наборі «червоними» згідно з вимогами розділу 4.9 методички для перевірки виправлень у версії `v2` (Спринт 5).

| Тест-клас | Вимоги SRS-1.1 | Тест-методів | Запусків (Runs) | Passed | Failures (дефекти v1) | Errors | Skipped | Пов'язані баг-репорти |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| `ExampleTest` | BL-40, BL-50, BL-60 | 2 | 2 | 2 | 0 | 0 | 0 | — |
| `CartTest` | BL-10, BL-20, BL-30, BL-40 | 15 | 25 | 22 | 3 | 0 | 0 | `BUG-010`, `BUG-011` |
| `DiscountCalculatorTest` | BL-50, BL-60 | 8 | 21 | 19 | 2 | 0 | 0 | `BUG-014`, `BUG-015` |
| `PromoCodeValidatorTest` | BL-70, BL-80 | 9 | 20 | 17 | 3 | 0 | 0 | `BUG-012`, `BUG-013` |
| **Разом (Unit)** | **BL-10 .. BL-80** | **34** | **68** | **60** | **8 (6 багів)** | **0** | **0** | **`BUG-010` .. `BUG-015`** |

---

## 3. Перелік тестів зі статусом `FAILURE` на модулі `v1` та трасування до дефектів

| ID дефекту | Вимога | Тест-клас і метод | Очікуваний результат (за SRS-1.1) | Фактичний результат (модуль v1) | Причина в коді `v1` |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`BUG-010`** | BL-10, BL-40 | `CartTest` (перевірка верхньої межі `qty = 100`) | `IllegalArgumentException` (максимум 99 од.) | Позиція додається / оновлюється з `qty = 100` | У `Cart.java` константа `MAX_QTY = 100` замість `99` |
| **`BUG-011`** | BL-20, BL-30 | `CartTest` (повторне додавання наявного `sku`) | Кількість наявного рядка збільшується на `qty` | Новий виклик перезаписує стару кількість | У `Cart.addItem` викликається `items.put(...)` без додавання `existing.qty() + qty` |
| **`BUG-012`** | BL-70 | `PromoCodeValidatorTest` (межа дати `today == expiresOn`) | `valid = true` (останній день дії включно) | `valid = false` («Термін дії промокоду минув») | У `PromoCodeValidator.validate` умова `!today.isBefore(promo.expiresOn())` відхиляє рівні дати |
| **`BUG-013`** | BL-70, BL-80 | `PromoCodeValidatorTest` (`singleUse = true, used = true`) | `valid = false` / `IllegalArgumentException` у `apply` | `valid = true`, промокод застосовується повторно | У `PromoCodeValidator.validate` відсутня перевірка `promo.singleUse() && promo.used()` |
| **`BUG-014`** | BL-50 | `DiscountCalculatorTest#volumeDiscountPercent_respectsAllThresholds` (`5000.00`) | `10` (10% знижки від 5000.00 грн включно) | `5` (`expected: <10> but was: <5>`) | У `DiscountCalculator.volumeDiscountPercent` стоїть `compareTo(TIER_10) > 0` замість `>= 0` |
| **`BUG-015`** | BL-50 | `DiscountCalculatorTest#volumeDiscount_roundsHalfUp` (`2333.33`) | `116.67` (округлення `116.6665` за `HALF_UP`) | `116.66` (`expected: <116.67> but was: <116.66>`) | У `DiscountCalculator.volumeDiscount` використано `RoundingMode.DOWN` замість `HALF_UP` |

---

## 4. Результат прогону UI-тестів Selenium WebDriver (`@Tag("ui")`)

Команда запуску: `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui`

| Клас UI-тесту | Тест-кейс Спринту 3 | Сценарій | Статус |
| :--- | :--- | :--- | :---: |
| `ui.LoginTest` | `TC-004`, `TC-005` | Успішний вхід покупця та відмова при невірному паролі | `PASSED` (2/2) |
| `ui.SearchHammerTest` | `TC-010` | Пошук товару за словом «Hammer» та очищення пошуку | `PASSED` (1/1) |
| `ui.AddToCartTest` | `TC-012` | Додавання товару до кошика з кількістю 1 та перевірка лічильника | `PASSED` (1/1) |
| `ui.AddToCart99Test` | `TC-013` | Додавання товару з граничною кількістю 99 одиниць | `PASSED` (1/1) |
| `ui.ContactFormTest` | `TC-035` | Перевірка обов'язкових полів порожньої форми зворотного зв'язку | `PASSED` (1/1) |
| **Разом (UI WebDriver)** | **6 сценаріїв** | **Усі 6 UI-сценаріїв проходять стабільно** | **`Tests run: 6, Failures: 0, Errors: 0`** |
