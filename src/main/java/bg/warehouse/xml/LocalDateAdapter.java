package bg.warehouse.xml;

import bg.warehouse.util.Constants;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value, Constants.DATE_FORMAT);
    }

    @Override
    public String marshal(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(Constants.DATE_FORMAT);
    }
}
