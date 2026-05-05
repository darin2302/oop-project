package bg.warehouse.service;

import bg.warehouse.model.Batch;

public record RemovalResult(Batch batch, double amountTaken) {
}
