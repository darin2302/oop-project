package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.model.LogEntry;
import bg.warehouse.model.Warehouse;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class LogCommand implements Command {

    @Override
    public void execute(String[] args, Scanner scanner) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            System.out.println(Constants.NO_FILE_OPEN);
            return;
        }

        if (args.length < 3) {
            System.out.println("Usage: log <from> <to> (dates in yyyy-MM-dd)");
            return;
        }

        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.parse(args[1], Constants.DATE_FORMAT);
            to = LocalDate.parse(args[2], Constants.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        Warehouse warehouse = session.getWarehouse();
        List<LogEntry> entries = warehouse.getLogEntries();

        boolean found = false;
        for (LogEntry entry : entries) {
            LocalDate entryDate = entry.getTimestamp().toLocalDate();
            if (!entryDate.isBefore(from) && !entryDate.isAfter(to)) {
                System.out.printf("[%s] %-6s %-15s %8.2f @ %s%n",
                        entry.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        entry.getAction(),
                        entry.getProductName(),
                        entry.getQuantity(),
                        entry.getLocation());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No log entries found in the given date range.");
        }
    }
}
