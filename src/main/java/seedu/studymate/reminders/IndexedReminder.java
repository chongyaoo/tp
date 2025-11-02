package seedu.studymate.reminders;

/**
 * Wrapper class that pairs a reminder with its 1-based display index.
 * Used by the scheduler to track reminder positions when notifying users.
 */
public class IndexedReminder {
    /**
     * The 1-based position of this reminder in the list
     */
    public final int index;

    /**
     * The reminder object
     */
    public final Reminder reminder;

    /**
     * Constructs an IndexedReminder.
     *
     * @param index    The 1-based position in the reminder list
     * @param reminder The reminder object
     */
    public IndexedReminder(int index, Reminder reminder) {
        this.index = index;
        this.reminder = reminder;
    }

    /**
     * Returns the reminder object.
     *
     * @return The reminder
     */
    public Reminder getReminder() {
        return reminder;
    }

    /**
     * Returns the 1-based index.
     *
     * @return The index position
     */
    public int getIndex() {
        return index;
    }
}
