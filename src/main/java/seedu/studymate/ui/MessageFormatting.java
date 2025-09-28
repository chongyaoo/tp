package seedu.studymate.ui;

/**
 * Class to manage all message formatting
 */
public class MessageFormatting {

    /**
     * Returns a formatted string representation of the to-do task, including its type
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
     * @return The formatted string for display
     */
    public static String deadlineString(Boolean isDone, String name, String deadline) {
        if (isDone) {
            return "[D][X] " + name + " (by: " + deadline + ")";
        }
        return "[D][ ] " + name + " (by: " + deadline + ")";
    }
}
