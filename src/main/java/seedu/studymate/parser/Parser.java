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
    private static final Pattern TIMER_PATTERN =
            Pattern.compile("^\\s*([^@]+)?\\s*(?:@\\s*(\\d+))?\\s*$");


    public Command parse(String line) throws StudyMateException {
        if (line.isEmpty()) {
            throw new StudyMateException("Line cannot be empty");
        }

        String cleanLine = line.replaceAll("\\s+", " ");

        String[] arguments = cleanLine.split(" ", 2);
        String argumentString = arguments.length > 1 ? arguments[1] : "";


        switch (arguments[0].toLowerCase()) {
        case "todo":
            return parseToDo(argumentString);
        case "deadline":
            return parseDeadline(argumentString);
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
        case "start":
            return parseTimerStart(argumentString);
        case "pause":
            return new Command(CommandType.PAUSE);
        case "resume":
            return new Command(CommandType.RESUME);
        case "reset":
            return new Command(CommandType.RESET);
        case "stat":
            return new Command(CommandType.STAT);
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
            for (String arg : indexArgs) {
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
            throw new StudyMateException("Input an event and a DATE/TIME for the reminder! " +
                    "Use '@' between the event and the DATE/TIME");
        }
        String reminder = String.join(" ", java.util.Arrays.copyOfRange(arguments, 0, atIndex));
        String dateTimeString = String.join(" ", java.util.Arrays.copyOfRange(arguments, atIndex + 1,
                arguments.length));
        try {
            DateTimeArg dateTimeArg = new DateTimeArg(LocalDate.parse(dateTimeString));
            return new Command(CommandType.REM_ADD, reminder, dateTimeArg);
        } catch (DateTimeParseException e) {
            throw new StudyMateException("Bad deadline syntax! The syntax is yyyy-mm-dd!");
        }
    }

    private Command parseTimerStart(String arguments) throws StudyMateException {
        // Handle default case: 'Start' command with no arguments
        if (arguments.trim().isEmpty()) {
            // label = "Focus session", index = null, default duration = 25
            return new Command(CommandType.START, null, "Focus session", 25);
        }

        // Setup variables
        Integer index = null;
        String label = null;
        int minutes = 25; // Default duration is 25 minutes

        // Match arguments
        Matcher matcher = TIMER_PATTERN.matcher(arguments.trim());

        if (!matcher.matches()) {
            throw new StudyMateException("Invalid timer start format. Use: start [INDEX | NAME] [@MINUTES]");
        }

        String targetStr = matcher.group(1);
        String minutesStr = matcher.group(2);

        // Extract and validate minutes
        if (minutesStr != null) {
            try {
                minutes = Integer.parseInt(minutesStr);
                if (minutes <= 0) {
                    throw new StudyMateException("Minutes for the timer must be a positive integer.");
                }
            } catch (NumberFormatException e) {
                throw new StudyMateException("Minutes must be a valid integer.");
            }
        }

        // Validate and extract Target (Index or Label)
        if (targetStr != null) {
            String trimmedTarget = targetStr.trim();
            if (trimmedTarget.isEmpty()) {
                throw new StudyMateException("Timer target cannot be empty.");
            }

            // Check if the target is an Index (contains only digits)
            if (trimmedTarget.matches("\\d+")) {
                try {
                    index = Integer.parseInt(trimmedTarget);
                    if (index <= 0) {
                        throw new StudyMateException("List index must be a positive integer.");
                    }
                    index = index - 1; // Convert to 0-based index
                } catch (NumberFormatException e) {
                    throw new StudyMateException("List index must be a valid integer.");
                }
            } else {
                // Target is treated as a Label
                label = trimmedTarget;
            }
        } else {
            // Target is null (i.e., only duration was specified, like 'start @ 45')
            label = "Focus session";
        }

        // Create and return the Command object
        return new Command(CommandType.START, index, label, minutes);
    }
}
