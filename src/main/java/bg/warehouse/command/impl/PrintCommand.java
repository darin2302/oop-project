package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.model.Batch;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;

import java.util.*;

public class PrintCommand implements Command {

    private final ConsoleIO io;

    public PrintCommand(ConsoleIO io) {
        this.io = io;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            io.println(Constants.NO_FILE_OPEN);
            return;
        }

        List<Batch> batches = session.getWarehouse().getBatches();

        if (batches.isEmpty()) {
            io.println("The warehouse is empty.");
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

        io.println(separator);
        io.println(header);
        io.println(separator);

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

            io.printf("| %-18s | %-15s | %-10s | %-8s | %-5s | %8s |%s%n",
                    b.getProductName(),
                    b.getManufacturer() != null ? b.getManufacturer() : "",
                    b.getExpiryDate(),
                    b.getLocation(),
                    b.getUnit().getShortName(),
                    String.format("%.2f", b.getQuantity()),
                    totalLabel);
        }

        io.println(separator);
    }
}
