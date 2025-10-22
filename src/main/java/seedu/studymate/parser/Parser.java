package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class Parser {
    private static final String DELIMITER_BY = "/by";
    private static final String DELIMITER_FROM = "/from";
    private static final String DELIMITER_TO = "/to";

    private static final String SORTED_FLAG = "-s";
    private static final String DESCRIPTION_FLAG = "-n";
    private static final String DEADLINE_FLAG = "-d";
    private static final String FROM_FLAG = "-f";
    private static final String TO_FLAG = "-t";

    private static final Pattern integerPattern = Pattern.compile("\\d");
    private static final Pattern multipleIntegerPattern = Pattern.compile(("\\d\\.\\.\\.\\d"));
    private static final Pattern TIMER_PATTERN =
            Pattern.compile("^\\s*([^@]+)?\\s*(?:@\\s*(\\d+))?\\s*$");

    private static final Logger logger = Logger.getLogger("Parser Logger");

    public Command parse(String line) throws StudyMateException {
        if (line.isEmpty()) {
            throw new StudyMateException("Line cannot be empty");
        }

        String cleanLine = line.replaceAll("\\s+", " ");

        String[] arguments = cleanLine.split(" ", 2);
        String argumentString = arguments.length > 1 ? arguments[1] : "";

        logger.log(Level.INFO, "Command received: " + arguments[0]);
        switch (arguments[0].toLowerCase()) {
        case "todo":
            return parseToDo(argumentString);
        case "deadline":
            return parseDeadline(argumentString);
        case "event":
            return parseEvent(argumentString);
        case "list":
            return parseList(argumentString);
        case "find":
            return parseFind(argumentString);
        case "mark":
            return parseMark(arguments);
        case "unmark":
            return parseUnmark(arguments);
        case "edit":
            return parseEdit(argumentString);
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
        logger.log(Level.INFO, "ToDo description: " + desc);
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
            logger.log(Level.INFO, "Deadline description: " + desc);
            logger.log(Level.INFO, "Deadline's deadline: " + dateTimeArg);
            return new Command(CommandType.DEADLINE, desc, dateTimeArg);
        } catch (DateTimeParseException e) {
            throw new StudyMateException("Bad deadline syntax! The syntax is yyyy-mm-dd!");
        }
    }

    private Command parseEvent(String arguments) throws StudyMateException {
        if (arguments.isEmpty()) {
            throw new StudyMateException("The description of the event task cannot be empty!");
        }

        if (!arguments.contains(DELIMITER_FROM) || !arguments.contains(DELIMITER_TO)) {
            throw new StudyMateException("The event task must have both " + DELIMITER_FROM + " and " + DELIMITER_TO +
                    " delimiters!");
        }

        // Ensure /from comes before /to
        int fromIndex = arguments.indexOf(DELIMITER_FROM);
        int toIndex = arguments.indexOf(DELIMITER_TO);
        if (fromIndex >= toIndex) {
            throw new StudyMateException("The " + DELIMITER_FROM + " delimiter must come before " + DELIMITER_TO + "!");
        }

        // Split the content into description, from and to parts
        String[] firstSplit = arguments.split(Pattern.quote(DELIMITER_FROM), 2);
        String desc = firstSplit[0].trim();

        if (firstSplit.length < 2) {
            throw new StudyMateException("The description, from date and to date of an event cannot be empty!");
        }

        String[] secondSplit = firstSplit[1].split(Pattern.quote(DELIMITER_TO), 2);
        String fromDate = secondSplit[0].trim();
        String toDate = secondSplit.length > 1 ? secondSplit[1].trim() : "";

        if (desc.isEmpty() || fromDate.isEmpty() || toDate.isEmpty()) {
            throw new StudyMateException("The description, from date and to date of an event cannot be empty!");
        }

        try {
            DateTimeArg fromDateTimeArg = new DateTimeArg(LocalDate.parse(fromDate));
            DateTimeArg toDateTimeArg = new DateTimeArg(LocalDate.parse(toDate));
            logger.log(Level.INFO, "Event description: " + desc);
            logger.log(Level.INFO, "Event's from date: " + fromDateTimeArg);
            logger.log(Level.INFO, "Event's to date: " + toDateTimeArg);
            return new Command(CommandType.EVENT, desc, fromDateTimeArg, toDateTimeArg);
        } catch (DateTimeParseException e) {
            throw new StudyMateException("Bad date syntax! The syntax is yyyy-mm-dd!");
        }
    }

    private Command parseFind(String arguments) throws StudyMateException {
        if (arguments.isEmpty()) {
            throw new StudyMateException("The substring cannot be empty!");
        }
        return new Command(arguments, CommandType.FIND);
    }

    private Command parseList(String arguments) throws StudyMateException {
        if (arguments.equals(SORTED_FLAG)) {
            return new Command(CommandType.LIST, true);
        } else if (arguments.isEmpty()) {
            return new Command(CommandType.LIST);
        } else {
            throw new StudyMateException("Invalid flags for list command!");
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

    private Command parseEdit(String arguments) throws StudyMateException {
        String[] editArgs = arguments.split(" ", 3);
        if (editArgs.length < 3) {
            throw new StudyMateException("Invalid syntax! The correct syntax is edit <index> -<flag> <value>\n" +
                    "Note that -flag can be n for name, d for deadline, f for from, t for to");
        }
        try {
            int index = Integer.parseInt(editArgs[0]) - 1;
            switch (editArgs[1]) {
            case DESCRIPTION_FLAG -> {
                return new Command(CommandType.EDIT_DESC, index, editArgs[2]);
            }
            case DEADLINE_FLAG -> {
                DateTimeArg dateTimeArg = new DateTimeArg(LocalDate.parse(editArgs[2]));
                return new Command(CommandType.EDIT_DEADLINE, index, dateTimeArg);
            }
            case FROM_FLAG -> {
                DateTimeArg dateTimeArg = new DateTimeArg(LocalDate.parse(editArgs[2]));
                return new Command(CommandType.EDIT_FROM, index, dateTimeArg);
            }
            case TO_FLAG -> {
                DateTimeArg dateTimeArg = new DateTimeArg(LocalDate.parse(editArgs[2]));
                return new Command(CommandType.EDIT_TO, index, dateTimeArg);
            }
            default -> throw new StudyMateException(
                    "Invalid syntax! The correct syntax is edit <index> -<flag> <value>\n" +
                            "Note that -flag can be n for name, d for deadline, f for from, t for to");
            }
        } catch (NumberFormatException e) {
            throw new StudyMateException("Invalid syntax! The correct syntax is edit <index> -<flag> <value>\n" +
                    "Note that -flag can be n for name, d for deadline, f for from, t for to");
        } catch (DateTimeParseException e) {
            throw new StudyMateException("Invalid syntax! The correct syntax is edit <index> -<flag> <value>\n" +
                    "Note that -flag can be n for name, d for deadline, f for from, t for to");
        }
    }

    private Command parseDelete(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.DELETE, indexes);
    }

    private LinkedHashSet<Integer> parseIndexes(String[] arguments) throws StudyMateException {
        // Check that the task number is not empty
        if (arguments.length <= 1) {
            throw new StudyMateException("The " + arguments[0] +
                    " command must be followed by a task number.");
        }
        try {
            String[] indexArgs = arguments[1].split(",");
            LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
            for (String arg : indexArgs) {
                String trimmedArg = arg.trim().replace(" ", ""); // Trim whitespace to handle "1, 2" inputs
                if (multipleIntegerPattern.matcher(trimmedArg).find()) {
                    String[] rangeParts = trimmedArg.split("\\.\\.\\.");
                    int[] startAndEndArgs = Arrays.stream(rangeParts)
                            .mapToInt(s -> Integer.parseInt(s.trim())) // Trim range tokens as well
                            .toArray();
                    if (startAndEndArgs[0] > startAndEndArgs[1]) {
                        throw new NumberFormatException();
                    }
                    for (int i = startAndEndArgs[0]; i <= startAndEndArgs[1]; i++) {
                        indexes.add(i - 1);
                    }
                } else if (integerPattern.matcher(trimmedArg).find()) {
                    indexes.add(Integer.parseInt(trimmedArg) - 1);
                } else {
                    throw new NumberFormatException();
                }
            }
            assert(!indexes.isEmpty());
            logger.log(Level.INFO, "ArrayList indexes : " + indexes);
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
        logger.log(Level.INFO, "rem command recorded : " + parts[0]);
        return switch (parts[0]) {
        case "rm" -> parseRemRm(parts);
        case "ls" -> parseRemLs(rest);
        case "on" -> parseRemOn(parts);
        case "off" -> parseRemOff(parts);
        case "snooze" -> parseRemSnooze(parts);
        default -> parseRemAdd(arguments[1]);
        };
    }

    private Command parseRemOn(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.REM_ON, indexes);
    }

    private Command parseRemOff(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.REM_OFF, indexes);
    }

    private Command parseRemRm(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.REM_RM, indexes);
    }

    private Command parseRemSnooze(String[] arguments) throws StudyMateException {
        if (arguments.length == 1) {
            throw new StudyMateException("Input index of the Reminder to snooze, followed by the duration!");
        }
        String[] parts = arguments[1].trim().split("\\s+");
        if (parts.length == 1) {
            try {
                Integer.parseInt(parts[0]);
                throw new StudyMateException("Please specify a duration! Usage: rem snooze <index> <duration>");
            } catch (NumberFormatException e) {
                throw new StudyMateException("Please specify which reminder to snooze! Usage: rem <index> snooze <duration>");
            }
        }
        else if (parts.length > 2) {
            throw new StudyMateException("Too many arguments! Usage: rem <index> snooze <duration>");
        }
        else {
            try {
                int snoozeIndex = Integer.parseInt(parts[0]);
                Duration snoozeDuration = parseInterval(parts[1]); //will automatically throw StudyMateException
                return new Command(CommandType.REM_SNOOZE, snoozeIndex, snoozeDuration);
            } catch (NumberFormatException e) {
                throw new StudyMateException("Please specify which reminder to snooze with an integer! Usage: rem <index> snooze <duration>");
            }
        }
    }

    private Command parseRemLs(String rest) throws StudyMateException {
        if (!Objects.equals(rest, "")) {
            throw new StudyMateException("Too many arguments for ls command!");
        }
        return new Command(CommandType.REM_LS);
    }

    private Command parseRemAdd(String rem) throws StudyMateException {
        if (rem == null || rem.isBlank()) {
            throw new StudyMateException("Input an event and a DATE/TIME for the reminder!");
        }
        String[] arguments = rem.trim().split("\\s+");

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

        int rIndex = 0;
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].equals("-r")) {
                rIndex = i;
                break;
            }
        }
        if (rIndex == arguments.length - 1) {
            throw new StudyMateException("Input a recurring duration after the -r flag!");
        }

        String reminder = String.join(" ", java.util.Arrays.copyOfRange(arguments, 0, atIndex));

        if (rIndex == 0) { //non-recurring reminder
            String dateTimeString = String.join(" ", java.util.Arrays.copyOfRange(arguments,
                    atIndex + 1, arguments.length));
            try {
                DateTimeArg dateTimeArg = parseDateTimeString(dateTimeString);
                logger.log(Level.INFO, "Reminder name : " + reminder);
                logger.log(Level.INFO, "Reminder date: " + dateTimeArg);
                return new Command(CommandType.REM_ADD_ONETIME, reminder, dateTimeArg);
            } catch (DateTimeParseException e) {
                throw new StudyMateException("Bad date/time syntax! The syntax is yyyy-mm-dd hh:mm!");
            }

        } else { //recurring reminder
            String dateTimeString = String.join(" ", java.util.Arrays.copyOfRange(arguments,
                    atIndex + 1, rIndex));
            String recurringString = String.join(" ", java.util.Arrays.copyOfRange(arguments,
                    rIndex + 1, arguments.length));
            Duration recurringDuration = parseInterval(recurringString);
            try {
                DateTimeArg dateTimeArg = parseDateTimeString(dateTimeString);
                logger.log(Level.INFO, "Reminder name : " + reminder);
                logger.log(Level.INFO, "Reminder date: " + dateTimeArg);
                return new Command(CommandType.REM_ADD_REC, reminder, dateTimeArg, recurringDuration);
            } catch (DateTimeParseException e) {
                throw new StudyMateException("Bad date/time syntax! The syntax is yyyy-mm-dd hh:mm!");
            }
        }
    }

    private DateTimeArg parseDateTimeString(String dateTimeString) throws DateTimeParseException {
        String[] parts = dateTimeString.trim().split(" ");

        if (parts.length != 2) {
            throw new DateTimeParseException("Time is required! Format: yyyy-mm-dd hh:mm", dateTimeString, 0);
        }

        // Parse date and time (both required)
        LocalDate date = LocalDate.parse(parts[0]);
        LocalTime time = LocalTime.parse(parts[1]);
        return new DateTimeArg(date, time);
    }

    private Duration parseInterval(String input) throws StudyMateException {
        input = input.trim().toLowerCase(); // normalize input, e.g., "1D" -> "1d"

        if (!input.matches("\\d+[smhdw]")) { //matches formatting pattern of number + unit
            throw new StudyMateException("Invalid interval format: " + input);
        }

        // Extract value and unit
        long value = Long.parseLong(input.substring(0, input.length() - 1));
        char unit = input.charAt(input.length() - 1);

        switch (unit) {
        case 'm':
            return Duration.ofMinutes(value);
        case 'h':
            return Duration.ofHours(value);
        case 'd':
            return Duration.ofDays(value);
        case 'w':
            return Duration.ofDays(value * 7);
        default:
            throw new StudyMateException("Unknown duration unit: " + unit);
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
        logger.log(Level.INFO, "Timer duration : " + minutes);
        logger.log(Level.INFO, "Timer label : " + label);
        logger.log(Level.INFO, "Target of timer : " + index);
        return new Command(CommandType.START, index, label, minutes);
    }
}
