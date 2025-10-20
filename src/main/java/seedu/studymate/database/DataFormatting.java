package seedu.studymate.database;

import seedu.studymate.parser.DateTimeArg;

import java.time.Duration;

/**
 * Class to manage all the data formatting
 */
public class DataFormatting {
    private static final char DELIM = 0x1F; // for serialisation
    /**
     * Returns a string representation of the to-do task suitable for saving to a file
     * @return A formatted string for file storage
     */
    public static String toDoSaveString(Boolean isDone, String name) {
        return "T" + DELIM + (isDone ? "1" : "0") + DELIM + name;
    }

    /**
     * Returns a string representation of the deadline suitable for saving to a file
     * @return The formatted string for saving
     */
    public static String deadlineSaveString(Boolean isDone, String name, DateTimeArg deadline) {
        return "D" + DELIM + (isDone ? "1" : "0") + DELIM + name + DELIM + deadline;
    }

    /**
     * Returns a string representation of the event suitable for saving to a file
     * @return The formatted string for saving
     */
    public static String eventSaveString(Boolean isDone, String name, DateTimeArg from, DateTimeArg to) {
        return "E" + DELIM + (isDone ? "1" : "0") + DELIM + name + DELIM + from + DELIM + to;
    }

    /**
     * Returns a string representation of a one-time reminder suitable for saving to a file
     * 
     * @param isDone Whether the reminder is completed or triggered.
     * @param name The name of or description of the reminder.
     * @param dateTime The date and time for the reminder.
     * @return A formatted string for file storage, e.g. "R|1|Appointment|2025-10-12T08:00"
     */
    public static String oneTimeReminderSaveString(Boolean isDone, String name, DateTimeArg dateTime) {
        return "R" + DELIM + "0" + DELIM + (isDone ? "1" : "0") + DELIM + name + DELIM + dateTime;
    }

    /**
     * Returns a string representation of arecurring reminder suitable for saving to a file
     *
     * @param isDone Whether the reminder is completed or triggered.
     * @param name The name of or description of the reminder.
     * @param dateTime The date and time for the reminder.
     * @return A formatted string for file storage, e.g. "R|1|Appointment|2025-10-12T08:00"
     */
    public static String recurringReminderSaveString(Boolean isDone, String name, DateTimeArg dateTime,
                                                     Duration interval) {
        return "R" + DELIM + "1" + DELIM + (isDone ? "1" : "0") + DELIM + name + DELIM + dateTime + DELIM + interval;
    }
}
