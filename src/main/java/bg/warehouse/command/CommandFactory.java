package bg.warehouse.command;

import bg.warehouse.command.impl.*;
import bg.warehouse.io.ConsoleIO;
import bg.warehouse.service.LocationAllocator;
import bg.warehouse.xml.XmlFileHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandFactory {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandFactory(ConsoleIO io) {
        XmlFileHandler xmlHandler = new XmlFileHandler();
        LocationAllocator locationAllocator = new LocationAllocator();

        commands.put("open", new OpenCommand(io, xmlHandler));
        commands.put("close", new CloseCommand(io));
        commands.put("save", new SaveCommand(io, xmlHandler));
        commands.put("save as", new SaveAsCommand(io, xmlHandler));
        commands.put("print", new PrintCommand(io));
        commands.put("add", new AddCommand(io, locationAllocator));
        commands.put("remove", new RemoveCommand(io));
        commands.put("log", new LogCommand(io));
        commands.put("clean", new CleanCommand(io));
        commands.put("help", new HelpCommand(io));
        commands.put("exit", new ExitCommand(io));
    }

    public Command getCommand(String commandName) {
        return commands.get(commandName.toLowerCase());
    }

    public void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(), command);
    }

    public static String[] tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens.toArray(new String[0]);
    }
}
