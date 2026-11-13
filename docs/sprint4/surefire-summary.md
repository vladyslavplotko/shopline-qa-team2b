# Протокол запуску автотестів (Maven Surefire) — Спринт 4

**Продукт:** «ШопЛайн» (`shopline-module` v1.0.0)
**Дата виконання:** 13.11.2026
**Відповідальний:** Прохоренко М.П. (Керівник тестування / КТ)
**Середовище:** Windows 11, JDK 17 (Eclipse Temurin), Apache Maven 3.9, JUnit Jupiter 5.10.2, Selenium WebDriver 4.21.0, Google Chrome 140.0 (headless `1280×900`).

---

## 1. Команди запуску

Робочий Maven-проєкт модуля розташований у директорії `code/modules/shopline/v1/`. Команди для запуску з кореня репозиторію:

* Чистий запуск юніт-тестів бізнес-логіки (група `ui` виключена за замовчуванням):
  `mvn -f code/modules/shopline/v1/pom.xml clean test`
* Повний запуск (юніт-тести + UI-тести Selenium WebDriver):
  `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none`
* Запуск виключно UI-тестів:
  `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui`

---

## 2. Зведений результат прогону юніт-тестів (`mvn clean test`)

На версії модуля `v1` юніт-тести JUnit 5 виявили **всі 6 із 6 закладених дефектів бізнес-логіки** (`BUG-010` – `BUG-015`). Відповідні тести залишено у наборі «червоними» згідно з розділом 4.9 методички для верифікації виправлень у версії `v2` (Спринт 5).

| Тест-клас | Вимоги SRS-1.1 | Тест-методів | Автори | Пов'язані баг-репорти |
| :--- | :--- | :---: | :--- | :--- |
| `ExampleTest` | BL-40, BL-50, BL-60 | 2 | Стартовий модуль | — |
| `CartTest` | BL-10, BL-20, BL-30, BL-40 | 17 | Плотко В. (6), Касян С. (11) | `BUG-010`, `BUG-011` |
| `DiscountCalculatorTest` | BL-50, BL-60 | 8 | Прохоренко М. (8) | `BUG-014`, `BUG-015` |
| `PromoCodeValidatorTest` | BL-70, BL-80 | 9 | Пінчук А. (9) | `BUG-012`, `BUG-013` |
| **Разом (Unit)** | **BL-10 .. BL-80** | **36 (34 командні)** | **Уся команда** | **6 дефектів (`BUG-010`..`BUG-015`)** |

---

## 3. Перелік «червоних» юніт-тестів (Failures) та прив'язка до дефектів модуля v1

| № | Тест-клас і метод | Вимога | Причина падіння (Expected vs Actual) | Звіт про дефект |
| :-: | :--- | :---: | :--- | :---: |
| 1 | `CartTest#sameSku_addsQuantityToExistingLine` | BL-30 | `expected: <2> but was: <1>` — повторний `addItem` перезаписує рядок замість додавання кількості | `BUG-010` (#19) |
| 2 | `CartTest#sameSku_addsDifferentQuantities` | BL-30 | `expected: <5> but was: <3>` — перезапис кількості при повторному `addItem` | `BUG-010` (#19) |
| 3 | `CartTest#sameSku_acceptsQuantityUpTo99` | BL-20, BL-30 | `expected: <99> but was: <1>` — перезапис кількості при повторному `addItem` | `BUG-010` (#19) |
| 4 | `CartTest#addItem_rejectsSameSkuQuantityAbove99` | BL-20 | Очікувався `IllegalArgumentException` при сумі `99 + 1 = 100`, але метод перезаписав кількість на `1` | `BUG-010` (#19) |
| 5 | `CartTest#changeQty_rejects0And100` | BL-40 | Очікувався `IllegalArgumentException` для `qty = 100`, але у `Cart` встановлено `MAX_QTY = 100` замість `99` | `BUG-011` (#20) |
| 6 | `CartTest#addItem_rejectsQuantity0And100` | BL-10 | Очікувався `IllegalArgumentException` для `qty = 100` через `MAX_QTY = 100` замість `99` | `BUG-011` (#20) |
| 7 | `PromoCodeValidatorTest#validate_expirationDateBoundary` | BL-70 | `expected: <true> but was: <false>` — умова `!today.isBefore(expiresOn)` відхиляє код в останній день дії (`today == expiresOn`) | `BUG-012` (#22) |
| 8 | `PromoCodeValidatorTest#validate_usedOneTimeCodeIsRejected` | BL-70 | `expected: <false> but was: <true>` — поля `singleUse` та `used` ігноруються у `validate()` | `BUG-013` (#23) |
| 9 | `PromoCodeValidatorTest#apply_usedOneTimeCodeIsRejected` | BL-80 | Очікувався `IllegalArgumentException` для використаного одноразового коду, але знижка нараховується | `BUG-013` (#23) |
| 10 | `DiscountCalculatorTest#volumeDiscountPercent_respectsAllThresholds` | BL-50 | На межі `5000.00` грн: `expected: <10> but was: <5>` через строге `>` замість `>=` для `TIER_10` | `BUG-014` (#24) |
| 11 | `DiscountCalculatorTest#volumeDiscount_roundsHalfUp` | BL-50 | Для `2333.33` грн (5%): `expected: <116.67> but was: <116.66>` через `RoundingMode.DOWN` замість `HALF_UP` | `BUG-015` (#25) |

Усі інші юніт-тести (включно з `CartTest#addItem_roundsUnitPriceHalfUp`, `addItem_rejectsBlankSku`, `addItem_rejectsInvalidPrices`, `addItem_acceptsBoundaryQuantities1And99` тощо) проходять успішно (`PASSED`).

---

## 4. Результат прогону UI-тестів Selenium WebDriver (`@Tag("ui")`)

Команда: `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui`

| Клас | Тест-метод | Кейс Спринту 3 / Вимога | Статус |
| :--- | :--- | :--- | :---: |
| `LoginTest` | `successfulCustomerLogin` | `TC-004` / `F-20` | PASSED |
| `LoginTest` | `loginWithWrongPasswordShowsError` | `TC-005` / `F-20` | PASSED |
| `SearchHammerTest` | `searchHammerAndClearSearch` | `TC-010` / `F-60` | PASSED |
| `AddToCartTest` | `addProductToCart` | `TC-012 (qty=1)` / `F-90` | PASSED |
| `AddToCart99Test` | `add99ProductsToCart` | `TC-012 (qty=99)` / `F-90` | PASSED |
| `ContactFormTest` | `emptyContactFormValidation` | `TC-018` / `F-140` | PASSED |

**Підсумок UI-набору:** `Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS`.
