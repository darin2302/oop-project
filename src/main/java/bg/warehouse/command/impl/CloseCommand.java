package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;
import bg.warehouse.util.FileUtils;

public class CloseCommand implements Command {

    private final ConsoleIO io;

    public CloseCommand(ConsoleIO io) {
        this.io = io;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            io.println(Constants.NO_FILE_OPEN);
            return;
        }

        String fileName = FileUtils.getFileName(session.getFilePath());
        session.clear();
        io.println("Successfully closed " + fileName);
    }
}
