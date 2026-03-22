package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.model.Warehouse;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.FileUtils;
import bg.warehouse.xml.XmlFileHandler;

import java.util.Scanner;

public class OpenCommand implements Command {

    private final XmlFileHandler xmlHandler = new XmlFileHandler();

    @Override
    public void execute(String[] args, Scanner scanner) {
        if (args.length < 2) {
            System.out.println("Usage: open <file>");
            return;
        }

        if (WarehouseSession.getInstance().isFileOpen()) {
            System.out.println("A file is already open. Please close it first.");
            return;
        }

        String filePath = args[1];

        try {
            Warehouse warehouse = xmlHandler.load(filePath);
            WarehouseSession.getInstance().setWarehouse(warehouse);
            WarehouseSession.getInstance().setFilePath(filePath);
            System.out.println("Successfully opened " + FileUtils.getFileName(filePath));
        } catch (Exception e) {
            System.out.println("Error opening file: " + e.getMessage());
        }
    }
}
