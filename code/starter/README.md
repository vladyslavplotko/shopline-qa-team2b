# qa-shopline — Спринт 4

Автотести для модуля «ШопЛайн» (JUnit 5) і UI-сценаріїв (Selenium WebDriver).

## Вимоги
JDK 17, Maven 3.9, Git, Chrome.

## Запуск
- Юніт-тести: `mvn clean test`
- Усі тести, включно з UI: `mvn test -DexcludedGroups=none`
- Один клас: `mvn test -Dtest=CartTest`

Звіти: `target/surefire-reports/`

## Структура
- `src/main/java/ua/kpi/tzyapz/shopline/` — модуль v1 (Cart, DiscountCalculator, PromoCodeValidator)
- `src/test/java/ua/kpi/tzyapz/shopline/` — юніт-тести
- `src/test/java/ua/kpi/tzyapz/shopline/ui/` — WebDriver-тести
- `selenium-ide/` — проєкт Selenium IDE та експорт
- `docs/` — матриця покриття, UI-сценарії, протокол запуску

## Автори тестів
(заповнити)
