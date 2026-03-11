package bg.warehouse.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Location {

    @XmlElement(required = true)
    private String section;

    @XmlElement(required = true)
    private int shelf;

    @XmlElement(required = true)
    private String slot;

    public Location() {
    }

    public Location(String section, int shelf, String slot) {
        this.section = section;
        this.shelf = shelf;
        this.slot = slot;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getShelf() {
        return shelf;
    }

    public void setShelf(int shelf) {
        this.shelf = shelf;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        return section + "-" + shelf + "-" + slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return shelf == location.shelf
                && Objects.equals(section, location.section)
                && Objects.equals(slot, location.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(section, shelf, slot);
    }
}
