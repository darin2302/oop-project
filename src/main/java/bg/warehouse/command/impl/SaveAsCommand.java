package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.xml.XmlFileHandler;

import java.util.Scanner;

public class SaveAsCommand implements Command {

    private final XmlFileHandler xmlHandler = new XmlFileHandler();

    @Override
    public void execute(String[] args, Scanner scanner) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            System.out.println("No file is currently open. Use 'open <file>' first.");
            return;
        }

        if (args.length < 3) {
            System.out.println("Usage: save as <file>");
            return;
        }

        String newPath = args[2];

        try {
            xmlHandler.save(session.getWarehouse(), newPath);
            session.setFilePath(newPath);

            String fileName = newPath.contains("\\")
                    ? newPath.substring(newPath.lastIndexOf('\\') + 1)
                    : newPath.contains("/")
                    ? newPath.substring(newPath.lastIndexOf('/') + 1)
                    : newPath;

            System.out.println("Successfully saved " + fileName);
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
