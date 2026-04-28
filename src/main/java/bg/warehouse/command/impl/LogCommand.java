package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.model.LogEntry;
import bg.warehouse.service.WarehouseService;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LogCommand implements Command {

    private final ConsoleIO io;
    private final WarehouseService service;

    public LogCommand(ConsoleIO io, WarehouseService service) {
        this.io = io;
        this.service = service;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession.getInstance().requireOpen();

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

        List<LogEntry> entries = service.queryLog(from, to);

        if (entries.isEmpty()) {
            io.println("No log entries found in the given date range.");
            return;
        }

        for (LogEntry entry : entries) {
            io.printf("[%s] %-6s %-15s %8.2f @ %s%n",
                    entry.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    entry.getAction(),
                    entry.getProductName(),
                    entry.getQuantity(),
                    entry.getLocation());
        }
    }
}
