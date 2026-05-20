package bg.warehouse;

import bg.warehouse.cli.CommandLineInterface;
import bg.warehouse.exception.WarehouseLoadException;

/** Application entry point. Starts the REPL and converts fatal load errors into exit code 1. */
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
