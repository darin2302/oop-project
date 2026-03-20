package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.model.Batch;
import bg.warehouse.model.Warehouse;
import bg.warehouse.session.WarehouseSession;

import java.util.List;
import java.util.Scanner;

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
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        Warehouse warehouse = session.getWarehouse();
        List<Batch> batches = warehouse.getBatches();

        for (Batch batch : batches) {
            if (batch.getProductName().equalsIgnoreCase(productName)) {
                batch.setQuantity(batch.getQuantity() - quantity);
                System.out.println("Removing from batch [" + batch.getLocation()
                        + ", expiry: " + batch.getExpiryDate() + "]: "
                        + String.format("%.2f", quantity) + " " + batch.getUnit());
                System.out.println("Successfully removed " + String.format("%.2f", quantity)
                        + " " + batch.getUnit() + " of " + productName + ".");
                return;
            }
        }

        System.out.println("Product not found: " + productName);
    }
}
