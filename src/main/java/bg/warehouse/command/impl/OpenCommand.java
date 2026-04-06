package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.model.Warehouse;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.FileUtils;
import bg.warehouse.xml.XmlFileHandler;

public class OpenCommand implements Command {

    private final ConsoleIO io;
    private final XmlFileHandler xmlHandler;

    public OpenCommand(ConsoleIO io, XmlFileHandler xmlHandler) {
        this.io = io;
        this.xmlHandler = xmlHandler;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            io.println("Usage: open <file>");
            return;
        }

        if (WarehouseSession.getInstance().isFileOpen()) {
            io.println("A file is already open. Please close it first.");
            return;
        }

        String filePath = args[1];

        try {
            Warehouse warehouse = xmlHandler.load(filePath);
            WarehouseSession.getInstance().setWarehouse(warehouse);
            WarehouseSession.getInstance().setFilePath(filePath);
            io.println("Successfully opened " + FileUtils.getFileName(filePath));
        } catch (Exception e) {
            io.println("Error opening file: " + e.getMessage());
        }
    }
}
