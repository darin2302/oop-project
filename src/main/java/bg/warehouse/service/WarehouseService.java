package bg.warehouse.service;

import bg.warehouse.model.Batch;
import bg.warehouse.model.LogEntry;
import bg.warehouse.model.Location;
import bg.warehouse.model.Product;
import bg.warehouse.model.Warehouse;
import bg.warehouse.observer.WarehouseEventListener;
import bg.warehouse.session.WarehouseSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WarehouseService {

    private final WarehouseSession session;
    private final LocationAllocator allocator;
    private final RemovalStrategy removalStrategy;

    public WarehouseService(WarehouseSession session, LocationAllocator allocator, RemovalStrategy removalStrategy) {
        this.session = session;
        this.allocator = allocator;
        this.removalStrategy = removalStrategy;
    }

    private final List<WarehouseEventListener> listeners = new ArrayList<>();

    public void addListener(WarehouseEventListener l) {
        listeners.add(l);
    }

    public void removeListener(WarehouseEventListener l) {
        listeners.remove(l);
    }

    private void fireAdded(String name, double qty, Location loc) {
        for (WarehouseEventListener l : listeners) {
            l.onProductAdded(name, qty, loc);
        }
    }

    private void fireRemoved(String name, double qty, Location loc) {
        for (WarehouseEventListener l : listeners) {
            l.onProductRemoved(name, qty, loc);
        }
    }

    private Warehouse warehouse() {
        return session.getWarehouse();
    }

    public Optional<Batch> findBatchByNameAndExpiry(String name, LocalDate expiry) {
        return warehouse().getBatches().stream()
                .filter(b -> b.getProductName().equalsIgnoreCase(name)
                        && b.getExpiryDate().equals(expiry))
                .findFirst();
    }

    public Optional<Location> findFreeSlot() {
        return allocator.findFreeSlot(warehouse());
    }

    public Batch addBatch(Product product, Location location) {
        Batch batch = product.toBatch(location);
        warehouse().getBatches().add(batch);
        fireAdded(product.getName(), product.getQuantity(), location);
        return batch;
    }

    public void mergeIntoBatch(Batch existing, double quantity) {
        existing.setQuantity(existing.getQuantity() + quantity);
        fireAdded(existing.getProductName(), quantity, existing.getLocation());
    }

    public List<Batch> findBatchesByName(String name) {
        return warehouse().getBatches().stream()
                .filter(b -> b.getProductName().equalsIgnoreCase(name))
                .sorted(Comparator.comparing(Batch::getExpiryDate))
                .collect(Collectors.toList());
    }

    public double totalQuantity(List<Batch> batches) {
        return batches.stream().mapToDouble(Batch::getQuantity).sum();
    }

    public List<RemovalResult> drain(List<Batch> sortedBatches, String name, double quantity) {
        List<RemovalResult> results = removalStrategy.remove(sortedBatches, name, quantity);

        List<Batch> emptied = new ArrayList<>();
        for (RemovalResult r : results) {
            fireRemoved(name, r.amountTaken(), r.batch().getLocation());
            if (r.batch().getQuantity() <= 0) {
                emptied.add(r.batch());
            }
        }

        warehouse().getBatches().removeAll(emptied);
        return results;
    }

    public List<Batch> findExpiringBy(LocalDate threshold) {
        return warehouse().getBatches().stream()
                .filter(b -> !b.getExpiryDate().isAfter(threshold))
                .collect(Collectors.toList());
    }

    public void removeAndLog(List<Batch> batches) {
        for (Batch batch : batches) {
            fireRemoved(batch.getProductName(), batch.getQuantity(), batch.getLocation());
        }
        warehouse().getBatches().removeAll(batches);
    }

    public List<Batch> getAllBatches() {
        return warehouse().getBatches();
    }

    public java.util.Map<String, Double> totalsByProductName() {
        java.util.Map<String, Double> totals = new java.util.LinkedHashMap<>();
        for (Batch b : warehouse().getBatches()) {
            totals.merge(b.getProductName(), b.getQuantity(), Double::sum);
        }
        return totals;
    }

    public List<LogEntry> queryLog(LocalDate from, LocalDate to) {
        return warehouse().getLogEntries().stream()
                .filter(e -> {
                    LocalDate d = e.getTimestamp().toLocalDate();
                    return !d.isBefore(from) && !d.isAfter(to);
                })
                .collect(Collectors.toList());
    }
}
