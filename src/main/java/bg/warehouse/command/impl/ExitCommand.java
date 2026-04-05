package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;

public class ExitCommand implements Command {

    private final ConsoleIO io;

    public ExitCommand(ConsoleIO io) {
        this.io = io;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Exiting the program...");
        System.exit(0);
    }
}
