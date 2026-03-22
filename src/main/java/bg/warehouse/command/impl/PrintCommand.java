package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.model.Batch;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.util.*;

public class PrintCommand implements Command {

    @Override
    public void execute(String[] args, Scanner scanner) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            System.out.println(Constants.NO_FILE_OPEN);
            return;
        }

        List<Batch> batches = session.getWarehouse().getBatches();

        if (batches.isEmpty()) {
            System.out.println("The warehouse is empty.");
            return;
        }

        Map<String, Double> totalQuantities = new LinkedHashMap<>();
        for (Batch b : batches) {
            totalQuantities.merge(b.getProductName(), b.getQuantity(), Double::sum);
        }

        String header = String.format("| %-18s | %-15s | %-10s | %-8s | %-5s | %-8s |",
                "Name", "Manufacturer", "Expiry", "Location", "Unit", "Qty");
        String separator = "+" + "-".repeat(20) + "+" + "-".repeat(17) + "+"
                + "-".repeat(12) + "+" + "-".repeat(10) + "+" + "-".repeat(7) + "+"
                + "-".repeat(10) + "+";

        System.out.println(separator);
        System.out.println(header);
        System.out.println(separator);

        Set<String> printed = new HashSet<>();
        for (Batch b : batches) {
            String totalLabel = "";
            if (!printed.contains(b.getProductName())) {
                double total = totalQuantities.get(b.getProductName());
                if (total != b.getQuantity()) {
                    totalLabel = " (total: " + String.format("%.2f", total) + ")";
                }
                printed.add(b.getProductName());
            }

            System.out.printf("| %-18s | %-15s | %-10s | %-8s | %-5s | %8s |%s%n",
                    b.getProductName(),
                    b.getManufacturer() != null ? b.getManufacturer() : "",
                    b.getExpiryDate(),
                    b.getLocation(),
                    b.getUnit().getShortName(),
                    String.format("%.2f", b.getQuantity()),
                    totalLabel);
        }

        System.out.println(separator);
    }
}
