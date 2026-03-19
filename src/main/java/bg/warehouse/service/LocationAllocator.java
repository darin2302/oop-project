package bg.warehouse.service;

import bg.warehouse.model.Batch;
import bg.warehouse.model.Location;
import bg.warehouse.model.Warehouse;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LocationAllocator {

    private static final String[] SECTIONS = {"A", "B", "C", "D", "E"};
    private static final int SHELVES_PER_SECTION = 5;
    private static final int SLOTS_PER_SHELF = 10;

    public Optional<Location> findFreeSlot(Warehouse warehouse) {
        Set<String> occupied = new HashSet<>();
        for (Batch batch : warehouse.getBatches()) {
            if (batch.getLocation() != null) {
                occupied.add(batch.getLocation().toString());
            }
        }

        for (String section : SECTIONS) {
            for (int shelf = 1; shelf <= SHELVES_PER_SECTION; shelf++) {
                for (int slot = 1; slot <= SLOTS_PER_SHELF; slot++) {
                    String slotStr = String.format("%02d", slot);
                    Location loc = new Location(section, shelf, slotStr);
                    if (!occupied.contains(loc.toString())) {
                        return Optional.of(loc);
                    }
                }
            }
        }

        return Optional.empty();
    }
}
