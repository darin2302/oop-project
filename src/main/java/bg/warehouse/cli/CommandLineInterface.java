package bg.warehouse.cli;

import bg.warehouse.command.Command;
import bg.warehouse.command.CommandFactory;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.io.SystemConsoleIO;

import java.util.Scanner;

public class CommandLineInterface {

    private final CommandFactory commandFactory;
    private final ConsoleIO io;

    public CommandLineInterface() {
        this.io = new SystemConsoleIO(new Scanner(System.in));
        this.commandFactory = new CommandFactory(io);
    }

    public CommandFactory getCommandFactory() {
        return commandFactory;
    }

    public void run() {
        io.println("Warehouse Management System");
        io.println("Type 'help' for a list of commands.");

        while (true) {
            io.print("> ");
            String input = io.readLine();

            if (input.isEmpty()) {
                continue;
            }

            String[] tokens = CommandFactory.tokenize(input);
            String commandName = tokens[0].toLowerCase();

            if ("save".equals(commandName) && tokens.length > 1 && "as".equalsIgnoreCase(tokens[1])) {
                commandName = "save as";
            }

            Command command = commandFactory.getCommand(commandName);

            if (command == null) {
                io.println("Unknown command: " + tokens[0] + ". Type 'help' for a list of commands.");
                continue;
            }

            command.execute(tokens);
        }
    }
}
