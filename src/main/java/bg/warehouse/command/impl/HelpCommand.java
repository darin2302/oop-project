package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;

public class HelpCommand implements Command {

    private final ConsoleIO io;

    public HelpCommand(ConsoleIO io) {
        this.io = io;
    }

    @Override
    public void execute(String[] args) {
        io.println("The following commands are supported:");
        io.println("  open <file>          opens <file>");
        io.println("  close                closes currently opened file");
        io.println("  save                 saves the currently open file");
        io.println("  save as <file>       saves the currently open file in <file>");
        io.println("  print                prints warehouse inventory");
        io.println("  add                  adds a new product to the warehouse");
        io.println("  remove <name> <qty>  removes product from the warehouse");
        io.println("  log <from> <to>      shows changes in the given date range");
        io.println("  clean                removes expired and soon-to-expire products");
        io.println("  help                 prints this information");
        io.println("  exit                 exits the program");
    }
}
