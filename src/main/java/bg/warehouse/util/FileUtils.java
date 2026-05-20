package bg.warehouse.util;

/** Cross-platform path helpers. */
public class FileUtils {

    public static String getFileName(String filePath) {
        if (filePath.contains("\\")) {
            return filePath.substring(filePath.lastIndexOf('\\') + 1);
        } else if (filePath.contains("/")) {
            return filePath.substring(filePath.lastIndexOf('/') + 1);
        }
        return filePath;
    }
}
