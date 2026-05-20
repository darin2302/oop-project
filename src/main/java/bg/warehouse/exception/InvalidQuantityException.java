package bg.warehouse.exception;

/** Thrown when a quantity input is non-positive, NaN, or infinite. */
public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException(String message) {
        super(message);
    }
}
