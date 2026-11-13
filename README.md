# ТЗЯПЗ — Забезпечення якості продукту П1 «ШопЛайн» (Команда 2b)

Репозиторій команди **2b** з дисципліни «Технології забезпечення якості програмних засобів».  
**Продукт:** П1 «ШопЛайн» — інтернет-магазин інструментів і садового обладнання (`https://practicesoftwaretesting.com`).

---

## Склад команди та ротація ролей

| Учасник | Спринт 1 (Процеси) | Спринт 2 (Вимоги) | Спринт 3 (Тест-кейси і баги) | Спринт 4 (Автоматизація) |
| :--- | :---: | :---: | :---: | :---: |
| **Прохоренко Матвій Павлович** | ТА | ІТ | СМ | **КТ** |
| **Плотко Владислав** | ІТ | СМ | КТ | **ТА** |
| **Касян Софія** | СМ | КТ | ТА | **ІТ** |
| **Пінчук Андрій Павлович** | КТ | ТА | ІТ | **СМ** |

---

## Швидкий старт: запуск автотестів (Спринт 4)

Робочий Maven-проєкт модуля бізнес-логіки `v1` та автотестів JUnit 5 / Selenium WebDriver 4 розташований у директорії [`code/modules/shopline/v1/`](./code/modules/shopline/v1/).

### Вимоги до середовища
* **JDK 17** (Eclipse Temurin) — перевірка: `java -version`
* **Apache Maven 3.9+** — перевірка: `mvn -v`
* **Google Chrome** (актуальна версія; драйвер завантажує Selenium Manager автоматично)

### Команди запуску з кореня репозиторію

```bash
# 1. Чистий запуск усіх юніт-тестів JUnit 5 (UI-тести виключені за замовчуванням)
mvn -f code/modules/shopline/v1/pom.xml clean test

# 2. Запуск усіх тестів (юніт-тести + UI-тести Selenium WebDriver у headless-режимі)
mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none

# 3. Запуск виключно UI-тестів Selenium WebDriver (@Tag("ui"))
mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui

# 4. Запуск окремого тест-класу
mvn -f code/modules/shopline/v1/pom.xml test -Dtest=DiscountCalculatorTest
```

---

## Структура репозиторію за спринтами

* **[`docs/sprint1/`](./docs/sprint1/)** — Спринт 1: декомпозиція продукту, BPMN-діаграми процесів, беклог.
* **[`docs/sprint2/`](./docs/sprint2/)** — Спринт 2: аудит вимог, реєстр дефектів вимог, виправлена специфікація [`SRS-1.1.md`](./docs/sprint2/SRS-1.1.md), тест-план.
* **[`docs/sprint3/`](./docs/sprint3/)** — Спринт 3: набір тест-кейсів (`test-cases-sprint3.xlsx`), журнал виконання (`execution-log.xlsx`), підсумковий звіт [`README.md`](./docs/sprint3/README.md).
* **[`docs/sprint4/`](./docs/sprint4/)** — Спринт 4 (Автоматизація):
  * [`docs/sprint4/README.md`](./docs/sprint4/README.md) — документація автотестів та розподіл авторів;
  * [`docs/sprint4/coverage-matrix.md`](./docs/sprint4/coverage-matrix.md) — матриця покриття вимог `BL-10..BL-80` юніт-тестами;
  * [`docs/sprint4/ui-scenarios.md`](./docs/sprint4/ui-scenarios.md) — специфікація 6 UI-сценаріїв WebDriver;
  * [`docs/sprint4/surefire-summary.md`](./docs/sprint4/surefire-summary.md) — протокол запуску Surefire та трасування до дефектів `BUG-010..BUG-015`;
  * [`docs/sprint4/task-board.md`](./docs/sprint4/task-board.md) та [`docs/sprint4/retro.md`](./docs/sprint4/retro.md).
* **[`code/modules/shopline/v1/`](./code/modules/shopline/v1/)** — вихідний код модуля бізнес-логіки `v1` (`Cart`, `DiscountCalculator`, `PromoCodeValidator`), юніт-тести JUnit 5 та UI-тести Selenium WebDriver (`ui/`).
* **[`selenium-ide/`](./selenium-ide/)** — проєкт Selenium IDE (`shopline.side`) та експортовані чернетки (`export/`).
* **[GitHub Issues](https://github.com/vladyslavplotko/shopline-qa-team2b/issues)** — звіти про дефекти demo-застосунку (Спринт 3) та модуля бізнес-логіки `v1` (Спринт 4, мітка `module`).
