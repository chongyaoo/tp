package seedu.studymate.reminders;

public class IndexedReminder {
    public final int index;
    public final Reminder reminder;

    public IndexedReminder(int index, Reminder reminder) {
        this.index = index;
        this.reminder = reminder;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public int getIndex() {
        return index;
    }
}
