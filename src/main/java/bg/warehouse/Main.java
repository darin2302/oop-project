package bg.warehouse;

import bg.warehouse.cli.CommandLineInterface;

public class Main {
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.run();
    }
}
