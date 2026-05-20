package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.model.Batch;
import bg.warehouse.service.WarehouseService;
import bg.warehouse.session.WarehouseSession;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Prints all batches in a tabular layout with per-product aggregated totals. */
public class PrintCommand implements Command {

    private final ConsoleIO io;
    private final WarehouseService service;

    public PrintCommand(ConsoleIO io, WarehouseService service) {
        this.io = io;
        this.service = service;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession.getInstance().requireOpen();

        List<Batch> batches = service.getAllBatches();

        if (batches.isEmpty()) {
            io.println("The warehouse is empty.");
            return;
        }

        Map<String, Double> totals = service.totalsByProductName();

        String header = String.format("| %-18s | %-15s | %-10s | %-8s | %-5s | %-8s | %-8s | %-7s |",
                "Name", "Manufacturer", "Expiry", "Location", "Unit", "Qty", "Vol(L)", "L/unit");
        String separator = "+" + "-".repeat(20) + "+" + "-".repeat(17) + "+"
                + "-".repeat(12) + "+" + "-".repeat(10) + "+" + "-".repeat(7) + "+"
                + "-".repeat(10) + "+" + "-".repeat(10) + "+" + "-".repeat(9) + "+";

        io.println(separator);
        io.println(header);
        io.println(separator);

        Set<String> printed = new HashSet<>();
        for (Batch b : batches) {
            String totalLabel = "";
            if (!printed.contains(b.getProductName())) {
                double total = totals.get(b.getProductName());
                if (total != b.getQuantity()) {
                    totalLabel = " (total: " + String.format("%.2f", total) + ")";
                }
                printed.add(b.getProductName());
            }

            io.printf("| %-18s | %-15s | %-10s | %-8s | %-5s | %8s | %8s | %7s |%s%n",
                    b.getProductName(),
                    b.getManufacturer() != null ? b.getManufacturer() : "",
                    b.getExpiryDate(),
                    b.getLocation(),
                    b.getUnit().getShortName(),
                    String.format("%.2f", b.getQuantity()),
                    String.format("%.2f", b.getOccupiedVolume()),
                    String.format("%.2f", b.getVolumePerUnit()),
                    totalLabel);
        }

        io.println(separator);
    }
}
