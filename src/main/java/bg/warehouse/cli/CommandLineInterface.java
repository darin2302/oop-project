package bg.warehouse.cli;

import bg.warehouse.command.Command;
import bg.warehouse.command.CommandFactory;

import java.util.Scanner;

public class CommandLineInterface {

    private final CommandFactory commandFactory;
    private final Scanner scanner;

    public CommandLineInterface() {
        this.commandFactory = new CommandFactory();
        this.scanner = new Scanner(System.in);
    }

    public CommandFactory getCommandFactory() {
        return commandFactory;
    }

    public void run() {
        System.out.println("Warehouse Management System");
        System.out.println("Type 'help' for a list of commands.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

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
                System.out.println("Unknown command: " + tokens[0] + ". Type 'help' for a list of commands.");
                continue;
            }

            command.execute(tokens, scanner);
        }
    }
}
