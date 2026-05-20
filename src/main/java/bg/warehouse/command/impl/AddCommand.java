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

/**
 * Interactively prompts for product fields, then either merges into an existing
 * (name + expiry) batch or allocates a fresh slot. Enforces slot volume capacity.
 */
public class AddCommand implements Command {

    private final ConsoleIO io;
    private final WarehouseService service;

    public AddCommand(ConsoleIO io, WarehouseService service) {
        this.io = io;
        this.service = service;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession.getInstance().requireOpen();

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
            if (quantity <= 0 || Double.isNaN(quantity) || Double.isInfinite(quantity)) {
                io.println("Invalid quantity.");
                return;
            }
        } catch (NumberFormatException e) {
            io.println("Invalid quantity.");
            return;
        }

        io.print("Volume per unit in litres (press Enter for " + Constants.DEFAULT_VOLUME_PER_UNIT + "): ");
        String vpuStr = io.readLine();
        double volumePerUnit;
        if (vpuStr.isBlank()) {
            volumePerUnit = Constants.DEFAULT_VOLUME_PER_UNIT;
        } else {
            try {
                volumePerUnit = Double.parseDouble(vpuStr);
                if (volumePerUnit <= 0 || Double.isNaN(volumePerUnit) || Double.isInfinite(volumePerUnit)) {
                    io.println("Invalid volume per unit.");
                    return;
                }
            } catch (NumberFormatException e) {
                io.println("Invalid volume per unit.");
                return;
            }
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
            Batch e = existing.get();
            double mergedVolume = (e.getQuantity() + quantity) * e.getVolumePerUnit();
            if (mergedVolume > Constants.SLOT_CAPACITY_LITERS) {
                double availableQty = (Constants.SLOT_CAPACITY_LITERS / e.getVolumePerUnit()) - e.getQuantity();
                io.printf("Merge would exceed slot capacity (%.2fL). Slot %s holds %.2f, max additional: %.2f%n",
                        Constants.SLOT_CAPACITY_LITERS, e.getLocation(), e.getQuantity(),
                        Math.max(0, availableQty));
                return;
            }
            service.mergeIntoBatch(e, quantity);
            io.println("Merged with existing batch at location " + e.getLocation() + ".");
            return;
        }

        double newVolume = quantity * volumePerUnit;
        if (newVolume > Constants.SLOT_CAPACITY_LITERS) {
            double maxQty = Constants.SLOT_CAPACITY_LITERS / volumePerUnit;
            io.printf("Quantity exceeds slot capacity (%.2fL). At %.2fL/unit, max per slot: %.2f%n",
                    Constants.SLOT_CAPACITY_LITERS, volumePerUnit, maxQty);
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
                .volumePerUnit(volumePerUnit)
                .expiryDate(expiryDate)
                .entryDate(entryDate)
                .comment(comment)
                .build();

        Location location = freeSlot.get();
        service.addBatch(product, location);
        io.printf("Product added at location %s (occupies %.2fL of %.2fL).%n",
                location, newVolume, Constants.SLOT_CAPACITY_LITERS);
    }
}
