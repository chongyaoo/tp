package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

public class Parser {
    private static final String DELIMITER_BY = "/by";
    private static final Pattern integerPattern = Pattern.compile("\\d");
    private static final Pattern multipleIntegerPattern = Pattern.compile(("\\d\\.\\.\\.\\d"));

    public Command parse(String line) throws StudyMateException {
        if (line.isEmpty()) {
            throw new StudyMateException("Line cannot be empty");
        }

        String cleanLine = line.replaceAll("\\s+", " ");

        String[] arguments = cleanLine.split(" ",2);

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

    private Command parseToDo(String desc) throws StudyMateException {
        if (desc.isEmpty()) {
            throw new StudyMateException("The description of a todo cannot be empty.");
        }
        return new Command(CommandType.TODO, desc);
    }

    private Command parseDeadline(String arguments) throws StudyMateException {
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

        try {
            DateTimeArg dateTimeArg = new DateTimeArg(LocalDate.parse(deadline));
            return new Command(CommandType.DEADLINE, desc, dateTimeArg);
        } catch (DateTimeParseException e) {
            throw new StudyMateException("Bad deadline syntax! The syntax is yyyy-mm-dd!");
        }
    }

    private Command parseMark(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.MARK, indexes);
    }

    private Command parseUnmark(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.UNMARK, indexes);
    }

    private Command parseDelete(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.DELETE, indexes);
    }

    private LinkedHashSet<Integer> parseIndexes(String[] arguments) throws StudyMateException {
        // Check that the task number is not empty
        if (arguments[1].isEmpty()) {
            throw new StudyMateException("The " + arguments[0] + " command must be followed by a task number.");
        }
        try {
            String[] indexArgs = arguments[1].split(",");
            LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
            for (String arg: indexArgs) {
                if (multipleIntegerPattern.matcher(arg).find()) {
                    int[] startAndEndArgs = Arrays.stream(arg.split("\\.\\.\\.")).mapToInt(Integer::parseInt).
                            toArray();
                    if (startAndEndArgs[0] > startAndEndArgs[1]) {
                        throw new NumberFormatException();
                    }
                    for (int i = startAndEndArgs[0]; i <= startAndEndArgs[1]; i++) {
                        indexes.add(i - 1);
                    }
                } else if (integerPattern.matcher(arg).find()) {
                    indexes.add(Integer.parseInt(arg) - 1);
                } else {
                    throw new NumberFormatException();
                }
            }
            return indexes;
        } catch (NumberFormatException e) {
            throw new StudyMateException("The " + arguments[0] + " command must be followed by a valid input");
        }
    }

}
