# Протокол запуску автотестів (Maven Surefire) — Спринт 4

**Продукт:** «ШопЛайн» (`shopline-module` v1.0.0)  
**Дата виконання:** 16.11.2026  
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

Підсумковий вивід Maven Surefire для юніт-тестів модуля `v1` (з урахуванням усіх наборів даних `@ParameterizedTest`):

```text
[INFO] Results:
[ERROR] Failures: 14
[INFO]
[ERROR] Tests run: 78, Failures: 14, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

На версії модуля `v1` юніт-тести JUnit 5 виявили **8 дефектів реалізації відносно вимог SRS-1.1** (`BUG-010` – `BUG-017`). Усі тести, що фіксують дефекти, залишено у наборі «червоними» згідно з розділом 4.9 методички для верифікації виправлень у версії `v2` (Спринт 5).

| Тест-клас | Вимоги SRS-1.1 | Тест-методів | Запусків (`Tests run`) | Passed | Failures | Errors | Skipped | Пов'язані баг-репорти |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| `ExampleTest` | BL-40, BL-50, BL-60 | 2 | 2 | 2 | 0 | 0 | 0 | — |
| `CartTest` | BL-10, BL-20, BL-30, BL-40 | 18 | 33 | 26 | 7 | 0 | 0 | `BUG-010`, `BUG-011`, `BUG-016` |
| `DiscountCalculatorTest` | BL-50, BL-60 | 8 | 21 | 17 | 4 | 0 | 0 | `BUG-014`, `BUG-015`, `BUG-017` |
| `PromoCodeValidatorTest` | BL-70, BL-80 | 9 | 22 | 19 | 3 | 0 | 0 | `BUG-012`, `BUG-013` |
| **Разом (Unit)** | **BL-10 .. BL-80** | **37 (35 авторських + 2 базові)** | **78** | **64** | **14 (8 багів)** | **0** | **0** | **`BUG-010` .. `BUG-017`** |

---

## 3. Перелік дефектів модуля `v1`, зафіксованих тестами зі статусом `FAILURE`

| ID дефекту | Severity / Priority | Вимога | Тест-клас і метод | Очікуваний результат (за SRS-1.1) | Фактичний результат (модуль v1) | Причина в коді `v1` |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- |
| **`BUG-010`** | Major / P2 | BL-20, BL-30 | `CartTest#addItem_sameSkuIncrementsExistingRow`, `sameSku_addsQuantityToExistingLine`, `sameSku_addsDifferentQuantities`, `addItem_sameSkuExceeding99ThrowsAndKeepsCartUnchanged` | Кількість наявного рядка збільшується на `qty`; при сумі > 99 кидається `IllegalArgumentException` | Новий виклик перезаписує стару кількість | У `Cart.addItem` викликається `items.put(...)` без додавання `existing.qty() + qty` |
| **`BUG-011`** | Major / P2 | BL-10, BL-40 | `CartTest#addItem_rejectsQuantityOutsideRange(100)`, `changeQty_rejects0And100` | `IllegalArgumentException` для кількості `100` (максимум 99 од.) | Позиція додається / оновлюється з `qty = 100` | У `Cart.java` константа `MAX_QTY = 100` замість `99` |
| **`BUG-012`** | Major / P2 | BL-70 | `PromoCodeValidatorTest#validate_expirationDateBoundary` | `valid = true` (останній день дії `today == expiresOn` включно) | `valid = false` («Термін дії промокоду минув») | У `PromoCodeValidator.validate` умова `!today.isBefore(promo.expiresOn())` відхиляє рівні дати |
| **`BUG-013`** | Critical / P1 | BL-70, BL-80 | `PromoCodeValidatorTest#validate_usedOneTimeCodeIsRejected`, `apply_usedOneTimeCodeIsRejected` | `valid = false` у `validate()` та `IllegalArgumentException` у `apply()` | `valid = true`, використаний одноразовий код застосовується повторно | У `PromoCodeValidator.validate` відсутня перевірка `promo.singleUse() && promo.used()` |
| **`BUG-014`** | Major / P2 | BL-50 | `DiscountCalculatorTest#volumeDiscountPercent_respectsAllThresholds` (`5000.00`) | `10` (10% знижки від 5000.00 грн включно) | `5` (`expected: <10> but was: <5>`) | У `DiscountCalculator.volumeDiscountPercent` стоїть `compareTo(TIER_10) > 0` замість `>= 0` |
| **`BUG-015`** | Major / P2 | BL-50 | `DiscountCalculatorTest#volumeDiscount_roundsHalfUp` (`2333.33`) | `116.67` (округлення `116.6665` за `HALF_UP`) | `116.66` (`expected: <116.67> but was: <116.66>`) | У `DiscountCalculator.volumeDiscount` використано `RoundingMode.DOWN` замість `HALF_UP` |
| **`BUG-016`** | Major / P2 | BL-10 | `CartTest#addItem_rejectsBlankName` | `IllegalArgumentException` при порожньому `name` (`null`, `""`, `"   "`) | Позиція з порожнім `name` додається до кошика | У `Cart.addItem` відсутня валідація параметра `name` |
| **`BUG-017`** | Major / P2 | BL-60 | `DiscountCalculatorTest#shippingFee_respects2000Threshold` (`1999.99`), `shippingFee_returns120BelowThreshold` | `120.00` грн для сум нижче порогу `2000.00` грн за SRS-1.1 | `0.00` грн (`expected: <120.00> but was: <0.00>`) | У `DiscountCalculator` константа `FREE_SHIPPING_FROM = 1500` замість `2000` за SRS-1.1 |

---

## 4. Результат прогону UI-тестів Selenium WebDriver (`@Tag("ui")`)

Команда запуску: `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui`

```text
[INFO] Results:
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

| Клас UI-тесту | Тест-кейс Спринту 3 | Вимоги | Сценарій | Рецензент (Code Review) | Статус |
| :--- | :--- | :--- | :--- | :--- | :---: |
| `ui.LoginTest` | `TC-004`, `TC-005` | `F-20` | Успішний вхід покупця (`UI-01`) та відмова при невірному паролі з перевіркою тексту помилки (`UI-02`) | Пінчук А. — reviewed | `PASSED` (2/2) |
| `ui.SearchHammerTest` | `TC-010` | `F-60` | Пошук за запитом `hammer`, перевірка результатів та очищення кнопкою скидання (`UI-03`) | Пінчук А. — reviewed | `PASSED` (1/1) |
| `ui.AddToCartTest` | `TC-012` | `F-90, GUI-30, GUI-70` | Додавання одного товару до кошика та перевірка рядка в кошику (`UI-04`) | Пінчук А. — reviewed | `PASSED` (1/1) |
| `ui.AddToCart99Test` | `TC-014` / `TC-012 (qty=99)` | `F-90, GUI-30` | Встановлення граничної кількості 99 одиниць товару та перевірка рядка в кошику (`UI-05`) | Пінчук А. — reviewed | `PASSED` (1/1) |
| `ui.ContactFormTest` | `TC-018` | `F-140, GUI-50, GUI-90` | Перевірка повідомлень валідації обов'язкових полів порожньої контактної форми (`UI-06`) | Пінчук А. — reviewed | `PASSED` (1/1) |
