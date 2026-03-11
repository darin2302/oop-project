package bg.warehouse.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "unit")
@XmlEnum
public enum Unit {

    @XmlEnumValue("KILOGRAMS")
    KILOGRAMS("KG"),

    @XmlEnumValue("LITRES")
    LITRES("L");

    private final String shortName;

    Unit(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public static Unit fromString(String text) {
        for (Unit u : Unit.values()) {
            if (u.name().equalsIgnoreCase(text) || u.shortName.equalsIgnoreCase(text)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Unknown unit: " + text);
    }
}
