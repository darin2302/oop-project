package bg.warehouse.observer;

import bg.warehouse.model.Location;

public interface WarehouseEventListener {
    void onProductAdded(String productName, double quantity, Location location);
    void onProductRemoved(String productName, double quantity, Location location);
}
