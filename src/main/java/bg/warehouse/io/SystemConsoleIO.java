package bg.warehouse.io;

import java.util.Scanner;

public class SystemConsoleIO implements ConsoleIO {

    private final Scanner scanner;

    public SystemConsoleIO(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    @Override
    public String readLine() {
        return scanner.nextLine().trim();
    }
}
