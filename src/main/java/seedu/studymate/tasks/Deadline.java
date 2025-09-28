package seedu.studymate.tasks;

import seedu.studymate.database.DataFormatting;
import seedu.studymate.ui.MessageFormatting;

/**
 * Represents a deadline task
 * A deadline is a basic task with a do-by date
 */
public class Deadline extends Task {

    private final String deadline;

    /**
     * Constructs Deadline task with the given name and a deadline
     * @param task The description or name of the task
     * @param deadline The deadline time of the task
     */
    public Deadline(String task, String deadline) {
        super(task);
        this.deadline = deadline;
    }

    /**
     * Returns a string representation of the task suitable for saving to a file
     * @return The formatted string for saving
     */
    @Override
    public String toSaveString() {
        return DataFormatting.deadlineSaveString(isDone, name, deadline);
    }

    /**
     * Returns a human-readable string representation of the deadline, including its completion status and due date
     * @return The formatted string for display
     */
    @Override
    public String toString() {
        return MessageFormatting.deadlineString(isDone, name, deadline);
    }
}
