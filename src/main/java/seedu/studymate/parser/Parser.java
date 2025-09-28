package seedu.studymate.parser;

public class Parser {
    String DELIMITER_BY = "/by";

    public Command parse(String line) throws IllegalArgumentException {
        if (line.isEmpty()) {
            throw new IllegalArgumentException("Invalid argument");
        }

        String clean_line = line.replaceAll("\\s+", " ");

        String verb = clean_line.split(" ")[0].toLowerCase();

        switch (verb) {
        case "todo":
            String desc = clean_line.split(" ",2)[1];
            return parseToDo(desc);
        case "deadline":
            String arguments = clean_line.split(" ",2)[1];
            return parseDeadline(arguments);
        case "list":
            return new Command(CommandType.LIST);
        default:
            throw new IllegalArgumentException("Invalid command");
        }
    }

    private Command parseToDo(String desc) throws IllegalArgumentException {
        if (desc.isEmpty()) {
            throw new IllegalArgumentException("The description of a todo cannot be empty.");
        }
        return new Command(CommandType.TODO, desc);
    }

    private Command parseDeadline(String arguments) throws IllegalArgumentException {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("The description of the deadline task cannot be empty!");
        }

        if (!arguments.contains(DELIMITER_BY)) {
            throw new IllegalArgumentException("The deadline task must have a " + DELIMITER_BY + " delimiter!");
        }

        // Split the content into description and deadline part using DelimiterType.DELIMITER_BY
        String[] deadlineParts = arguments.split(DELIMITER_BY, 2);
        String desc = deadlineParts[0].trim();
        String deadline = deadlineParts.length > 1 ? deadlineParts[1].trim() : "";

        if (desc.isEmpty() || deadline.isEmpty()) {
            throw new IllegalArgumentException("The description and deadline of a deadline cannot be empty!");
        }

        // Assuming Command class supports description and deadline:
        return new Command(CommandType.DEADLINE, desc, deadline);
    }

}
