package bg.warehouse.exception;

/**
 * Fatal: warehouse file could not be loaded (missing, malformed, unreadable).
 * Propagates to Main, which prints the message and exits with code 1.
 */
public class WarehouseLoadException extends RuntimeException {

    public WarehouseLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
