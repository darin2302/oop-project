package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.model.Batch;
import bg.warehouse.model.Warehouse;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CleanCommand implements Command {

    @Override
    public void execute(String[] args, Scanner scanner) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            System.out.println(Constants.NO_FILE_OPEN);
            return;
        }

        Warehouse warehouse = session.getWarehouse();
        LocalDate today = LocalDate.now();

        List<Batch> expired = new ArrayList<>();
        for (Batch batch : warehouse.getBatches()) {
            if (!batch.getExpiryDate().isAfter(today)) {
                expired.add(batch);
            }
        }

        if (expired.isEmpty()) {
            System.out.println("Cleaning expired and soon-to-expire products...");
            System.out.println("No products due for cleaning.");
            return;
        }

        System.out.println("Cleaning expired and soon-to-expire products...");
        System.out.println("Cleaned items:");
        for (Batch batch : expired) {
            System.out.printf("  %-15s | %-8s | qty: %8.2f | expiry: %s%n",
                    batch.getProductName(),
                    batch.getLocation(),
                    batch.getQuantity(),
                    batch.getExpiryDate());
        }

        warehouse.getBatches().removeAll(expired);
        System.out.println("Removed " + expired.size() + " expired batch(es).");
    }
}
