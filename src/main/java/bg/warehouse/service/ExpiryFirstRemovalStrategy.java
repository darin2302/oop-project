package bg.warehouse.service;

import bg.warehouse.model.Batch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExpiryFirstRemovalStrategy implements RemovalStrategy {

    @Override
    public List<RemovalResult> remove(List<Batch> candidates, String productName, double quantity) {
        List<Batch> sorted = new ArrayList<>(candidates);
        sorted.sort(Comparator.comparing(Batch::getExpiryDate));

        List<RemovalResult> results = new ArrayList<>();
        double remaining = quantity;

        for (Batch batch : sorted) {
            if (remaining <= 0) break;
            double take = Math.min(batch.getQuantity(), remaining);
            batch.setQuantity(batch.getQuantity() - take);
            remaining -= take;
            results.add(new RemovalResult(batch, take));
        }

        return results;
    }
}
