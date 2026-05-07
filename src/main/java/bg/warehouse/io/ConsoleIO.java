package bg.warehouse.io;

/**
 * Abstraction over console input and output.
 * Allows commands to be written without depending on System.out / Scanner directly (DIP).
 */
public interface ConsoleIO {

    /** Print a line terminated with a newline. */
    void println(String message);

    /** Print a string without a trailing newline (for inline prompts). */
    void print(String message);

    /** Print using printf-style formatting. */
    void printf(String format, Object... args);

    /** Read a single line of user input, trimmed. */
    String readLine();
}
