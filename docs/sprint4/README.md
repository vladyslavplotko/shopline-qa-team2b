# Спринт 4 — Автоматизація тестування: JUnit 5 і Selenium WebDriver 4

**Продукт:** П1 «ШопЛайн» (інтернет-магазин інструментів і садового обладнання)
**Команда:** 2b (4 особи)
**Версія модуля бізнес-логіки:** `v1` (`code/modules/shopline/v1`)
**Демо-застосунок для UI-тестів:** `[https://practicesoftwaretesting.com](https://practicesoftwaretesting.com)`

---

## 1. Розподіл ролей та авторів тестів у Спринті 4

| Учасник | Роль у Спринті 4 | Написані тест-класи та артефакти | К-сть тест-методів |
| :--- | :--- | :--- | :--- |
| **Прохоренко М.П.** | Керівник тестування (КТ) | `DiscountCalculatorTest.java` (`BL-50`, `BL-60`), `BUG-014`, `BUG-015`, `BUG-017`, `surefire-summary.md`, `README.md`, перехресне рев'ю `PromoCodeValidatorTest` | 8 unit-методів |
| **Плотко В.** | Тест-аналітик (ТА) | `CartTest.java` (част. 1: `BL-10`, `BL-20`), `BUG-016`, `coverage-matrix.md`, `ui-scenarios.md` | 7 unit-методів (`CartTest.java`, част. 1: BL-10, BL-20) |
| **Касян С.** | Інженер з тестування (ІТ / SDET) | `CartTest.java` (част. 2: `BL-30`, `BL-40`), `BUG-010`, `BUG-011`, 6 класів WebDriver у пакеті `ui/` (6 кейсів С3), `selenium-ide/shopline.side` + експорт | 11 unit-методів + 6 UI-методів |
| **Пінчук А.П.** | Скрам-майстер / Рецензент (СМ) | `PromoCodeValidatorTest.java` (`BL-70`, `BL-80`), `BUG-012`, `BUG-013`, Code Review тест-класів за чек-листом 5.3, `task-board.md`, `retro.md` | 9 unit-методів |

---

## 2. Виконання нормативів команди (4 особи)

| Показник за методичкою (§6) | Норматив (4 особи) | Факт команди | Статус |
| :--- | :---: | :---: | :---: |
| Юніт-тестів JUnit 5 (кожен параметризований метод = 1) | ≥ 16 | **35** (без `ExampleTest`) | Виконано |
| Юніт-тестів на кожного члена команди (за комітами) | ≥ 3 | **від 7 до 11** на особу | Виконано |
| UI-сценаріїв Selenium WebDriver (`@Tag("ui")`) | ≥ 6 | **6** (6 різних тест-кейсів С3) | Виконано |
| Сценаріїв у Selenium IDE (`.side` + експорт у Java) | 1–2 | **2** | Виконано |
| Знайдених дефектів модуля v1 із 6 закладених | ≥ 4 | **6 закладених (`BUG-010`..`BUG-015`) + 2 розбіжності з SRS-1.1 (`BUG-016`, `BUG-017`)** | Виконано |

---

## 3. Інструкція зі збірки та запуску «з нуля»

### Вимоги до середовища
* **JDK 17** (перевірка: `java -version`)
* **Apache Maven 3.9+** (перевірка: `mvn -v`)
* **Google Chrome** (Selenium Manager автоматично завантажує сумісний `chromedriver`)

### Клонування та запуск
```bash
git clone https://github.com/vladyslavplotko/shopline-qa-team2b.git
cd shopline-qa-team2b

# 1. Запуск швидкого набору юніт-тестів модуля v1 (без UI):
mvn -f code/modules/shopline/v1/pom.xml clean test

# 2. Запуск одного тест-класу:
mvn -f code/modules/shopline/v1/pom.xml test -Dtest=PromoCodeValidatorTest

# 3. Запуск усіх тестів разом із WebDriver UI-тестами:
mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none

# 4. Запуск тільки WebDriver UI-тестів:
mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui
```

Звіти виконання Maven Surefire генеруються у директорії:
`code/modules/shopline/v1/target/surefire-reports/`

---

## 4. Знайдені дефекти модуля `shopline/v1` (GitHub Issues з міткою `module`)

