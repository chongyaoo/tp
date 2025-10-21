package seedu.studymate.ui;

import seedu.studymate.parser.DateTimeArg;

import java.time.Duration;

/**
 * Class to manage all message formatting
 */
public class MessageFormatting {

    /**
     * Returns a formatted string representation of the to-do task, including its type
     *
     * @return A formatted string
     */
    public static String toDoString(Boolean isDone, String name) {
        if (isDone) {
            return "[T][X] " + name;
        }
        return "[T][ ] " + name;
    }

    /**
     * Returns a human-readable string representation of the deadline, including its completion status and due date
     *
     * @return The formatted string for display
     */
    public static String deadlineString(Boolean isDone, String name, DateTimeArg deadline) {
        if (isDone) {
            return "[D][X] " + name + " (by: " + deadline + ")";
        }
        return "[D][ ] " + name + " (by: " + deadline + ")";
    }

    /**
     * Returns a human-readable string representation of the event, including its completion status, from DateTimeArg
     * and end DateTimeArg
     *
     * @return The formatted string for display
     */
    public static String eventString(Boolean isDone, String name, DateTimeArg from, DateTimeArg to) {
        if (isDone) {
            return "[E][X] " + name + " (from: " + from + ", to: " + to + ")";
        }
        return "[E][ ] " + name + " (from: " + from + ", to: " + to + ")";
    }

    public static String oneTimeReminderString(Boolean onReminder, String name, DateTimeArg dateTime) {
        String dateTimeString = dateTime.toString().replace("T", " ");
        if (onReminder) {
            return "[RO][O] " + name + " (" + dateTimeString + ")";
        }
        return "[RO][ ] " + name + " (" + dateTimeString + ")";
    }

    public static String recReminderString(Boolean onReminder, String name, DateTimeArg dateTime,
                                           Duration reminderInterval) {
        String returnString;
        String dateTimeString = dateTime.toString().replace("T", " ");
        String reminderIntervalString = reminderInterval.toString().replace("PT", "");
        if (onReminder) {
            returnString = "[RR][O] " + name + " (interval: " + reminderIntervalString + ")\n" +
                    "Next reminder: " + dateTimeString;
            return returnString;
        }
        returnString = "[RR][ ] " + name + " (interval: " + reminderIntervalString + ")\n" +
                "Next reminder: " + dateTimeString;
        return returnString;
    }
}
