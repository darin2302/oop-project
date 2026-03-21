package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;
import bg.warehouse.util.FileUtils;

import java.util.Scanner;

public class CloseCommand implements Command {

    @Override
    public void execute(String[] args, Scanner scanner) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            System.out.println(Constants.NO_FILE_OPEN);
            return;
        }

        String fileName = FileUtils.getFileName(session.getFilePath());
        session.clear();
        System.out.println("Successfully closed " + fileName);
    }
}
