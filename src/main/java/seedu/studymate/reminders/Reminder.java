package seedu.studymate.reminders;

import seedu.studymate.database.DataFormatting;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageFormatting;

import java.time.Clock;
import java.time.Duration;

/**
 * Represents a reminder with a name and schedule.
 * Delegates scheduling logic to either OneTimeSchedule or RecurringSchedule.
 * Acts as a facade providing unified interface for different schedule types.
 */
public class Reminder {
    protected final String name;
    protected Schedule schedule;
    protected DateTimeArg remindAt;

    /**
     * Constructs a one-time Reminder with specified fired status.
     * Used when loading reminders from storage.
     *
     * @param name Task/event description
     * @param dateTime When to fire
     * @param clock Clock for time comparisons
     * @param isDone Whether already fired
     */
    public Reminder(String name, DateTimeArg dateTime, Clock clock, boolean isDone) {
        this.schedule = new OneTimeSchedule(dateTime, clock);
        this.schedule.setFired(isDone);
        this.name = name;
        this.remindAt = dateTime;
    }

    /**
     * Constructs a One-time Reminder for testing (fired status = true).
     *
     * @param name Task/event description
     * @param dateTime When to fire
     * @param clock Clock for time comparisons
     */
    public Reminder(String name, DateTimeArg dateTime, Clock clock) {
        this.schedule = new OneTimeSchedule(dateTime, clock);
        this.schedule.setFired(true);
        this.name = name;
        this.remindAt = dateTime;
    }

    /**
     * Constructs a recurring Reminder.
     *
     * @param name Task/event description
     * @param dateTime When to first fire
     * @param interval Duration between firings
     * @param clock Clock for time comparisons
     */
    public Reminder(String name, DateTimeArg dateTime, Duration interval, Clock clock) {
        this.schedule = new RecurringSchedule(dateTime, interval, clock);
        this.name = name;
        this.remindAt = dateTime;
    }

    /**
     * Package-private constructor for testing with custom Schedule.
     *
     * @param name Task/event description
     * @param dateTime Reminder time
     * @param schedule Custom schedule implementation
     */
    Reminder(String name, DateTimeArg dateTime, Schedule schedule) {
        this.name = name;
        this.remindAt = dateTime;
        this.schedule = schedule;
    }

    /**
     * Returns the reminder name.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets whether this reminder is active.
     *
     * @param onReminder true to activate, false to deactivate
     */
    public void setOnReminder(Boolean onReminder) {
        this.schedule.setOnReminder(onReminder);
    }

    /**
     * Checks if this reminder is active.
     *
     * @return true if active, false otherwise
     */
    public Boolean getOnReminder() {
        return this.schedule.getOnReminder();
    }

    /**
     * Checks if this reminder is due to fire.
     * Called periodically by Scheduler.
     *
     * @return true if reminder should fire now, false otherwise
     */
    public Boolean isDue() {
        return schedule.isDue();
    }

    /**
     * Marks as fired and performs post-firing actions.
     * One-time: deactivates. Recurring: reschedules to next occurrence.
     * Called by Scheduler when reminder triggers.
     */
    public void isFired() {
        schedule.isFired();
        if (!schedule.isRecurring()) {
            schedule.setOnReminder(false);
        }
    }

    /**
     * Checks if this is a recurring reminder.
     *
     * @return true if recurring, false if one-time
     */
    public boolean isRecurring() {
        return schedule.isRecurring();
    }

    /**
     * Delays this reminder by specified duration.
     * Only supported for one-time reminders.
     *
     * @param duration Delay duration
     * @throws StudyMateException if new time not in future
     */
    public void snooze(Duration duration) throws StudyMateException {
        schedule.snooze(duration);
    }

    /**
     * Returns string formatted for saving to storage.
     * Includes all data needed to reconstruct reminder when loading.
     *
     * @return Formatted save string
     */
    public String toSaveString() {
        if (schedule.isRecurring()) {
            return DataFormatting.recurringReminderSaveString(schedule.getOnReminder(),
                    name, remindAt, schedule.interval());
        }
        return DataFormatting.oneTimeReminderSaveString(schedule.getOnReminder(), name, remindAt, schedule.getFired());
    }

    /**
     * Returns string formatted for display to users.
     *
     * @return Formatted display string
     */
    public String toString() {
        if (schedule.isRecurring()) {
            return MessageFormatting.recReminderString(schedule.getOnReminder(), name, remindAt, schedule.interval());
        }
        return MessageFormatting.oneTimeReminderString(schedule.getOnReminder(), name, remindAt);
    }
}
