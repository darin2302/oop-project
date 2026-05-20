package bg.warehouse.exception;

import bg.warehouse.util.Constants;

/** Thrown by commands that require an open session when none is active. */
public class NoFileOpenException extends RuntimeException {

    public NoFileOpenException() {
        super(Constants.NO_FILE_OPEN);
    }
}
