package bg.warehouse.exception;

/** Thrown when remove/query operations target a product name that has no matching batches. */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productName) {
        super("Product not found: " + productName);
    }
}
