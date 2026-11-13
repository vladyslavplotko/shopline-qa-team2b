# Спринт 4 — Автоматизація тестування: JUnit 5 і Selenium WebDriver 4

**Продукт:** П1 «ШопЛайн» (інтернет-магазин інструментів і садового обладнання)  
**Команда:** 2b (4 особи)  
**Версія модуля бізнес-логіки:** `v1` (`code/modules/shopline/v1`)  
**Демо-застосунок для UI-тестів:** `https://practicesoftwaretesting.com`

---

## 1. Розподіл ролей та авторів тестів у Спринті 4

| Учасник | Роль у Спринті 4 | Написані тест-класи та артефакти | К-сть тест-методів |
| :--- | :--- | :--- | :---: |
| **Прохоренко М.П.** | Керівник тестування (КТ) | `DiscountCalculatorTest.java` (`BL-50`, `BL-60`), `BUG-014`, `BUG-015`, `surefire-summary.md`, `README.md`, перехресне рев'ю `PromoCodeValidatorTest` | 8 unit-методів |
| **Плотко В.** | Тест-аналітик (ТА) | `CartTest.java` (част. 1: `BL-10`, `BL-20`), `BUG-010`, `BUG-011`, `coverage-matrix.md`, `ui-scenarios.md` | 6 unit-методів (CartTest.java, част. 1: BL-10, BL-20) |
| **Касян С.** | Інженер з тестування (ІТ / SDET) | `CartTest.java` (част. 2: `BL-30`, `BL-40`), 5 класів WebDriver у пакеті `ui/` (6 сценаріїв), `selenium-ide/shopline.side` + експорт у Java | 11 unit-методів + 6 UI-методів (CartTest.java, част. 2: BL-30, BL-40) |
| **Пінчук А.П.** | Скрам-майстер / Рецензент (СМ) | `PromoCodeValidatorTest.java` (`BL-70`, `BL-80`), `BUG-012`, `BUG-013`, Code Review тест-класів за чек-листом 5.3, `task-board.md`, `retro.md` | 9 unit-методів |

---

## 2. Виконання нормативів команди (для 4 осіб)

| Показник | Норматив (4 особи) | Фактично виконано | Статус |
| :--- | :---: | :---: | :---: |
| Юніт-тестів JUnit 5 (методів `@Test` / `@ParameterizedTest`) | $\ge 16$ | **36 методів** (34 авторські методи команди + 2 методи стартового ExampleTest) | Виконано |
| Юніт-тестів на кожного учасника (за комітами) | $\ge 3$ | **від 6 до 9 методів** на кожного | Виконано |
| UI-сценаріїв Selenium WebDriver (`@Tag("ui")`) | $\ge 6$ | **6 тест-методів** (у 5 класах пакета `ui`) | Виконано |
| Сценаріїв у Selenium IDE (`*.side` + експорт) | $1–2$ | **2 сценарії** (`shopline.side` + 2 експорти в `selenium-ide/export/`) | Виконано |
| Знайдених закладених дефектів модуля `v1` | $\ge 4$ із 6 | **6 із 6** закладених (100%) + 1 додатковий за SRS-1.1 (BUG-016) | Виконано |

---

## 3. Інструкція зі збирання та запуску тестів «з нуля»

### Передумови
- **JDK 17** (`java -version` показує `17.x`)
- **Apache Maven 3.9+** (`mvn -v`)
- **Google Chrome** (актуальна версія; драйвер завантажується автоматично через Selenium Manager)

### Команди запуску з кореня репозиторію
- Клонування репозиторію:
  `git clone https://github.com/vladyslavplotko/shopline-qa-team2b.git`
  `cd shopline-qa-team2b`
- Запуск усіх юніт-тестів JUnit 5 (UI-тести виключені за замовчуванням):
  `mvn -f code/modules/shopline/v1/pom.xml clean test`
- Запуск окремого тест-класу модуля:
  `mvn -f code/modules/shopline/v1/pom.xml test -Dtest=DiscountCalculatorTest`
- Запуск усіх тестів включно з UI-тестами Selenium WebDriver:
  `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none`
- Запуск лише UI-тестів Selenium WebDriver:
  `mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui`

---

## 4. Навігація по артефактах Спринту 4

- **Матриця покриття BL:** [`docs/sprint4/coverage-matrix.md`](./coverage-matrix.md)
- **Специфікація UI-сценаріїв:** [`docs/sprint4/ui-scenarios.md`](./ui-scenarios.md)
- **Протокол запуску Surefire:** [`docs/sprint4/surefire-summary.md`](./surefire-summary.md)
- **Дошка задач:** [`docs/sprint4/task-board.md`](./task-board.md)
- **Протокол ретроспективи:** [`docs/sprint4/retro.md`](./retro.md)
- **Проєкт Selenium IDE та експорт:** [`selenium-ide/`](../../selenium-ide/)
- **Вихідний код модуля та автотестів:** [`code/modules/shopline/v1/`](../../code/modules/shopline/v1/)
- **Звіти про дефекти модуля (`BUG-010`..`BUG-015`):** [GitHub Issues (мітка `module`)](https://github.com/vladyslavplotko/shopline-qa-team2b/issues)
