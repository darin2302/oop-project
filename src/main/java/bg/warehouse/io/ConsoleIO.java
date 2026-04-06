package bg.warehouse.io;

public interface ConsoleIO {
    void println(String message);
    void print(String message);
    void printf(String format, Object... args);
    String readLine();
}
