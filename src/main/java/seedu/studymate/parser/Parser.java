package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static String DELIMITER_BY = "/by";
    private static Pattern integerPattern = Pattern.compile("\\d");
    private static Pattern multipleIntegerPattern = Pattern.compile(("\\d...\\d"));

    public Command parse(String line) throws StudyMateException {
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

        // Assuming Command class supports description and deadline:
        return new Command(CommandType.DEADLINE, desc, deadline);
    }

    private Command parseMark(String[] arguments) throws StudyMateException {
        int[] ranges = parseIndexRange(arguments);
        return new Command(CommandType.MARK, ranges[0], ranges[1]);
    }

    private Command parseUnmark(String[] arguments) throws StudyMateException {
        int[] ranges = parseIndexRange(arguments);
        return new Command(CommandType.UNMARK, ranges[0], ranges[1]);
    }

    private Command parseDelete(String[] arguments) throws StudyMateException {
        int[] ranges = parseIndexRange(arguments);
        return new Command(CommandType.DELETE, ranges[0], ranges[1]);
    }

    private int[] parseIndexRange(String[] arguments) throws StudyMateException {
        // Check that the task number is not empty
        if (arguments[1].isEmpty()) {
            throw new StudyMateException("The " + arguments[0] + " command must be followed by a task number.");
        }
        try {
            String[] indexArgs = arguments[1].split(",");
            int[] ranges = new int[2];
            int maximum = Integer.MIN_VALUE;
            int minimum = Integer.MAX_VALUE;
            for (String arg: indexArgs) {
                if (integerPattern.matcher(arg).find()) {
                    int val = Integer.parseInt(arg);
                    if (val > maximum) {
                        maximum = val;
                    }
                    if (val < minimum) {
                        minimum = val;
                    }
                } else if (multipleIntegerPattern.matcher(arg).find()) {
                    int[] startAndEndArgs = Arrays.stream(arg.split("...")).mapToInt(Integer::parseInt).toArray();
                    if (startAndEndArgs[0] > startAndEndArgs[1]) {
                        throw new NumberFormatException();
                    }
                    for (int i = startAndEndArgs[0]; i <= startAndEndArgs[1]; i++) {
                        if (i > maximum) {
                            maximum = i;
                        }
                        if (i < minimum) {
                            minimum = i;
                        }
                    }
                    ranges[0] = minimum;
                    ranges[1] = maximum;
                    return ranges;
                } else {
                    throw new NumberFormatException();
                }
            }
            return ranges;
        } catch (NumberFormatException e) {
            throw new StudyMateException("The " + arguments[0] + " command must be followed by a valid input");
        }
    }

}
