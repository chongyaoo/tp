package seedu.studymate.tasks;

/**
 * The base class for all tasks
 * It provides core functionality common to all task types, such as a name and a completion status
 */
public abstract class Task {
    protected String name;
    protected Boolean isDone;

    /**
     * Constructs a Task with the given name and a default status of not done
     * @param name The name of the task
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Returns the name of the task
     * @return The name of the task as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the completion status of the task
     * @param isDone A boolean indicating if the task is done (true) or not (false)
     */
    public void setDone(Boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Gets the completion status of the task
     * @return isDone A boolean indicating if the task is done (true) or not (false)
     */
    public boolean getDone() {
        return isDone;
    }

    /**
     * Sets the name/description of the task
     * @param name The new name/description for the task
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of the task suitable for saving to a file
     * This method is intended to be overridden by subclasses to provide specific formatting
     * @return An empty string
     */
    public abstract String toSaveString();
}
