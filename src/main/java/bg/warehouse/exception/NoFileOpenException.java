package bg.warehouse.exception;

import bg.warehouse.util.Constants;

public class NoFileOpenException extends RuntimeException {

    public NoFileOpenException() {
        super(Constants.NO_FILE_OPEN);
    }
}
