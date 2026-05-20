package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;

/** Terminates the REPL loop and exits the JVM. */
public class ExitCommand implements Command {

    private final ConsoleIO io;

    public ExitCommand(ConsoleIO io) {
        this.io = io;
    }

    @Override
    public void execute(String[] args) {
        io.println("Exiting the program...");
        System.exit(0);
    }
}
