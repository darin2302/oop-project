package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.model.Batch;
import bg.warehouse.model.Location;
import bg.warehouse.model.Product;
import bg.warehouse.model.Unit;
import bg.warehouse.service.WarehouseService;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class AddCommand implements Command {

    private final ConsoleIO io;
    private final WarehouseService service;

    public AddCommand(ConsoleIO io, WarehouseService service) {
        this.io = io;
        this.service = service;
    }

    @Override
    public void execute(String[] args) {
        if (!WarehouseSession.getInstance().isFileOpen()) {
            io.println(Constants.NO_FILE_OPEN);
            return;
        }

        io.print("Product name: ");
        String name = io.readLine();
        if (name.isBlank()) {
            io.println("Product name cannot be empty.");
            return;
        }

        io.print("Manufacturer: ");
        String manufacturer = io.readLine();

        io.print("Unit (KILOGRAMS/LITRES): ");
        String unitStr = io.readLine();
        Unit unit;
        try {
            unit = Unit.fromString(unitStr);
        } catch (IllegalArgumentException e) {
            io.println("Invalid unit. Use KILOGRAMS or LITRES.");
            return;
        }

        io.print("Quantity: ");
        String qtyStr = io.readLine();
        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                io.println("Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            io.println("Invalid quantity.");
            return;
        }

        io.print("Expiry date (yyyy-MM-dd): ");
        LocalDate expiryDate;
        try {
            expiryDate = LocalDate.parse(io.readLine(), Constants.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            io.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        io.print("Entry date (yyyy-MM-dd): ");
        LocalDate entryDate;
        try {
            entryDate = LocalDate.parse(io.readLine(), Constants.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            io.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        io.print("Comment: ");
        String comment = io.readLine();

        Optional<Batch> existing = service.findBatchByNameAndExpiry(name, expiryDate);
        if (existing.isPresent()) {
            service.mergeIntoBatch(existing.get(), quantity);
            io.println("Merged with existing batch at location " + existing.get().getLocation() + ".");
            return;
        }

        Optional<Location> freeSlot = service.findFreeSlot();
        if (freeSlot.isEmpty()) {
            io.println("Warehouse is full. Cannot add product.");
            return;
        }

        Product product = new Product.Builder()
                .name(name)
                .manufacturer(manufacturer)
                .unit(unit)
                .quantity(quantity)
                .expiryDate(expiryDate)
                .entryDate(entryDate)
                .comment(comment)
                .build();

        Location location = freeSlot.get();
        service.addBatch(product, location);
        io.println("Product added at location " + location + ".");
    }
}
