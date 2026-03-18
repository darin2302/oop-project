package bg.warehouse.command.impl;

import bg.warehouse.command.Command;

import java.util.Scanner;

public class HelpCommand implements Command {

    @Override
    public void execute(String[] args, Scanner scanner) {
        System.out.println("The following commands are supported:");
        System.out.println("  open <file>          opens <file>");
        System.out.println("  close                closes currently opened file");
        System.out.println("  save                 saves the currently open file");
        System.out.println("  save as <file>       saves the currently open file in <file>");
        System.out.println("  print                prints warehouse inventory");
        System.out.println("  add                  adds a new product to the warehouse");
        System.out.println("  remove <name> <qty>  removes product from the warehouse");
        System.out.println("  log <from> <to>      shows changes in the given date range");
        System.out.println("  clean                removes expired and soon-to-expire products");
        System.out.println("  help                 prints this information");
        System.out.println("  exit                 exits the program");
    }
}
