package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.model.Batch;
import bg.warehouse.service.WarehouseService;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Removes expired and soon-to-expire batches (≤ today + EXPIRY_WARNING_DAYS), then prompts for unit price to compute loss. */
public class CleanCommand implements Command {

    private final ConsoleIO io;
    private final WarehouseService service;

    public CleanCommand(ConsoleIO io, WarehouseService service) {
        this.io = io;
        this.service = service;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession.getInstance().requireOpen();

        LocalDate threshold = LocalDate.now().plusDays(Constants.EXPIRY_WARNING_DAYS);
        List<Batch> expired = service.findExpiringBy(threshold);

        if (expired.isEmpty()) {
            io.println("Cleaning expired and soon-to-expire products...");
            io.println("No products due for cleaning.");
            return;
        }

        io.println("Cleaning expired and soon-to-expire products...");
        io.println("Cleaned items:");
        for (Batch batch : expired) {
            io.printf("  %-15s | %-8s | qty: %8.2f | expiry: %s%n",
                    batch.getProductName(),
                    batch.getLocation(),
                    batch.getQuantity(),
                    batch.getExpiryDate());
        }

        service.removeAndLog(expired);
        io.println("Removed " + expired.size() + " expired batch(es).");

        // loss calculation
        Set<String> productNames = new LinkedHashSet<>();
        for (Batch batch : expired) {
            productNames.add(batch.getProductName());
        }

        double totalLoss = 0;
        for (String name : productNames) {
            io.print("Enter price per unit for " + name + " (or press Enter to skip): ");
            String priceStr = io.readLine();
            if (priceStr.isEmpty()) {
                continue;
            }
            try {
                double price = Double.parseDouble(priceStr);
                double productQty = 0;
                for (Batch batch : expired) {
                    if (batch.getProductName().equalsIgnoreCase(name)) {
                        productQty += batch.getQuantity();
                    }
                }
                double loss = productQty * price;
                totalLoss += loss;
                io.printf("  Loss for %s: %.2f%n", name, loss);
            } catch (NumberFormatException e) {
                io.println("  Invalid price, skipping " + name);
            }
        }

        if (totalLoss > 0) {
            io.printf("Total loss: %.2f%n", totalLoss);
        }
    }
}
