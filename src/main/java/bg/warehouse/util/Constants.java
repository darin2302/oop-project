package bg.warehouse.util;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final String NO_FILE_OPEN = "No file is currently open. Use 'open <file>' first.";

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static final int EXPIRY_WARNING_DAYS = 3;
}
