package seedu.studymate.reminders;

import java.time.LocalDateTime;
import java.util.List;

public final class Scheduler {
    private final ReminderList reminderList;

    public Scheduler(ReminderList reminderList) {
        this.reminderList = reminderList;
    }

    /**
     * Call this on a timer (e.g., every 30s) and on app open.
     */
    public List<Reminder> tick() {
        List<Reminder> remindersToOutput = new java.util.ArrayList<>();
        for (Reminder r : reminderList.getReminders()) {
           if (r.isDue()) {
                // Add reminders to list to output
                remindersToOutput.add(r);
                // Perform resets after firing the reminder once
                r.isFired();
            }
        }
        return remindersToOutput;
    }
}