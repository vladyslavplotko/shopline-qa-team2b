# Протокол запуску автотестів (Maven Surefire) — Спринт 4

**Продукт:** «ШопЛайн» (`shopline-module` v1.0.0)  
**Дата виконання:** 13.11.2026  
**Відповідальний:** Прохоренко М.П. (Керівник тестування / КТ)  
**Середовище:** Windows 11, JDK 17 (Eclipse Temurin), Apache Maven 3.9, JUnit Jupiter 5.10.2, Selenium WebDriver 4.21.0, Google Chrome 140.0 (headless `1280×900`).

---

## 1. Команди запуску

Оскільки робочий Maven-проєкт модуля розташований у директорії `code/modules/shopline/v1/`, запуск виконується з кореня репозиторію з прапорцем `-f` (або після переходу `cd code/modules/shopline/v1`):

```bash
# 1. Чистий запуск юніт-тестів бізнес-логіки (група "ui" виключена за замовчуванням)
mvn -f code/modules/shopline/v1/pom.xml clean test

# 2. Повний запуск (юніт-тести + UI-тести Selenium WebDriver)
mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none

# 3. Запуск виключно UI-тестів
mvn -f code/modules/shopline/v1/pom.xml test -DexcludedGroups=none -Dgroups=ui
