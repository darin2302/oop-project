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
import java.util.List;
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

        List<Batch> existing = service.findBatchesByNameAndExpiry(name, expiryDate);
        double effectiveVpu = existing.isEmpty() ? volumePerUnit : existing.get(0).getVolumePerUnit();
        double remaining = quantity;

        // Step 1: top up existing slots (same name + expiry) to capacity
        for (Batch b : existing) {
            if (remaining <= 0) break;
            double slotCapUnits = Constants.SLOT_CAPACITY_LITERS / b.getVolumePerUnit();
            double free = slotCapUnits - b.getQuantity();
            if (free <= 0) continue;
            double take = Math.min(remaining, free);
            service.mergeIntoBatch(b, take);
            io.printf("Merged %.2f into existing slot %s (now %.2f of %.2f units).%n",
                    take, b.getLocation(), b.getQuantity(), slotCapUnits);
            remaining -= take;
        }

        // Step 2: spill remainder into new slots
        while (remaining > 0) {
            Optional<Location> freeSlot = service.findFreeSlot();
            if (freeSlot.isEmpty()) {
                io.printf("Warehouse is full. Unplaced quantity: %.2f%n", remaining);
                return;
            }
            double maxPerSlot = Constants.SLOT_CAPACITY_LITERS / effectiveVpu;
            double chunk = Math.min(remaining, maxPerSlot);

            Product product = new Product.Builder()
                    .name(name)
                    .manufacturer(manufacturer)
                    .unit(unit)
                    .quantity(chunk)
                    .volumePerUnit(effectiveVpu)
                    .expiryDate(expiryDate)
                    .entryDate(entryDate)
                    .comment(comment)
                    .build();

            Location loc = freeSlot.get();
            service.addBatch(product, loc);
            io.printf("Placed %.2f at new slot %s (occupies %.2fL of %.2fL).%n",
                    chunk, loc, chunk * effectiveVpu, Constants.SLOT_CAPACITY_LITERS);
            remaining -= chunk;
        }
    }
}
