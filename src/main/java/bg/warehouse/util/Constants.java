package bg.warehouse.util;

import java.time.format.DateTimeFormatter;

/** Shared message strings, date formats, and warehouse configuration values. */
public class Constants {

    public static final String NO_FILE_OPEN = "No file is currently open. Use 'open <file>' first.";

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static final int EXPIRY_WARNING_DAYS = 3;

    /** Maximum volume (in litres) a single slot can hold. One slot = one batch. */
    public static final double SLOT_CAPACITY_LITERS = 100.0;

    /** Default volume per unit (litres) when the user does not specify one. */
    public static final double DEFAULT_VOLUME_PER_UNIT = 1.0;
}