| Bug ID | Issue | Вимога | Тест-методи, що фіксують дефект | Severity | Priority | Короткий опис дефекту |
| :---: | :---: | :---: | :--- | :---: | :---: | :--- |
| `BUG-010` | #19 | BL-20, BL-30 | `CartTest#sameSku_addsQuantityToExistingLine`, `sameSku_addsDifferentQuantities`, `sameSku_acceptsQuantityUpTo99`, `addItem_rejectsSameSkuQuantityAbove99` | Major | P2 | Повторне додавання товару з тим самим `sku` перезаписує кількість замість її збільшення |
| `BUG-011` | #20 | BL-10, BL-40 | `CartTest#changeQty_rejects0And100`, `addItem_rejectsQuantity0And100` | Major | P2 | Константа `MAX_QTY = 100` замість `99` дозволяє встановити недопустиму кількість `100` |
| `BUG-012` | #22 | BL-70 | `PromoCodeValidatorTest#validate_expirationDateBoundary` | Major | P2 | Умова `!today.isBefore(expiresOn)` помилково відхиляє промокод в останній день дії (`today == expiresOn`) |
| `BUG-013` | #23 | BL-70, BL-80 | `PromoCodeValidatorTest#validate_usedOneTimeCodeIsRejected`, `apply_usedOneTimeCodeIsRejected` | Critical | P1 | Поля `singleUse` та `used` ігноруються — вже використаний одноразовий промокод не відхиляється |
| `BUG-014` | #24 | BL-50 | `DiscountCalculatorTest#volumeDiscountPercent_respectsAllThresholds` | Major | P2 | Строге порівняння `>` замість `>=` на порозі `5000.00` грн повертає знижку 5% замість 10% |
| `BUG-015` | #25 | BL-50 | `DiscountCalculatorTest#volumeDiscount_roundsHalfUp` | Major | P2 | Сума знижки округлюється за `RoundingMode.DOWN` замість вимаганого `RoundingMode.HALF_UP` |
| `BUG-016` | #26 | BL-10 | `CartTest#addItem_rejectsBlankName` | Minor | P3 | Метод `Cart.addItem` не перевіряє порожню або `null` назву товару (`name`), хоча BL-10 SRS-1.1 вимагає непорожні `sku` і `name` |
| `BUG-017` | #27 | BL-60 | `DiscountCalculatorTest#shippingFee_respects2000Threshold`, `shippingFee_returns120BelowThreshold` | Major | P2 | Безкоштовна доставка у модулі v1 застосовується від порога `1500.00` грн замість `2000.00` грн за BL-60 SRS-1.1 |

---

## 5. Висновок про стан модуля `shopline/v1`

За результатами автоматизованого модульного тестування (35 командних тест-методів JUnit 5, що покривають усі правила `BL-10`–`BL-80` специфікації `SRS-1.1`), версія **v1.0.0** модуля бізнес-логіки «ШопЛайн» **не готова до випуску в експлуатацію** і потребує доопрацювання у версії `v2`:
1. Виявлено **1 Critical** дефект (`BUG-013`), який дозволяє необмежено застосовувати вже використані одноразові промокоди, що несе прямі фінансові збитки.
2. Виявлено **6 Major** дефектів граничної логіки та розрахунків: втрата попередньої кількості товару при повторному додаванні артикула (`BUG-010`), зміщена верхня межа кількості в кошику до 100 одиниць (`BUG-011`), передчасне відхилення промокоду в останній день дії (`BUG-012`), втрата рівня знижки 10% рівно на межі 5000.00 грн (`BUG-014`), заниження знижки через округлення `DOWN` замість `HALF_UP` (`BUG-015`) та невідповідність порога безкоштовної доставки між кодом модуля (1500.00 грн) і виправленою специфікацією `SRS-1.1` (2000.00 грн, `BUG-017`).
3. Виявлено **1 Minor** дефект валідації вхідних даних (`BUG-016` — відсутність перевірки порожньої назви товару `name`).

Усі тести, що фіксують зазначені дефекти, збережено у наборі для проведення автоматизованого регресійного тестування на версії модуля `v2` у Спринті 5.

---

## 6. Посилання на артефакти Спринту 4
* [Матриця покриття бізнес-логіки (`coverage-matrix.md`)](./coverage-matrix.md)
* [Специфікація UI-сценаріїв WebDriver (`ui-scenarios.md`)](./ui-scenarios.md)
* [Протокол запуску Surefire (`surefire-summary.md`)](./surefire-summary.md)
* [Таблиця розподілу задач (`task-board.md`)](./task-board.md)
* [Протокол ретроспективи (`retro.md`)](./retro.md)
* [Проєкт Selenium IDE та Java-експорт (`selenium-ide/`)](../../selenium-ide/)
