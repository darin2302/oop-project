package bg.warehouse.service;

import bg.warehouse.model.Batch;

import java.util.List;

/**
 * Strategy interface for removing batches by some ordering policy.
 * Implementations decide which batches to drain first.
 */
public interface RemovalStrategy {

    /** Drain {@code quantity} units of {@code productName} from {@code candidates}. */
    List<RemovalResult> remove(List<Batch> candidates, String productName, double quantity);
}
