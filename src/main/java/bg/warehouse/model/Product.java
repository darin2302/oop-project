package bg.warehouse.model;

import bg.warehouse.util.Constants;

import java.time.LocalDate;

/**
 * Input description used by {@code add} before placement. Once placed, the data lives on a {@link Batch}.
 * Constructed exclusively via {@link Builder}.
 */
public class Product {

    private String name;
    private String manufacturer;
    private Unit unit;
    private double quantity;
    private double volumePerUnit = Constants.DEFAULT_VOLUME_PER_UNIT;
    private LocalDate expiryDate;
    private LocalDate entryDate;
    private String comment;

    private Product() {
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Unit getUnit() {
        return unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getVolumePerUnit() {
        return volumePerUnit > 0 ? volumePerUnit : Constants.DEFAULT_VOLUME_PER_UNIT;
    }

    public double getOccupiedVolume() {
        return quantity * getVolumePerUnit();
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public String getComment() {
        return comment;
    }

    public Batch toBatch(Location location) {
        Batch batch = new Batch();
        batch.setProductName(name);
        batch.setManufacturer(manufacturer);
        batch.setUnit(unit);
        batch.setQuantity(quantity);
        batch.setVolumePerUnit(getVolumePerUnit());
        batch.setExpiryDate(expiryDate);
        batch.setEntryDate(entryDate);
        batch.setLocation(location);
        batch.setComment(comment);
        return batch;
    }

    public static class Builder {

        private final Product product = new Product();

        public Builder name(String name) {
            product.name = name;
            return this;
        }

        public Builder manufacturer(String manufacturer) {
            product.manufacturer = manufacturer;
            return this;
        }

        public Builder unit(Unit unit) {
            product.unit = unit;
            return this;
        }

        public Builder quantity(double quantity) {
            product.quantity = quantity;
            return this;
        }

        public Builder volumePerUnit(double volumePerUnit) {
            product.volumePerUnit = volumePerUnit;
            return this;
        }

        public Builder expiryDate(LocalDate expiryDate) {
            product.expiryDate = expiryDate;
            return this;
        }

        public Builder entryDate(LocalDate entryDate) {
            product.entryDate = entryDate;
            return this;
        }

        public Builder comment(String comment) {
            product.comment = comment;
            return this;
        }

        public Product build() {
            if (product.name == null || product.name.isBlank()) {
                throw new IllegalArgumentException("Product name is required");
            }
            if (product.unit == null) {
                throw new IllegalArgumentException("Unit is required");
            }
            if (product.quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (product.expiryDate == null) {
                throw new IllegalArgumentException("Expiry date is required");
            }
            if (product.entryDate == null) {
                throw new IllegalArgumentException("Entry date is required");
            }
            return product;
        }
    }
}
