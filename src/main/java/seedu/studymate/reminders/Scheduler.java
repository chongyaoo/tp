package seedu.studymate.reminders;

import seedu.studymate.ui.MessageHandler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private final ReminderList reminderList;
    private ScheduledExecutorService executorService;
    private final int intervalSeconds;

    public Scheduler(ReminderList reminderList) {
        this(reminderList, 30); // Default 30 seconds
    }

    public Scheduler(ReminderList reminderList, int intervalSeconds) {
        this.reminderList = reminderList;
        this.intervalSeconds = intervalSeconds;
    }

    /**
     * Starts periodic reminder checking with automatic notifications.
     */
    public void start() {
        if (executorService != null) {
            return; // Already started
        }

        executorService = Executors.newSingleThreadScheduledExecutor();

        // Check immediately on start
        checkAndNotify();

        // Schedule periodic checks
        executorService.scheduleAtFixedRate(
                this::checkAndNotify,
                intervalSeconds,
                intervalSeconds,
                TimeUnit.SECONDS
        );
    }

    /**
     * Stops the periodic reminder checking.
     */
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    /**
     * Checks for due reminders and returns them.
     * Call this manually or let start() handle it automatically.
     */
    public synchronized List<Reminder> tick() {
        List<Reminder> remindersToOutput = new java.util.ArrayList<>();
        for (Reminder r : reminderList.getReminders()) {
            if (r.isDue()) {
                remindersToOutput.add(r);
                r.isFired();
            }
        }
        return remindersToOutput;
    }

    private void checkAndNotify() {
        List<Reminder> dueReminders = tick();
        if (!dueReminders.isEmpty()) {
            MessageHandler.sendRemindersDone(dueReminders);
        }
    }
}
