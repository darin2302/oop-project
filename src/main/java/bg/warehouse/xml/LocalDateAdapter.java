package bg.warehouse.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate unmarshal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value, FORMAT);
    }

    @Override
    public String marshal(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(FORMAT);
    }
}
