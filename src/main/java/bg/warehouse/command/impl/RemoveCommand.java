package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.model.Batch;
import bg.warehouse.model.Warehouse;
import bg.warehouse.session.WarehouseSession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RemoveCommand implements Command {

    @Override
    public void execute(String[] args, Scanner scanner) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            System.out.println("No file is currently open. Use 'open <file>' first.");
            return;
        }

        if (args.length < 3) {
            System.out.println("Usage: remove <product_name> <quantity>");
            return;
        }

        String productName = args[1];
        double quantity;
        try {
            quantity = Double.parseDouble(args[2]);
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        Warehouse warehouse = session.getWarehouse();

        List<Batch> matching = warehouse.getBatches().stream()
                .filter(b -> b.getProductName().equalsIgnoreCase(productName))
                .sorted(Comparator.comparing(Batch::getExpiryDate))
                .collect(Collectors.toList());

        if (matching.isEmpty()) {
            System.out.println("Product not found: " + productName);
            return;
        }

        double remaining = quantity;
        List<Batch> toRemove = new ArrayList<>();

        for (Batch batch : matching) {
            if (remaining <= 0) break;

            double take = Math.min(batch.getQuantity(), remaining);
            batch.setQuantity(batch.getQuantity() - take);
            remaining -= take;

            System.out.println("Removing from batch [" + batch.getLocation()
                    + ", expiry: " + batch.getExpiryDate() + "]: "
                    + String.format("%.2f", take) + " " + batch.getUnit());

            if (batch.getQuantity() <= 0) {
                toRemove.add(batch);
            }
        }

        warehouse.getBatches().removeAll(toRemove);

        System.out.println("Successfully removed " + String.format("%.2f", quantity)
                + " " + matching.get(0).getUnit() + " of " + productName + ".");
    }
}
