package seedu.studymate.database;

import seedu.studymate.parser.DateTimeArg;

/**
 * Class to manage all the data formatting
 */
public class DataFormatting {

    /**
     * Returns a string representation of the to-do task suitable for saving to a file
     * @return A formatted string for file storage
     */
    public static String toDoSaveString(Boolean isDone, String name) {
        if (isDone) {
            return "T|1|" + name;
        }
        return "T|0|" + name;
    }

    /**
     * Returns a string representation of the task suitable for saving to a file
     * @return The formatted string for saving
     */
    public static String deadlineSaveString(Boolean isDone, String name, DateTimeArg deadline) {
        if (isDone) {
            return "D|1|" + name + "|" + deadline;
        }
        return "D|0|" + name + "|" + deadline;
    }

    /**
     * Returns a string representation of the reminder suitable for saving to a file
     * 
     * @param isDone Whether the reminder is completed or triggered.
     * @param name The name of or description of the reminder.
     * @param dateTime The date and time for the reminder.
     * @return A formatted string for file storage, e.g. "R|1|Appointment|2025-10-12T08:00"
     */
    public static String reminderSaveString(Boolean isDone, String name, DateTimeArg dateTime) {
        if (isDone) {
            return "R|1|" + name + "|" + dateTime;
        }
        return "R|0|" + name + "|" + dateTime;
    }


}
