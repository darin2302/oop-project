package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.model.Batch;
import bg.warehouse.model.LogAction;
import bg.warehouse.model.Warehouse;
import bg.warehouse.service.LogHelper;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

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
            System.out.println(Constants.NO_FILE_OPEN);
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

        double totalAvailable = matching.stream()
                .mapToDouble(Batch::getQuantity)
                .sum();

        if (quantity > totalAvailable) {
            System.out.println("Not enough stock. Available: " + String.format("%.2f", totalAvailable)
                    + " " + matching.get(0).getUnit());
            System.out.println("Batches:");
            for (Batch b : matching) {
                System.out.println("  [" + b.getLocation() + ", expiry: " + b.getExpiryDate()
                        + "]: " + String.format("%.2f", b.getQuantity()) + " " + b.getUnit());
            }
            System.out.print("Do you want to remove all available stock? (yes/no): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (!answer.equals("yes")) {
                System.out.println("Removal cancelled.");
                return;
            }
            quantity = totalAvailable;
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

            LogHelper.log(warehouse, LogAction.REMOVE, productName, take, batch.getLocation());

            if (batch.getQuantity() <= 0) {
                toRemove.add(batch);
            }
        }

        warehouse.getBatches().removeAll(toRemove);

        System.out.println("Successfully removed " + String.format("%.2f", quantity)
                + " " + matching.get(0).getUnit() + " of " + productName + ".");
    }
}
