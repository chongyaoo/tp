package seedu.studymate.reminders;

import seedu.studymate.ui.MessageHandler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages periodic checking and triggering of due reminders from a {@link ReminderList}.
 * The Scheduler runs in a background thread, periodically checking all reminders
 * and automatically notifying users when reminders become due.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Automatic background checking at configurable intervals (default: 30 seconds)</li>
 *   <li>Immediate check upon startup to catch any missed reminders</li>
 *   <li>Thread-safe operation with synchronized access to the reminder list</li>
 *   <li>Automatic notification of due reminders via MessageHandler</li>
 *   <li>Support for both one-time and recurring reminders</li>
 *   <li>Clean shutdown mechanism to stop background threads</li>
 * </ul>
 *
 * <p>The scheduler uses a single-threaded executor to ensure reminders are checked
 * sequentially without overlapping checks. This prevents race conditions and ensures
 * consistent reminder state management.</p>
 *
 * <p><b>Thread safety:</b> The scheduler synchronizes on the ReminderList when checking
 * reminders to ensure thread-safe access to the reminder collection.</p>
 */
public class Scheduler {
    /**
     * The list of reminders to check periodically
     */
    private final ReminderList reminderList;

    /**
     * Executor service managing the background checking thread
     */
    private ScheduledExecutorService executorService;

    /**
     * Time interval in seconds between reminder checks
     */
    private final int intervalSeconds;

    /**
     * Constructs a Scheduler with the specified reminder list using the default check interval.
     * The scheduler is created in an inactive state and must be started with {@link #start()}.
     *
     * <p>This constructor uses the default check interval of 30 seconds, which provides
     * a good balance between responsiveness and system resource usage.</p>
     *
     * @param reminderList The {@link ReminderList} containing reminders to monitor for due times
     */
    public Scheduler(ReminderList reminderList) {
        this(reminderList, 30); // Default 30 seconds
    }

    /**
     * Constructs a Scheduler with the specified reminder list and custom check interval.
     * The scheduler is created in an inactive state and must be started with {@link #start()}
     *
     * @param reminderList    The {@link ReminderList} containing reminders to monitor for due times
     * @param intervalSeconds The time in seconds between reminder checks (must be positive).
     */
    public Scheduler(ReminderList reminderList, int intervalSeconds) {
        this.reminderList = reminderList;
        this.intervalSeconds = intervalSeconds;
    }

    /**
     * Starts the periodic reminder checking with automatic user notifications.
     * This method initializes a background thread that checks for due reminders
     * at the configured interval and automatically notifies users when reminders fired.
     *
     * <p>The scheduler uses a single-threaded executor to ensure sequential processing.
     * Each check cycle:</p>
     * <ol>
     *   <li>Iterates through all reminders in the list</li>
     *   <li>Identifies reminders that are due</li>
     *   <li>Fires those reminders (updating their state)</li>
     *   <li>Sends notifications to the user via MessageHandler</li>
     * </ol>
     *
     * <p><b>Thread safety:</b> The periodic checks synchronize on the reminderList
     * to ensure safe concurrent access.</p>
     *
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
     * Stops the periodic reminder checking and shuts down the background thread.
     * This method should be called when the application is closing to ensure
     * proper cleanup of system resources.
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>Immediately attempts to stop all executing and pending checks</li>
     *   <li>Interrupts the background thread if it's currently running a check</li>
     *   <li>Releases all associated system resources</li>
     *   <li>If already shut down or never started, this method does nothing</li>
     * </ul>
     *
     * <p>After calling shutdown(), the scheduler cannot be restarted. A new
     * Scheduler instance must be created if reminder checking needs to resume.</p>
     *
     */
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    /**
     * Performs a single check for due reminders and returns them without sending notifications.
     * This method provides manual control over reminder checking and is useful for testing
     * or implementing custom notification logic.
     *
     * <p>Operation flow:</p>
     * <ol>
     *   <li>Synchronizes on the reminderList for thread-safe access</li>
     *   <li>Iterates through all reminders in the list</li>
     *   <li>Checks each reminder's {@link Reminder#isDue()} status</li>
     *   <li>For each due reminder:
     *     <ul>
     *       <li>Retrieves its list index (1-based for display)</li>
     *       <li>Creates an {@link IndexedReminder} wrapper</li>
     *       <li>Calls {@link Reminder#isFired()} to update its state</li>
     *       <li>Adds it to the return list</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * <p>State changes:</p>
     * <ul>
     *   <li><b>One-time reminders</b>: Marked as fired and deactivated</li>
     *   <li><b>Recurring reminders</b>: Rescheduled to their next occurrence</li>
     * </ul>
     *
     * @return A list of {@link IndexedReminder} objects representing all reminders
     *              that were due at the time of this check.
     */

    public List<IndexedReminder> tick() {
        List<IndexedReminder> remindersToOutput = new java.util.ArrayList<>();
        synchronized (reminderList) {
            for (Reminder r : reminderList.getReminders()) {
                if (r.isDue()) {
                    int index = reminderList.getReminderIndex(r) + 1;
                    IndexedReminder indexedReminder = new IndexedReminder(index, r);
                    remindersToOutput.add(indexedReminder);
                    r.isFired();
                }
            }
        }
        return remindersToOutput;
    }

    /**
     * Private helper method that checks for due reminders and automatically notifies the user.
     * This method combines the functionality of {@link #tick()} with automatic notification
     * via {@link MessageHandler}.
     *
     * <p>Operation:</p>
     * <ol>
     *   <li>Calls {@link #tick()} to identify and fire all due reminders</li>
     *   <li>If any reminders were due, sends them to MessageHandler for user notification</li>
     *   <li>If no reminders are due, does nothing (no notification)</li>
     * </ol>
     *
     */
    private void checkAndNotify() {
        List<IndexedReminder> dueReminders = tick();
        if (!dueReminders.isEmpty()) {
            MessageHandler.sendReminder(dueReminders);
        }
    }
}
