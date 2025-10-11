package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.regex.Matcher;
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
        case "rem":
            return parseRem(arguments);
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

    private Command parseRem(String[] arguments) throws StudyMateException { //including rem
        if (arguments.length < 2) {
            throw new StudyMateException("The rem command must be followed by a subcommand.");
        }
        String[] parts = arguments[1].trim().split("\\s+", 2);
        String rest = parts.length > 1 ? parts[1].trim() : "";
        return switch (parts[0]) {
            case "rm" -> parseRemRm(parts);
            case "ls" -> parseRemLs(rest);
            default -> parseRemAdd(arguments[1]);
        };
    }

    private Command parseRemRm(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.REM_RM, indexes);
    }

    private Command parseRemLs(String rest) throws StudyMateException {
        if (!Objects.equals(rest, "")) {
            throw new StudyMateException("Too many arguments for ls command!");
        }
        return new Command(CommandType.REM_LS);
    }

    private Command parseRemAdd(String task) throws StudyMateException {
        if (task == null || task.isBlank()) {
            throw new StudyMateException("Input an event and a DATE/TIME for the reminder!");
        }
        String[] arguments = task.trim().split("\\s+");

        int atIndex = 0;
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].equals("@")) {
                atIndex = i;
                break;
            }
        }
        if (atIndex == 0 || atIndex == arguments.length - 1) {
            throw new StudyMateException("Input an event and a DATE/TIME for the reminder! Use '@' between the event and the DATE/TIME");
        }
        String reminder = String.join(" ", java.util.Arrays.copyOfRange(arguments, 0, atIndex - 1));
        String dateTimeString = String.join(" ", java.util.Arrays.copyOfRange(arguments, atIndex + 1, arguments.length - 1));
        try {
            DateTimeArg dateTimeArg = new DateTimeArg(LocalDate.parse(dateTimeString));
            return new Command(CommandType.REM_ADD, reminder, dateTimeArg);
        } catch (DateTimeParseException e) {
            throw new StudyMateException("Bad deadline syntax! The syntax is yyyy-mm-dd!");
        }
    }
}
