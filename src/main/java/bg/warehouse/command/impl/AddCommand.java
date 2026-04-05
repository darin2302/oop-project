package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.model.*;
import bg.warehouse.service.LocationAllocator;
import bg.warehouse.service.LogHelper;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class AddCommand implements Command {

    private final ConsoleIO io;
    private final LocationAllocator locationAllocator;

    public AddCommand(ConsoleIO io, LocationAllocator locationAllocator) {
        this.io = io;
        this.locationAllocator = locationAllocator;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
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
        String expiryStr = io.readLine();
        LocalDate expiryDate;
        try {
            expiryDate = LocalDate.parse(expiryStr, Constants.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            io.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        io.print("Entry date (yyyy-MM-dd): ");
        String entryStr = io.readLine();
        LocalDate entryDate;
        try {
            entryDate = LocalDate.parse(entryStr, Constants.DATE_FORMAT);
        } catch (DateTimeParseException e) {
            io.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        io.print("Comment: ");
        String comment = io.readLine();

        Warehouse warehouse = session.getWarehouse();

        for (Batch existing : warehouse.getBatches()) {
            if (existing.getProductName().equalsIgnoreCase(name)
                    && existing.getExpiryDate().equals(expiryDate)) {
                existing.setQuantity(existing.getQuantity() + quantity);
                io.println("Merged with existing batch at location " + existing.getLocation() + ".");

                LogHelper.log(warehouse, LogAction.ADD, name, quantity, existing.getLocation());
                return;
            }
        }

        Optional<Location> freeSlot = locationAllocator.findFreeSlot(warehouse);
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
        Batch batch = product.toBatch(location);
        warehouse.getBatches().add(batch);

        LogHelper.log(warehouse, LogAction.ADD, name, quantity, location);

        io.println("Product added at location " + location + ".");
    }
}
