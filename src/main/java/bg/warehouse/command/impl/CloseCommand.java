package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.session.WarehouseSession;

import java.util.Scanner;

public class CloseCommand implements Command {

    @Override
    public void execute(String[] args, Scanner scanner) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            System.out.println("No file is currently open. Use 'open <file>' first.");
            return;
        }

        String filePath = session.getFilePath();
        String fileName = filePath.contains("\\")
                ? filePath.substring(filePath.lastIndexOf('\\') + 1)
                : filePath.contains("/")
                ? filePath.substring(filePath.lastIndexOf('/') + 1)
                : filePath;

        session.clear();
        System.out.println("Successfully closed " + fileName);
    }
}
