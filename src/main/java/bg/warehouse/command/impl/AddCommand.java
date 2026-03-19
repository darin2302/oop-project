package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.model.*;
import bg.warehouse.service.LocationAllocator;
import bg.warehouse.session.WarehouseSession;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

public class AddCommand implements Command {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocationAllocator locationAllocator = new LocationAllocator();

    @Override
    public void execute(String[] args, Scanner scanner) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            System.out.println("No file is currently open. Use 'open <file>' first.");
            return;
        }

        String name = prompt(scanner, "Product name: ");
        if (name.isBlank()) {
            System.out.println("Product name cannot be empty.");
            return;
        }

        String manufacturer = prompt(scanner, "Manufacturer: ");

        String unitStr = prompt(scanner, "Unit (KILOGRAMS/LITRES): ");
        Unit unit;
        try {
            unit = Unit.fromString(unitStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid unit. Use KILOGRAMS or LITRES.");
            return;
        }

        String qtyStr = prompt(scanner, "Quantity: ");
        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        String expiryStr = prompt(scanner, "Expiry date (yyyy-MM-dd): ");
        LocalDate expiryDate;
        try {
            expiryDate = LocalDate.parse(expiryStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        String entryStr = prompt(scanner, "Entry date (yyyy-MM-dd): ");
        LocalDate entryDate;
        try {
            entryDate = LocalDate.parse(entryStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        String comment = prompt(scanner, "Comment: ");

        Warehouse warehouse = session.getWarehouse();

        for (Batch existing : warehouse.getBatches()) {
            if (existing.getProductName().equalsIgnoreCase(name)
                    && existing.getExpiryDate().equals(expiryDate)) {
                existing.setQuantity(existing.getQuantity() + quantity);
                System.out.println("Merged with existing batch at location " + existing.getLocation() + ".");

                LogEntry logEntry = new LogEntry(
                        java.time.LocalDateTime.now(), "ADD", name, quantity,
                        existing.getLocation().toString());
                warehouse.getLogEntries().add(logEntry);
                return;
            }
        }

        Optional<Location> freeSlot = locationAllocator.findFreeSlot(warehouse);
        if (freeSlot.isEmpty()) {
            System.out.println("Warehouse is full. Cannot add product.");
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

        LogEntry logEntry = new LogEntry(
                java.time.LocalDateTime.now(), "ADD", name, quantity,
                location.toString());
        warehouse.getLogEntries().add(logEntry);

        System.out.println("Product added at location " + location + ".");
    }

    private String prompt(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}
