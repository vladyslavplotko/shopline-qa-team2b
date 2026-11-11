package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void sameSku_addsQuantityToExistingLine() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 1);
        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 1);

        assertEquals(1, cart.items().size());
        assertEquals(2, cart.qtyOf("SKU-1"));
        assertEquals(2, cart.itemCount());
    }

    @Test
    void sameSku_addsDifferentQuantities() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 2);
        cart.addItem("SKU-1", "Hammer", new BigDecimal("100.00"), 3);

        assertEquals(1, cart.items().size());
        assertEquals(5, cart.qtyOf("SKU-1"));
        assertEquals(5, cart.itemCount());
    }

    @Test
    void sameSku_acceptsQuantityUpTo99() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 98);
        cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 1);

        assertEquals(1, cart.items().size());
        assertEquals(99, cart.qtyOf("SKU-1"));
        assertEquals(99, cart.itemCount());
    }

    @Test
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
    void removeItem_unknownSkuThrows() {
        Cart cart = new Cart();

        assertThrows(
                NoSuchElementException.class,
                () -> cart.removeItem("UNKNOWN")
        );
    }

    @Test
    void changeQty_accepts1And99() {
        Cart cart = new Cart();
        cart.addItem("SKU-1", "Hammer", new BigDecimal("10.00"), 2);

        cart.changeQty("SKU-1", 1);
        assertEquals(1, cart.qtyOf("SKU-1"));

        cart.changeQty("SKU-1", 99);
        assertEquals(99, cart.qtyOf("SKU-1"));
        assertEquals(99, cart.itemCount());
    }

    @Test
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
    void changeQty_unknownSkuThrows() {
        Cart cart = new Cart();

        assertThrows(
                NoSuchElementException.class,
                () -> cart.changeQty("UNKNOWN", 2)
        );
    }

    @Test
    void subtotal_sumsLinesAndRoundsHalfUp() {
        Cart cart = new Cart();

        cart.addItem("SKU-1", "Product 1", new BigDecimal("10.105"), 2);
        cart.addItem("SKU-2", "Product 2", new BigDecimal("5.50"), 3);

        assertEquals(new BigDecimal("36.72"), cart.subtotal());
    }

    @Test
    void emptyCart_returnsZeroSubtotalAndItemCount() {
        Cart cart = new Cart();

        assertAll(
                () -> assertEquals(new BigDecimal("0.00"), cart.subtotal()),
                () -> assertEquals(0, cart.itemCount())
        );
    }

    @Test
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
}