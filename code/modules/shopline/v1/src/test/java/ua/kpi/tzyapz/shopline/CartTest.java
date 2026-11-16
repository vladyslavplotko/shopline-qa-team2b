package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести для класу Cart (правила BL-10, BL-20, BL-30, BL-40).
 * Автори: Касян С. (ІТ), Плотко В. (ТА)
 * Рецензент: Пінчук А. (СМ)
 */
@Tag("unit")
@DisplayName("Cart — операції з кошиком покупця (BL-10, BL-20, BL-30, BL-40)")
class CartTest {

    // ==========================================
    // КАСЯН СОФІЯ — BL-30, BL-40
    // ==========================================

    // BUG-010 (Issue #19): падає на v1 (повторне додавання SKU перезаписує кількість замість збільшення)
    @Test
    @DisplayName("BL-30: повторне додавання того самого SKU збільшує кількість рядка (1 + 1 = 2)")
    void sameSku_addsQuantityToExistingLine() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 1);
        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 1);

        assertEquals(1, cart.items().size());
        assertEquals(2, cart.qtyOf("SKU-1"));
        assertEquals(2, cart.itemCount());
    }

    // BUG-010 (Issue #19): падає на v1 (повторне додавання SKU перезаписує кількість)
    @Test
    @DisplayName("BL-30: повторне додавання того самого SKU підсумовує різні кількості (2 + 3 = 5)")
    void sameSku_addsDifferentQuantities() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 2);
        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 3);

        assertEquals(1, cart.items().size());
        assertEquals(5, cart.qtyOf("SKU-1"));
        assertEquals(5, cart.itemCount());
    }

    // BUG-010 (Issue #19): падає на v1 (повторне додавання SKU перезаписує кількість)
    @Test
    @DisplayName("BL-20/BL-30: повторне додавання того самого SKU дозволяє досягти верхньої межі 99 (98 + 1)")
    void sameSku_acceptsQuantityUpTo99() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 98);
        cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 1);

        assertEquals(1, cart.items().size());
        assertEquals(99, cart.qtyOf("SKU-1"));
        assertEquals(99, cart.itemCount());
    }

    @Test
    @DisplayName("BL-40: removeItem видаляє вказаний рядок і оновлює subtotal та itemCount")
    void removeItem_removesRequestedLine() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 2);
        cart.addItem("SKU-2", "Drill", new BigDecimal("200.00"), 1);

        cart.removeItem("SKU-1");

        assertEquals(1, cart.items().size());
        assertEquals(1, cart.itemCount());
        assertEquals(new BigDecimal("200.00"), cart.subtotal());
    }

    @Test
    @DisplayName("BL-40: removeItem для невідомого SKU кидає NoSuchElementException")
    void removeItem_unknownSkuThrows() {
        Cart cart = new Cart();

        assertThrows(
                NoSuchElementException.class,
                () -> cart.removeItem("UNKNOWN")
        );
    }

    @Test
    @DisplayName("BL-40: changeQty приймає граничні значення кількості 1, 2, 98 та 99")
    void changeQty_accepts1And99() {
        Cart cart = new Cart();
        cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 5);

        cart.changeQty("SKU-1", 1);
        assertEquals(1, cart.qtyOf("SKU-1"));

        cart.changeQty("SKU-1", 2);
        assertEquals(2, cart.qtyOf("SKU-1"));

        cart.changeQty("SKU-1", 98);
        assertEquals(98, cart.qtyOf("SKU-1"));

        cart.changeQty("SKU-1", 99);
        assertEquals(99, cart.qtyOf("SKU-1"));
        assertEquals(99, cart.itemCount());
    }

    // BUG-011 (Issue #20): падає на v1 (MAX_QTY = 100 дозволяє встановити кількість 100)
    @Test
    @DisplayName("BL-40: changeQty відхиляє позамежні значення 0 та 100 із IllegalArgumentException")
    void changeQty_rejects0And100() {
        Cart cart = new Cart();
        cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 2);

        assertThrows(
                IllegalArgumentException.class,
                () -> cart.changeQty("SKU-1", 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> cart.changeQty("SKU-1", 100)
        );

        assertEquals(2, cart.qtyOf("SKU-1"));
    }

    @Test
    @DisplayName("BL-40: changeQty для невідомого SKU кидає NoSuchElementException")
    void changeQty_unknownSkuThrows() {
        Cart cart = new Cart();

        assertThrows(
                NoSuchElementException.class,
                () -> cart.changeQty("UNKNOWN", 2)
        );
    }

    @Test
    @DisplayName("BL-40: subtotal підсумовує рядки та округлює ціну за HALF_UP")
    void subtotal_sumsLinesAndRoundsHalfUp() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Product 1", new BigDecimal("10.105"), 2);
        cart.addItem("SKU-2", "Product 2", new BigDecimal("5.50"), 3);

        assertEquals(new BigDecimal("36.72"), cart.subtotal());
    }

    @Test
    @DisplayName("BL-40: порожній кошик повертає subtotal 0.00 та itemCount 0")
    void emptyCart_returnsZeroSubtotalAndItemCount() {
        Cart cart = new Cart();

        assertAll(
                () -> assertEquals(new BigDecimal("0.00"), cart.subtotal()),
                () -> assertEquals(0, cart.itemCount())
        );
    }

    @Test
    @DisplayName("BL-40: видалення останнього товару залишає кошик порожнім")
    void removingLastItem_leavesEmptyCart() {
        Cart cart = new Cart();
        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 1);

        cart.removeItem("SKU-1");

        assertAll(
                () -> assertEquals(0, cart.items().size()),
                () -> assertEquals(new BigDecimal("0.00"), cart.subtotal()),
                () -> assertEquals(0, cart.itemCount())
        );
    }

    // ==========================================
    // ВЛАДИСЛАВ ПЛОТКО — BL-10, BL-20
    // ==========================================

    @Test
    @DisplayName("BL-10: Accepts boundary quantities 1, 2, 98, 99 and minimum positive price 0.01")
    void addItem_acceptsBoundaryQuantities1And99() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("0.01"), 1);
        cart.addItem("SKU-2", "Nails", new BigDecimal("10.00"), 2);
        cart.addItem("SKU-3", "Saw", new BigDecimal("10.00"), 98);
        cart.addItem("SKU-4", "Drill", new BigDecimal("10.00"), 99);

        assertAll(
                () -> assertEquals(1, cart.qtyOf("SKU-1")),
                () -> assertEquals(2, cart.qtyOf("SKU-2")),
                () -> assertEquals(98, cart.qtyOf("SKU-3")),
                () -> assertEquals(99, cart.qtyOf("SKU-4")),
                () -> assertEquals(200, cart.itemCount())
        );
    }

    // BUG-011 (Issue #20): падає на v1 (MAX_QTY = 100 приймає кількість 100 без винятку)
    @Test
    @DisplayName("BL-10: Rejects quantities 0 and 100")
    void addItem_rejectsQuantity0And100() {
        Cart cart = new Cart();

        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 0)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem("SKU-2", "Drill", new BigDecimal("10.00"), 100)
                ),
                () -> assertEquals(0, cart.items().size())
        );
    }

    @Test
    @DisplayName("BL-10: Rejects zero (0.00) and negative (-0.01) prices")
    void addItem_rejectsInvalidPrices() {
        Cart cart = new Cart();

        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem("SKU-1", "Hammer", new BigDecimal("0.00"), 1)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem("SKU-2", "Drill", new BigDecimal("-0.01"), 1)
                ),
                () -> assertEquals(0, cart.items().size())
        );
    }

    @Test
    @DisplayName("BL-10: Rejects empty, blank and null SKU")
    void addItem_rejectsBlankSku() {
        Cart cart = new Cart();

        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem("", "Hammer", new BigDecimal("10.00"), 1)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem("   ", "Hammer", new BigDecimal("10.00"), 1)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem(null, "Hammer", new BigDecimal("10.00"), 1)
                ),
                () -> assertEquals(0, cart.items().size())
        );
    }

    // BUG-016: падає на v1, бо Cart.addItem не перевіряє порожній або null рядок name за BL-10 SRS-1.1
    @Test
    @DisplayName("BL-10: Rejects empty, blank and null product name")
    void addItem_rejectsBlankName() {
        Cart cart = new Cart();

        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem("SKU-1", "", new BigDecimal("10.00"), 1)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem("SKU-2", "   ", new BigDecimal("10.00"), 1)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> cart.addItem("SKU-3", null, new BigDecimal("10.00"), 1)
                ),
                () -> assertEquals(0, cart.items().size())
        );
    }

    @Test
    @DisplayName("BL-10: Rounds unitPrice 1.005 -> 1.01 using HALF_UP")
    void addItem_roundsUnitPriceHalfUp() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("1.005"), 1);

        assertAll(
                () -> assertEquals(new BigDecimal("1.01"), cart.subtotal()),
                () -> assertEquals(1, cart.itemCount())
        );
    }

    // BUG-010 (Issue #19): падає на v1 (повторне додавання SKU перезаписує кількість замість кидання винятку при сумі > 99)
    @Test
    @DisplayName("BL-20: Rejects repeated SKU quantity above 99 (40 + 60 = 100)")
    void addItem_rejectsSameSkuQuantityAbove99() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 40);

        assertThrows(
                IllegalArgumentException.class,
                () -> cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 60)
        );

        assertEquals(1, cart.items().size());
        assertEquals(40, cart.qtyOf("SKU-1"));
        assertEquals(40, cart.itemCount());
    }
}
