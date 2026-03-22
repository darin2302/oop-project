package bg.warehouse.service;

import bg.warehouse.model.LogAction;
import bg.warehouse.model.LogEntry;
import bg.warehouse.model.Location;
import bg.warehouse.model.Warehouse;

import java.time.LocalDateTime;

public class LogHelper {

    public static void log(Warehouse warehouse, LogAction action, String productName,
                           double quantity, Location location) {
        LogEntry entry = new LogEntry(
                LocalDateTime.now(), action.name(), productName, quantity,
                location.toString());
        warehouse.getLogEntries().add(entry);
    }
}
