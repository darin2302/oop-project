package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.model.LogEntry;
import bg.warehouse.model.Warehouse;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LogCommand implements Command {

    private final ConsoleIO io;

    public LogCommand(ConsoleIO io) {
        this.io = io;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            io.println(Constants.NO_FILE_OPEN);
            return;
        }

        if (args.length < 3) {
            io.println("Usage: log <from> <to> (dates in yyyy-MM-dd)");
            return;
        }

        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.parse(args[1], Constants.DATE_FORMAT);
            to = LocalDate.parse(args[2], Constants.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            io.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        Warehouse warehouse = session.getWarehouse();
        List<LogEntry> entries = warehouse.getLogEntries();

        boolean found = false;
        for (LogEntry entry : entries) {
            LocalDate entryDate = entry.getTimestamp().toLocalDate();
            if (!entryDate.isBefore(from) && !entryDate.isAfter(to)) {
                io.printf("[%s] %-6s %-15s %8.2f @ %s%n",
                        entry.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        entry.getAction(),
                        entry.getProductName(),
                        entry.getQuantity(),
                        entry.getLocation());
                found = true;
            }
        }

        if (!found) {
            io.println("No log entries found in the given date range.");
        }
    }
}
