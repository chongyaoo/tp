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
}
