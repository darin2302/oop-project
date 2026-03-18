package bg.warehouse.command.impl;

import bg.warehouse.command.Command;

import java.util.Scanner;

public class ExitCommand implements Command {

    @Override
    public void execute(String[] args, Scanner scanner) {
        System.out.println("Exiting the program...");
        System.exit(0);
    }
}
