package bg.warehouse.service;

import bg.warehouse.model.Batch;

import java.util.List;

public interface RemovalStrategy {
    List<RemovalResult> remove(List<Batch> candidates, String productName, double quantity);
}
