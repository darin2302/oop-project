package bg.warehouse.command.impl;

import bg.warehouse.command.Command;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.session.WarehouseSession;
import bg.warehouse.util.Constants;
import bg.warehouse.util.FileUtils;
import bg.warehouse.xml.XmlFileHandler;

public class SaveCommand implements Command {

    private final ConsoleIO io;
    private final XmlFileHandler xmlHandler;

    public SaveCommand(ConsoleIO io, XmlFileHandler xmlHandler) {
        this.io = io;
        this.xmlHandler = xmlHandler;
    }

    @Override
    public void execute(String[] args) {
        WarehouseSession session = WarehouseSession.getInstance();

        if (!session.isFileOpen()) {
            io.println(Constants.NO_FILE_OPEN);
            return;
        }

        try {
            xmlHandler.save(session.getWarehouse(), session.getFilePath());
            io.println("Successfully saved " + FileUtils.getFileName(session.getFilePath()));
        } catch (Exception e) {
            io.println("Error saving file: " + e.getMessage());
        }
    }
}
