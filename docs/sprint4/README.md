# Спринт 4 — Автоматизація тестування: JUnit 5 і Selenium WebDriver 4

**Продукт:** П1 «ШопЛайн» (інтернет-магазин інструментів і садового обладнання)
**Команда:** 2b (4 особи)
**Версія модуля бізнес-логіки:** `v1` (`code/modules/shopline/v1`)
**Демо-застосунок для UI-тестів:** `https://practicesoftwaretesting.com`

---

## 1. Розподіл ролей та авторів тестів у Спринті 4

| Учасник | Роль у Спринті 4 | Написані тест-класи та артефакти | К-сть тест-методів |
| :--- | :--- | :--- | :--- |
| **Прохоренко М.П.** | Керівник тестування (КТ) | `DiscountCalculatorTest.java` (`BL-50`, `BL-60`), `BUG-014`, `BUG-015`, `surefire-summary.md`, `README.md`, перехресне рев'ю `PromoCodeValidatorTest` | 8 unit-методів |
| **Плотко В.** | Тест-аналітик (ТА) | `CartTest.java` (част. 1: `BL-10`, `BL-20`), `BUG-010`, `BUG-011`, `coverage-matrix.md`, `ui-scenarios.md` | 6 unit-методів (`CartTest.java`, част. 1: BL-10, BL-20) |
| **Касян С.** | Інженер з тестування (ІТ / SDET) | `CartTest.java` (част. 2: `BL-30`, `BL-40`), 5 класів WebDriver у пакеті `ui/` (6 сценаріїв), `selenium-ide/shopline.side` + експорт у Java | 11 unit-методів + 6 UI-методів (`CartTest.java`, част. 2: BL-30, BL-40) |
| **Пінчук А.П.** | Скрам-майстер / Рецензент (СМ) | `PromoCodeValidatorTest.java` (`BL-70`, `BL-80`), `BUG-012`, `BUG-013`, Code Review тест-класів за чек-листом 5.3, `task-board.md`, `retro.md` | 9 unit-методів |

---

## 2. Виконання нормативів команди (4 особи)

| Показник за методичкою (розділ 6) | Норматив (4 особи) | Факт команди | Статус |
| :--- | :---: | :---: | :---: |
| Юніт-тестів JUnit 5 (кожен параметризований метод = 1) | ≥ 16 | **34** (без `ExampleTest`) | Виконано |
| Юніт-тестів на кожного члена команди (за комітами) | ≥ 3 | **від 6 до 11** на особу | Виконано |
| UI-сценаріїв Selenium WebDriver (`@Tag("ui")`) | ≥ 6 | **6** | Виконано |
| Сценаріїв у Selenium IDE (`.side` + експорт у Java) | 1–2 | **2** | Виконано |
| Знайдених дефектів модуля v1 із 6 закладених | ≥ 5 | **6 із 6 (`BUG-010`..`BUG-015`)** | Виконано |

---

## 3. Інструкція зі збірки та запуску «з нуля»

### Вимоги до середовища
* **JDK 17** (перевірка: `java -version`)
* **Apache Maven 3.9+** (перевірка: `mvn -v`)
* **Google Chrome** (Selenium Manager автоматично завантажує сумісний `chromedriver`)

### Клонування та запуск
```bash
git clone [https://github.com/vladyslavplotko/shopline-qa-team2b.git](https://github.com/vladyslavplotko/shopline-qa-team2b.git)
cd shopline-qa-team2b

# 1. Запуск швидкого набору юніт-тестів модуля v1 (без UI):
mvn -f code/modules/shopline/v1/pom.xml clean test

# 2. Запуск одного тест-класу:
mvn -f code/modules/shopline/v1/pom.xml test -Dtest=PromoCodeValidatorTest

# 3. Запуск усіх тестів разом із WebDriver UI-тестами:
mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none

# 4. Запуск тільки WebDriver UI-тестів:
mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui
