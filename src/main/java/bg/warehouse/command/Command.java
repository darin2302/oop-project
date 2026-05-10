package bg.warehouse.command;

/**
 * Command in the Command pattern. Each CLI command implements this interface.
 */
public interface Command {

    /** Execute the command with the given tokenized arguments. */
    void execute(String[] args);
}
