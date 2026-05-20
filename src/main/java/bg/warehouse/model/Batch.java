package bg.warehouse.model;

import bg.warehouse.util.Constants;
import bg.warehouse.xml.LocalDateAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/**
 * A physical placement of stock: a quantity of one product at one location with a single expiry date.
 * Identified by (productName, expiryDate). Persisted via JAXB.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Batch {

    @XmlElement(required = true)
    private String productName;

    private String manufacturer;

    @XmlElement(required = true)
    private Unit unit;

    @XmlElement(required = true)
    private double quantity;

    @XmlElement
    private double volumePerUnit = Constants.DEFAULT_VOLUME_PER_UNIT;

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate expiryDate;

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate entryDate;

    @XmlElement(required = true)
    private Location location;

    private String comment;

    public Batch() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getVolumePerUnit() {
        return volumePerUnit > 0 ? volumePerUnit : Constants.DEFAULT_VOLUME_PER_UNIT;
    }

    public void setVolumePerUnit(double volumePerUnit) {
        this.volumePerUnit = volumePerUnit;
    }

    public double getOccupiedVolume() {
        return quantity * getVolumePerUnit();
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
