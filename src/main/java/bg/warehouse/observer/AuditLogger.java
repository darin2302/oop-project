package bg.warehouse.observer;

import bg.warehouse.model.LogAction;
import bg.warehouse.model.LogEntry;
import bg.warehouse.model.Location;
import bg.warehouse.session.WarehouseSession;

import java.time.LocalDateTime;

public class AuditLogger implements WarehouseEventListener {

    private final WarehouseSession session;

    public AuditLogger(WarehouseSession session) {
        this.session = session;
    }

    @Override
    public void onProductAdded(String productName, double quantity, Location location) {
        append(LogAction.ADD, productName, quantity, location);
    }

    @Override
    public void onProductRemoved(String productName, double quantity, Location location) {
        append(LogAction.REMOVE, productName, quantity, location);
    }

    private void append(LogAction action, String productName, double quantity, Location location) {
        LogEntry entry = new LogEntry(
                LocalDateTime.now(), action.name(), productName, quantity, location.toString());
        session.getWarehouse().getLogEntries().add(entry);
    }
}
