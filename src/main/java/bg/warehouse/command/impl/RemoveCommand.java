package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.model.Batch;
import bg.warehouse.service.RemovalResult;
import bg.warehouse.service.WarehouseService;
import bg.warehouse.session.WarehouseSession;

import java.util.List;

public class RemoveCommand implements Command {

    private final ConsoleIO io;
    private final WarehouseService service;

    public RemoveCommand(ConsoleIO io, WarehouseService service) {
        this.io = io;
        this.service = service;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession.getInstance().requireOpen();

        if (args.length < 3) {
            io.println("Usage: remove <product_name> <quantity>");
            return;
        }

        String productName = args[1];
        double quantity;
        try {
            quantity = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            io.println("Invalid quantity.");
            return;
        }

        List<Batch> matching = service.requireBatchesByName(productName);

        double totalAvailable = service.totalQuantity(matching);

        if (quantity > totalAvailable) {
            io.println("Not enough stock. Available: " + String.format("%.2f", totalAvailable)
                    + " " + matching.get(0).getUnit());
            io.println("Batches:");
            for (Batch b : matching) {
                io.println("  [" + b.getLocation() + ", expiry: " + b.getExpiryDate()
                        + "]: " + String.format("%.2f", b.getQuantity()) + " " + b.getUnit());
            }
            io.print("Do you want to remove all available stock? (yes/no): ");
            String answer = io.readLine().toLowerCase();
            if (!answer.equals("yes")) {
                io.println("Removal cancelled.");
                return;
            }
            quantity = totalAvailable;
        }

        List<RemovalResult> results = service.drain(matching, productName, quantity);
        for (RemovalResult r : results) {
            io.println("Removing from batch [" + r.batch().getLocation()
                    + ", expiry: " + r.batch().getExpiryDate() + "]: "
                    + String.format("%.2f", r.amountTaken()) + " " + r.batch().getUnit());
        }

        io.println("Successfully removed " + String.format("%.2f", quantity)
                + " " + matching.get(0).getUnit() + " of " + productName + ".");
    }
}
