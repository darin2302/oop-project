package bg.warehouse.command;

import java.util.Scanner;

public interface Command {

    void execute(String[] args, Scanner scanner);
}
