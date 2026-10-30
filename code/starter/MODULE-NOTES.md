# ShopLine v1 — навчальна симуляція з 6 дефектами

Створено на основі SRS-1.1, BL-10..BL-80. Це НЕ оригінальний модуль викладача. Містить рівно шість навмисних поведінкових відхилень: два у Cart, три у DiscountCalculator, одне у PromoCodeValidator. Не виправляйте їх до написання тестів. Для рев’ю дефекти позначені коментарями `seeded defect` у коді; перед сліпим тестуванням їх можна прибрати з коментарів (не змінюючи логіку).

SRS BL-80 вимагає глобальної перевірки одноразового промокоду між усіма інтернет-магазинами, що неможливо гарантувати автономною Java-бібліотекою без зовнішнього реєстру. Локальна множина used — лише навчальна модель. Формат коду [A-Za-z0-9]+ — припущення, оскільки BL-70 прямо визначає тільки довжину 6..10.

API: Cart.addItem(sku,name,unitPrice,qty), removeItem(sku), changeQty(sku,qty), subtotal(), itemCount(); DiscountCalculator.volumeDiscountPercent(subtotal), volumeDiscount(subtotal), shippingFee(subtotalAfterDiscount); PromoCodeValidator.validate(PromoCode,today,subtotal), apply(PromoCode,today,subtotal). Додатковий запис PromoCode потрібний за розділом 5 SRS.

Запуск: mvn clean test (JDK 17). SmokeTest перевіряє лише JUnit, не поведінкові дефекти.
