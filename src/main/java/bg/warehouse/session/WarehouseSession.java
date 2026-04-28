package bg.warehouse.session;

import bg.warehouse.exception.NoFileOpenException;
import bg.warehouse.model.Warehouse;

public class WarehouseSession {

    private static WarehouseSession instance;

    private Warehouse warehouse;
    private String filePath;

    private WarehouseSession() {
    }

    public static WarehouseSession getInstance() {
        if (instance == null) {
            instance = new WarehouseSession();
        }
        return instance;
    }

    public boolean isFileOpen() {
        return warehouse != null && filePath != null;
    }

    public void requireOpen() {
        if (!isFileOpen()) {
            throw new NoFileOpenException();
        }
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void clear() {
        this.warehouse = null;
        this.filePath = null;
    }
}
