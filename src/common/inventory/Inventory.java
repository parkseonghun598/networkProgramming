package common.inventory;

import common.item.Item;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private static final int MAX_SLOTS = 24; // 6x4 grid
    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public boolean addItem(Item item) {
        if (items.size() >= MAX_SLOTS) {
            return false; // 인벤토리가 가득 참
        }
        items.add(item);
        return true;
    }

    public boolean removeItem(String itemId) {
        return items.removeIf(item -> item.getId().equals(itemId));
    }

    public Item getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean isFull() {
        return items.size() >= MAX_SLOTS;
    }
}

