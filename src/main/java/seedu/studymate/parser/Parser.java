package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;

public class Parser {
    static String DELIMITER_BY = "/by";

    public static Command parse(String line) throws StudyMateException {
        if (line.isEmpty()) {
            throw new StudyMateException("Line cannot be empty");
        }

        String clean_line = line.replaceAll("\\s+", " ");

        String[] arguments = clean_line.split(" ",2);

        switch (arguments[0].toLowerCase()) {
        case "todo":
            return parseToDo(arguments[1]);
        case "deadline":
            return parseDeadline(arguments[1]);
        case "list":
            return new Command(CommandType.LIST);
        case "mark":
            return parseMark(arguments);
        case "unmark":
            return parseUnmark(arguments);
        case "delete":
            return parseDelete(arguments);
        case "bye":
            return new Command(CommandType.BYE);
        default:
            throw new StudyMateException("Unknown command");
        }
    }

    private static Command parseToDo(String desc) throws StudyMateException {
        if (desc.isEmpty()) {
            throw new StudyMateException("The description of a todo cannot be empty.");
        }
        return new Command(CommandType.TODO, desc);
    }

    private static Command parseDeadline(String arguments) throws StudyMateException {
        if (arguments.isEmpty()) {
            throw new StudyMateException("The description of the deadline task cannot be empty!");
        }

        if (!arguments.contains(DELIMITER_BY)) {
            throw new StudyMateException("The deadline task must have a " + DELIMITER_BY + " delimiter!");
        }

        // Split the content into description and deadline part using DelimiterType.DELIMITER_BY
        String[] deadlineParts = arguments.split(DELIMITER_BY, 2);
        String desc = deadlineParts[0].trim();
        String deadline = deadlineParts.length > 1 ? deadlineParts[1].trim() : "";

        if (desc.isEmpty() || deadline.isEmpty()) {
            throw new StudyMateException("The description and deadline of a deadline cannot be empty!");
        }

        // Assuming Command class supports description and deadline:
        return new Command(CommandType.DEADLINE, desc, deadline);
    }

    private static Command parseMark(String[] arguments) throws StudyMateException {
        validateNumberedCommand(arguments);
        return new Command(CommandType.MARK, arguments[1]);
    }

    private static Command parseUnmark(String[] arguments) throws StudyMateException {
        validateNumberedCommand(arguments);
        return new Command(CommandType.UNMARK, arguments[1]);
    }

    private static Command parseDelete(String[] arguments) throws StudyMateException {
        validateNumberedCommand(arguments);
        return new Command(CommandType.DELETE, arguments[1]);
    }

    private static void validateNumberedCommand(String[] arguments) throws StudyMateException {
        // Check that the task number is not empty
        if (arguments[1].isEmpty()) {
            throw new StudyMateException("The " + arguments[0] + " command must be followed by a task number.");
        }
        try {
            Integer.parseInt(arguments[1]);
        } catch (NumberFormatException e) {
            // Check that string is a valid integer
            throw new StudyMateException("The " + arguments[0] + " command must be followed by a valid number!");
        }
    }

}
