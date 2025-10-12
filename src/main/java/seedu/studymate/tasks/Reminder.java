package seedu.studymate.tasks;

import seedu.studymate.database.DataFormatting;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageFormatting;

/**
 * A reminder has a DateTime for reminding, and the task to remind
 */
public class Reminder {
    protected final String name;
    protected Boolean isReminder;
    private final DateTimeArg dateTime;

    /**
     * Constructs a Reminder with default status !isReminded
     *
     * @param name The name of the task
     **/
    public Reminder(String name, DateTimeArg dateTime) {
        this.name = name;
        this.isReminder = false;
        this.dateTime = dateTime;
    }

    /**
     * Returns the name of the task
     *
     * @return The name of the task as a String
     */
    public String getReminder() {
        return name;
    }

    /**
     * Sets the completion status of the task
     *
     * @param isReminder A boolean indicating if the task is done (true) or not (false)
     */
    public void setReminded(Boolean isReminder) {
        this.isReminder = isReminder;
    }

    /**
     * Returns a string representation of the task suitable for saving to a file.
     * This method is intended to be overridden by subclasses to provide specific formatting.
     * 
     * @return An empty String
     */
    public String toSaveString() {
        return DataFormatting.reminderSaveString(isReminder, name, dateTime);
    }

    public String toString() {
        return MessageFormatting.reminderString(isReminder, name, dateTime);
    }

}

