package bg.warehouse;

import bg.warehouse.cli.CommandLineInterface;
import bg.warehouse.exception.WarehouseLoadException;

public class Main {
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        try {
            cli.run();
        } catch (WarehouseLoadException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
