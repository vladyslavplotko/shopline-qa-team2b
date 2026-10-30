package ua.kpi.tzyapz.shopline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Cart {
    public record Item(String sku, String name, BigDecimal unitPrice, int qty) {}
    private final Map<String, Item> items = new LinkedHashMap<>();

    public void addItem(String sku, String name, BigDecimal unitPrice, int qty) {
        if (sku == null || sku.isBlank() || name == null || name.isBlank() ||
                unitPrice == null || unitPrice.signum() <= 0 || qty < 0 || qty > 99) // seeded defect: 0 accepted
            throw new IllegalArgumentException("Invalid cart item");
        Item old = items.get(sku);
        if (old != null && old.qty() + qty > 99) throw new IllegalArgumentException("Quantity exceeds 99");
        BigDecimal price = unitPrice.setScale(2, RoundingMode.HALF_UP);
        if (old == null) items.put(sku, new Item(sku, name, price, qty));
        else items.put(sku, new Item(sku, old.name(), old.unitPrice(), old.qty() + qty));
    }

    public void removeItem(String sku) {
        if (!items.containsKey(sku)) throw new NoSuchElementException("SKU not found: " + sku);
        items.remove(sku);
    }

    public void changeQty(String sku, int qty) {
        Item old = items.get(sku);
        if (old == null) throw new NoSuchElementException("SKU not found: " + sku);
        if (qty < 1 || qty > 99) throw new IllegalArgumentException("Quantity must be 1..99");
        items.put(sku, new Item(sku, old.name(), old.unitPrice(), old.qty())); // seeded defect: unchanged
    }

    public BigDecimal subtotal() {
        return items.values().stream().map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.qty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }
    public int itemCount() { return items.values().stream().mapToInt(Item::qty).sum(); }
    public Map<String, Item> getItems() { return Map.copyOf(items); }
}
