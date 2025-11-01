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

/**
 * Parses user input and converts it into Command objects.
 * This class handles all parsing logic for various command types including tasks,
 * reminders, timers, and habits.
 */
public class Parser {
    private static final String DELIMITER_BY = "/by";
    private static final String DELIMITER_FROM = "/from";
    private static final String DELIMITER_TO = "/to";

    private static final String SORTED_FLAG = "-s";
    private static final String DESCRIPTION_FLAG = "-n";
    private static final String DEADLINE_FLAG = "-d";
    private static final String FROM_FLAG = "-f";
    private static final String TO_FLAG = "-t";
    private static final int maxValue = 10000;

    private static final Pattern integerPattern = Pattern.compile("\\d");
    private static final Pattern multipleIntegerPattern = Pattern.compile(("\\d\\.\\.\\.\\d"));
    private static final Pattern TIMER_PATTERN =
            Pattern.compile("^\\s*([^@]+)?\\s*(?:@\\s*(\\d+))?\\s*$");

    private static final Logger logger = Logger.getLogger("Parser Logger");

    /**
     * Parses a line of user input and returns the corresponding Command object.
     *
     * @param line The user input string to parse
     * @return A Command object representing the parsed input
     * @throws StudyMateException If the input is invalid or cannot be parsed
     */
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
        case "habit":
            return parseHabit(arguments);
        default:
            throw new StudyMateException("Unknown command");
        }
    }

    /**
     * Parses a to-do command.
     *
     * @param desc The description of the to-do task
     * @return A Command object for creating a to-do
     * @throws StudyMateException If the description is empty
     */
    private Command parseToDo(String desc) throws StudyMateException {
        if (desc.isEmpty()) {
            throw new StudyMateException("The description of a todo cannot be empty.");
        }
        logger.log(Level.INFO, "ToDo description: " + desc);
        return new Command(CommandType.TODO, desc);
    }

    /**
     * Parses a deadline command.
     *
     * @param arguments The arguments containing description and deadline
     * @return A Command object for creating a deadline
     * @throws StudyMateException If the arguments are invalid or missing required delimiters
     */
    private Command parseDeadline(String arguments) throws StudyMateException {
        if (arguments.isEmpty()) {
            throw new StudyMateException("The description of the deadline task cannot be empty!");
        }

        if (!arguments.toLowerCase().contains(DELIMITER_BY)) {
            throw new StudyMateException("The deadline task must have a " + DELIMITER_BY + " delimiter!");
        }

        // Split the content into description and deadline part using DelimiterType.DELIMITER_BY (case-insensitive)
        String[] deadlineParts = arguments.split("(?i)" + Pattern.quote(DELIMITER_BY), 2);
        String desc = deadlineParts[0].trim();
        String deadline = deadlineParts.length > 1 ? deadlineParts[1].trim() : "";

        if (desc.isEmpty() || deadline.isEmpty()) {
            throw new StudyMateException("The description and deadline of a deadline cannot be empty!");
        }

        try {
            DateTimeArg dateTimeArg = parseDateTimeString(deadline);
            logger.log(Level.INFO, "Deadline description: " + desc);
            logger.log(Level.INFO, "Deadline's deadline: " + dateTimeArg);
            return new Command(CommandType.DEADLINE, desc, dateTimeArg);
        } catch (DateTimeParseException e) {
            throw new StudyMateException("Bad datetime syntax! The syntax is YYYY-MM-DD HH:mm!");
        }
    }

    /**
     * Parses an event command.
     *
     * @param arguments The arguments containing description, from datetime, and to datetime
     * @return A Command object for creating an event
     * @throws StudyMateException If the arguments are invalid or missing required delimiters
     */
    private Command parseEvent(String arguments) throws StudyMateException {
        if (arguments.isEmpty()) {
            throw new StudyMateException("The description of the event task cannot be empty!");
        }

        String lowerArgs = arguments.toLowerCase();
        if (!lowerArgs.contains(DELIMITER_FROM) || !lowerArgs.contains(DELIMITER_TO)) {
            throw new StudyMateException("The event task must have both " + DELIMITER_FROM + " and " + DELIMITER_TO +
                    " delimiters!");
        }

        // Ensure /from comes before /to (case-insensitive check)
        int fromIndex = lowerArgs.indexOf(DELIMITER_FROM);
        int toIndex = lowerArgs.indexOf(DELIMITER_TO);
        if (fromIndex >= toIndex) {
            throw new StudyMateException("The " + DELIMITER_FROM + " delimiter must come before " + DELIMITER_TO + "!");
        }

        // Split the content into description, from and to parts (case-insensitive)
        String[] firstSplit = arguments.split("(?i)" + Pattern.quote(DELIMITER_FROM), 2);
        String desc = firstSplit[0].trim();

        if (firstSplit.length < 2) {
            throw new StudyMateException("The description, from date and to date of an event cannot be empty!");
        }

        String[] secondSplit = firstSplit[1].split("(?i)" + Pattern.quote(DELIMITER_TO), 2);
        String fromDateTime = secondSplit[0].trim();
        String toDateTime = secondSplit.length > 1 ? secondSplit[1].trim() : "";

        if (desc.isEmpty() || fromDateTime.isEmpty() || toDateTime.isEmpty()) {
            throw new StudyMateException("The description, from date and to date of an event cannot be empty!");
        }

        try {
            DateTimeArg fromDateTimeArg = parseDateTimeString(fromDateTime);
            DateTimeArg toDateTimeArg = parseDateTimeString(toDateTime);
            logger.log(Level.INFO, "Event description: " + desc);
            logger.log(Level.INFO, "Event's from date: " + fromDateTimeArg);
            logger.log(Level.INFO, "Event's to date: " + toDateTimeArg);
            return new Command(CommandType.EVENT, desc, fromDateTimeArg, toDateTimeArg);
        } catch (DateTimeParseException e) {
            throw new StudyMateException("Bad datetime syntax! The syntax is YYYY-MM-DD HH:mm");
        }
    }

    /**
     * Parses a find command.
     *
     * @param arguments The substring to search for
     * @return A Command object for finding tasks
     * @throws StudyMateException If the substring is empty
     */
    private Command parseFind(String arguments) throws StudyMateException {
        if (arguments.isEmpty()) {
            throw new StudyMateException("The substring cannot be empty!");
        }
        return new Command(arguments, CommandType.FIND);
    }

    /**
     * Parses a list command.
     *
     * @param arguments The flags for the list command (e.g., -s for sorted)
     * @return A Command object for listing tasks
     * @throws StudyMateException If invalid flags are provided
     */
    private Command parseList(String arguments) throws StudyMateException {
        if (arguments.equalsIgnoreCase(SORTED_FLAG)) {
            return new Command(CommandType.LIST, true);
        } else if (arguments.isEmpty()) {
            return new Command(CommandType.LIST);
        } else {
            throw new StudyMateException("Invalid flags for list command!");
        }
    }

    /**
     * Parses a mark command.
     *
     * @param arguments The arguments containing task indexes to mark
     * @return A Command object for marking tasks
     * @throws StudyMateException If the indexes are invalid
     */
    private Command parseMark(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.MARK, indexes);
    }

    /**
     * Parses an unmark command.
     *
     * @param arguments The arguments containing task indexes to unmark
     * @return A Command object for unmarking tasks
     * @throws StudyMateException If the indexes are invalid
     */
    private Command parseUnmark(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.UNMARK, indexes);
    }

    /**
     * Parses an edit command.
     *
     * @param arguments The arguments containing index, flag, and new value
     * @return A Command object for editing a task
     * @throws StudyMateException If the syntax is invalid
     */
    private Command parseEdit(String arguments) throws StudyMateException {
        String[] editArgs = arguments.split(" ", 3);
        if (editArgs.length < 3) {
            throw new StudyMateException("Invalid syntax! The correct syntax is edit <index> -<flag> <value>\n" +
                    "Note that -flag can be n for name, d for deadline, f for from, t for to");
        }
        try {
            int index = Integer.parseInt(editArgs[0]) - 1;
            switch (editArgs[1].toLowerCase()) {
            case DESCRIPTION_FLAG -> {
                return new Command(CommandType.EDIT_DESC, index, editArgs[2]);
            }
            case DEADLINE_FLAG -> {
                DateTimeArg dateTimeArg = parseDateTimeString(editArgs[2]);
                return new Command(CommandType.EDIT_DEADLINE, index, dateTimeArg);
            }
            case FROM_FLAG -> {
                DateTimeArg dateTimeArg = parseDateTimeString(editArgs[2]);
                return new Command(CommandType.EDIT_FROM, index, dateTimeArg);
            }
            case TO_FLAG -> {
                DateTimeArg dateTimeArg = parseDateTimeString(editArgs[2]);
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
            throw new StudyMateException("Bad datetime syntax! The syntax is YYYY-MM-DD HH:mm!");
        }
    }

    /**
     * Parses a delete command.
     *
     * @param arguments The arguments containing task indexes to delete
     * @return A Command object for deleting tasks
     * @throws StudyMateException If the indexes are invalid
     */
    private Command parseDelete(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.DELETE, indexes);
    }

    /**
     * Parses indexes from command arguments, supporting single indexes, range, and comma-separated mixes of both
     * notations (e.g., "6,1...5").
     *
     * @param arguments The arguments containing the indexes
     * @return A LinkedHashSet of parsed indexes (0-based)
     * @throws StudyMateException If the index format is invalid
     */
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
                if (multipleIntegerPattern.matcher(arg).find()) {
                    String[] rangeParts = arg.split("\\.\\.\\.");
                    // lambda chain returns [startArg, endArg]
                    int[] startAndEndArgs = Arrays.stream(rangeParts)
                            .mapToInt(s -> Integer.parseInt(s.trim())) // Trim range tokens as well
                            .toArray();
                    if (startAndEndArgs[0] > startAndEndArgs[1]) {
                        throw new NumberFormatException();
                    }
                    for (int i = startAndEndArgs[0]; i <= startAndEndArgs[1]; i++) {
                        capNumbers(i);
                        indexes.add(i - 1);
                    }
                } else if (integerPattern.matcher(arg).find()) {
                    int index = Integer.parseInt(arg);
                    capNumbers(index);
                    indexes.add(index - 1);
                } else {
                    throw new NumberFormatException();
                }
            }
            assert (!indexes.isEmpty());
            logger.log(Level.INFO, "ArrayList indexes : " + indexes);
            return indexes;
        } catch (NumberFormatException e) {
            throw new StudyMateException("The " + arguments[0] + " command must be followed by a valid input");
        }
    }

    /**
     * Parses a reminder command and routes to the appropriate subcommand handler.
     *
     * @param arguments The arguments for the reminder command
     * @return A Command object for the reminder operation
     * @throws StudyMateException If the arguments are invalid
     */
    private Command parseRem(String[] arguments) throws StudyMateException { //including rem
        if (arguments.length < 2) {
            throw new StudyMateException("The rem command must be followed by a subcommand.");
        }
        String[] parts = arguments[1].trim().split("\\s+", 2);
        String rest = parts.length > 1 ? parts[1].trim() : "";
        logger.log(Level.INFO, "rem command recorded : " + parts[0]);
        return switch (parts[0].toLowerCase()) {
        case "rm" -> parseRemRm(parts);
        case "ls" -> parseRemLs(rest);
        case "on" -> parseRemOn(parts);
        case "off" -> parseRemOff(parts);
        case "snooze" -> parseRemSnooze(parts);
        default -> parseRemAdd(arguments[1]);
        };
    }

    /**
     * Parses a reminder on command.
     *
     * @param arguments The arguments containing reminder indexes to turn on
     * @return A Command object for turning on reminders
     * @throws StudyMateException If the indexes are invalid
     */
    private Command parseRemOn(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.REM_ON, indexes);
    }

    /**
     * Parses a reminder off command.
     *
     * @param arguments The arguments containing reminder indexes to turn off
     * @return A Command object for turning off reminders
     * @throws StudyMateException If the indexes are invalid
     */
    private Command parseRemOff(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.REM_OFF, indexes);
    }

    /**
     * Parses a reminder remove command.
     *
     * @param arguments The arguments containing reminder indexes to remove
     * @return A Command object for removing reminders
     * @throws StudyMateException If the indexes are invalid
     */
    private Command parseRemRm(String[] arguments) throws StudyMateException {
        LinkedHashSet<Integer> indexes = parseIndexes(arguments);
        return new Command(CommandType.REM_RM, indexes);
    }

    /**
     * Parses a reminder snooze command.
     *
     * @param arguments The arguments containing reminder index and snooze duration
     * @return A Command object for snoozing a reminder
     * @throws StudyMateException If the arguments are invalid
     */
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
                throw new StudyMateException("Please specify which reminder to snooze! " +
                        "Usage: rem <index> snooze <duration>");
            }
        } else if (parts.length > 2) {
            throw new StudyMateException("Too many arguments! Usage: rem <index> snooze <duration>");
        } else {
            try {
                int snoozeIndex = Integer.parseInt(parts[0]) - 1;
                Duration snoozeDuration = parseInterval(parts[1]); //will automatically throw StudyMateException
                return new Command(CommandType.REM_SNOOZE, snoozeIndex, snoozeDuration);
            } catch (NumberFormatException e) {
                throw new StudyMateException("Please specify which reminder to snooze with an integer! " +
                        "Usage: rem <index> snooze <duration>");
            }
        }
    }

    /**
     * Parses a reminder list command.
     *
     * @param rest The remaining arguments (should be empty)
     * @return A Command object for listing reminders
     * @throws StudyMateException If there are extra arguments
     */
    private Command parseRemLs(String rest) throws StudyMateException {
        if (!Objects.equals(rest, "")) {
            throw new StudyMateException("Too many arguments for ls command!");
        }
        return new Command(CommandType.REM_LS);
    }

    /**
     * Parses a reminder add command, supporting both one-time and recurring reminders.
     *
     * @param rem The reminder arguments including name, datetime, and optional recurrence
     * @return A Command object for adding a reminder
     * @throws StudyMateException If the arguments are invalid or missing required components
     */
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
            if (arguments[i].equalsIgnoreCase("-r")) {
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
                throw new StudyMateException("Bad date/time syntax! The syntax is YYYY-MM-DD hh:mm!");
            }

        } else { //recurring reminder
            try {
                String dateTimeString = String.join(" ", java.util.Arrays.copyOfRange(arguments,
                        atIndex + 1, rIndex));
                String recurringString = String.join(" ", java.util.Arrays.copyOfRange(arguments,
                        rIndex + 1, arguments.length));
                Duration recurringDuration = parseInterval(recurringString);
                DateTimeArg dateTimeArg = parseDateTimeString(dateTimeString);
                logger.log(Level.INFO, "Reminder name : " + reminder);
                logger.log(Level.INFO, "Reminder date: " + dateTimeArg);
                return new Command(CommandType.REM_ADD_REC, reminder, dateTimeArg, recurringDuration);
            } catch (DateTimeParseException e) {
                throw new StudyMateException("Bad date/time syntax! The syntax is YYYY-MM-DD hh:mm!");
            } catch (NumberFormatException e) {
                throw new StudyMateException("Input value is too long!");
            }
        }
    }

    /**
     * Parses a date-time string in the format "YYYY-MM-DD HH:mm".
     *
     * @param dateTimeString The date-time string to parse
     * @return A DateTimeArg object representing the parsed date and time
     * @throws DateTimeParseException If the format is invalid
     */
    private DateTimeArg parseDateTimeString(String dateTimeString) throws DateTimeParseException {
        String[] parts = dateTimeString.trim().split(" ");

        if (parts.length != 2) {
            throw new DateTimeParseException("Time is required! Format: YYYY-MM-DD hh:mm", dateTimeString, 0);
        }

        // Parse date and time (both required)
        LocalDate date = LocalDate.parse(parts[0]);
        LocalTime time = LocalTime.parse(parts[1]);
        return new DateTimeArg(date, time);
    }

    /**
     * Parses an interval string in the format of a number followed by a unit (m/h/d/w).
     * Supported units: m (minutes), h (hours), d (days), w (weeks).
     *
     * @param input The interval string to parse (e.g., "5m", "2h", "1d", "1w")
     * @return A Duration object representing the parsed interval
     * @throws StudyMateException If the format is invalid or unit is unknown
     */
    private Duration parseInterval(String input) throws StudyMateException {
        input = input.trim().toLowerCase(); // normalize input, e.g., "1D" -> "1d"

        if (!input.matches("\\d+[smhdw]")) { //matches formatting pattern of number + unit
            throw new StudyMateException("Invalid interval format: " + input);
        }

        // Extract value and unit
        try {
            int value = Integer.parseInt(input.substring(0, input.length() - 1));
            capNumbers(value);
            long durationValue = (long) value;
            if (value <= 0) {
                throw new StudyMateException("Invalid duration provided! It must be greater than 0");
            }
            char unit = input.charAt(input.length() - 1);
            switch (unit) {
            case 'm':
                return Duration.ofMinutes(durationValue);
            case 'h':
                return Duration.ofHours(durationValue);
            case 'd':
                return Duration.ofDays(durationValue);
            case 'w':
                return Duration.ofDays(durationValue * 7);
            default:
                throw new StudyMateException("Unknown duration unit: " + unit);
            }
        } catch (ArithmeticException e) {
            throw new StudyMateException("Invalid duration provided");
        } catch (NumberFormatException e) {
            throw new StudyMateException("Input value is too long!");
        }
    }

    /**
     * Parses a timer start command with optional task index or label and duration.
     *
     * @param arguments The arguments containing optional index/label and duration
     * @return A Command object for starting a timer
     * @throws StudyMateException If the format is invalid or values are out of range
     */
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
                capNumbers(minutes);
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

    /**
     * Parses a habit command and routes to the appropriate subcommand handler.
     *
     * @param arguments The arguments for the habit command
     * @return A Command object for the habit operation
     * @throws StudyMateException If the arguments are invalid
     */
    private Command parseHabit(String[] arguments) throws StudyMateException { //including rem
        if (arguments.length < 2) {
            throw new StudyMateException("The habit command must be followed by a subcommand.");
        }
        String[] parts = arguments[1].trim().split("\\s+", 2);
        String rest = parts.length > 1 ? parts[1].trim() : "";
        logger.log(Level.INFO, "Habit command recorded : " + parts[0]);
        return switch (parts[0].toLowerCase()) {
        case "rm" -> parseHabitRm(rest);
        case "ls" -> new Command(CommandType.HABIT_LIST);
        case "streak" -> parseHabitStreak(rest);
        default -> parseHabitAdd(arguments[1]);
        };
    }

    /**
     * Parses a habit add command with name and interval.
     *
     * @param habit The habit arguments containing name and interval
     * @return A Command object for adding a habit
     * @throws StudyMateException If the arguments are invalid or missing the interval flag
     */
    private Command parseHabitAdd(String habit) throws StudyMateException {
        String[] arguments = habit.trim().split("\\s+");
        // Convert arguments to lowercase for case-insensitive flag matching
        String[] lowerArgs = Arrays.stream(arguments).map(String::toLowerCase).toArray(String[]::new);
        int tIndex = Arrays.asList(lowerArgs).indexOf("-t");
        if (habit.isBlank() || tIndex == 0) {
            throw new StudyMateException("Input a habit!");
        } else if (tIndex == arguments.length - 1 || tIndex == -1) {
            throw new StudyMateException("Input a recurring duration after the -t flag!");
        }
        Duration interval = parseInterval(arguments[tIndex + 1]);
        String habitName = String.join(" ", java.util.Arrays.copyOfRange(arguments, 0, tIndex));
        return new Command(CommandType.HABIT_ADD, habitName, interval);
    }

    /**
     * Parses a habit streak command.
     *
     * @param arguments The habit index to increment streak
     * @return A Command object for incrementing a habit's streak
     * @throws StudyMateException If the index is invalid
     */
    private Command parseHabitStreak(String arguments) throws StudyMateException {
        try {
            int index = Integer.parseInt(arguments) - 1;
            return new Command(CommandType.HABIT_STREAK, index);
        } catch (NumberFormatException e) {
            throw new StudyMateException("Please input a valid index!");
        }
    }

    /**
     * Parses a habit remove command.
     *
     * @param arguments The habit index to delete
     * @return A Command object for deleting a habit
     * @throws StudyMateException If the index is invalid
     */
    private Command parseHabitRm(String arguments) throws StudyMateException {
        try {
            int index = Integer.parseInt(arguments) - 1;
            return new Command(CommandType.HABIT_DELETE, index);
        } catch (NumberFormatException e) {
            throw new StudyMateException("Please input a valid index!");
        }
    }

    private void capNumbers(int number) throws StudyMateException {
        if (number > maxValue) {
            throw new StudyMateException("Number is too high!");
        }
    }
}
