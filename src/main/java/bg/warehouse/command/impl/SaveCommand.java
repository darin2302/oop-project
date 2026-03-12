package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.xml.XmlFileHandler;

import java.util.Scanner;

public class SaveCommand implements Command {

    private final XmlFileHandler xmlHandler = new XmlFileHandler();

    @Override
    public void execute(String[] args, Scanner scanner) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            System.out.println("No file is currently open. Use 'open <file>' first.");
            return;
        }

        try {
            xmlHandler.save(session.getWarehouse(), session.getFilePath());

            String filePath = session.getFilePath();
            String fileName = filePath.contains("\\")
                    ? filePath.substring(filePath.lastIndexOf('\\') + 1)
                    : filePath.contains("/")
                    ? filePath.substring(filePath.lastIndexOf('/') + 1)
                    : filePath;

            System.out.println("Successfully saved " + fileName);
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
