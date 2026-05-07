package bg.warehouse.observer;

import bg.warehouse.model.Location;

/**
 * Observer interface for warehouse mutation events.
 * AuditLogger is the default listener; more can be added via WarehouseService.addListener.
 */
public interface WarehouseEventListener {

    /** Fired after a product batch is added or merged. */
    void onProductAdded(String productName, double quantity, Location location);

    /** Fired after a product is removed (by remove or clean). */
    void onProductRemoved(String productName, double quantity, Location location);
}
