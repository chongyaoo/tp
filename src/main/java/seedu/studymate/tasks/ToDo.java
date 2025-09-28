package seedu.studymate.tasks;

import seedu.studymate.database.DataFormatting;
import seedu.studymate.ui.MessageFormatting;

/**
 * Represents a to-do task
 * A to-do is a basic task with no specific date or time
 */
public class ToDo extends Task {

    /**
     * Constructs a to-do task with the specified description
     * @param task The description of the to-do task
     */
    public ToDo(String task) {
        super(task);
    }

    /**
     * Returns a string representation of the to-do task suitable for saving to a file
     * @return A formatted string for file storage
     */
    @Override
    public String toSaveString() {
        return DataFormatting.toDoSaveString(isDone, name);
    }

    /**
     * Returns a formatted string representation of the to-do task, including its type
     * @return A formatted string
     */
    @Override
    public String toString() {
        return MessageFormatting.toDoString(isDone, name);
    }
}