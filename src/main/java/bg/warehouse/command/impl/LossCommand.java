package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.exception.InvalidQuantityException;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.service.WarehouseService;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LossCommand implements Command {

    private final ConsoleIO io;
    private final WarehouseService service;

    public LossCommand(ConsoleIO io, WarehouseService service) {
        this.io = io;
        this.service = service;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession.getInstance().requireOpen();

        if (args.length < 5) {
            io.println("Usage: loss <product> <price> <from> <to>");
            return;
        }

        String product = args[1];

        double price;
        try {
            price = Double.parseDouble(args[2]);
            if (price <= 0 || Double.isNaN(price) || Double.isInfinite(price)) {
                throw new InvalidQuantityException("Price must be positive.");
            }
        } catch (NumberFormatException e) {
            io.println("Invalid price.");
            return;
        }

        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.parse(args[3], Constants.DATE_FORMAT);
            to = LocalDate.parse(args[4], Constants.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            io.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        double qty = service.lossInPeriod(product, from, to);
        double total = qty * price;
        io.printf("Lost %.2f of %s @ %.2f/unit = %.2f over [%s..%s]%n",
                qty, product, price, total, from, to);
    }
}
