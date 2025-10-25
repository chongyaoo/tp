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
        String dateTimeString = deadline.toString().replace("T", " ");
        if (isDone) {
            return "[D][X] " + name + " (by: " + dateTimeString + ")";
        }
        return "[D][ ] " + name + " (by: " + dateTimeString + ")";
    }

    /**
     * Returns a human-readable string representation of the event, including its completion status, from DateTimeArg
     * and end DateTimeArg
     *
     * @return The formatted string for display
     */
    public static String eventString(Boolean isDone, String name, DateTimeArg from, DateTimeArg to) {
        String fromString = from.toString().replace("T", " ");
        String toEventString = to.toString().replace("T", " ");
        if (isDone) {
            return "[E][X] " + name + " (from: " + fromString + ", to: " + toEventString + ")";
        }
        return "[E][ ] " + name + " (from: " + fromString + ", to: " + toEventString + ")";
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

    /**
     * Returns a formatted string representation of a habit
     *
     * @param name The name of the habit
     * @param deadline The deadline for the habit
     * @param streak The current streak count
     * @return The formatted string for display
     */
    public static String habitString(String name, DateTimeArg deadline, int streak) {
        String deadlineString = deadline.toString().replace("T", " ");
        return "[H] " + name + " (deadline: " + deadlineString + ", streak: " + streak + ")";
    }
}
