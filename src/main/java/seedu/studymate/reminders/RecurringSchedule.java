package seedu.studymate.reminders;

import seedu.studymate.parser.DateTimeArg;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Schedule that fires repeatedly at regular intervals.
 * Automatically reschedules after each firing. Cannot be snoozed.
 */
public class RecurringSchedule implements Schedule {
    private final DateTimeArg remindAt;
    private final Duration interval;
    private Boolean onReminder;
    private final Clock clock;

    /**
     * Constructs a RecurringSchedule.
     * Created in active state (onReminder = true).
     *
     * @param remindAt When to first fire
     * @param interval Duration between firings
     * @param clock Clock for time comparisons
     */
    public RecurringSchedule(DateTimeArg remindAt, Duration interval, Clock clock) {
        this.remindAt = remindAt;
        this.interval = interval;
        this.onReminder = true;
        this.clock = clock;
    }

    /**
     * Returns the interval between firings.
     *
     * @return The duration between repetitions
     */
    public Duration interval() {
        return interval;
    }

    /**
     * Sets whether this reminder is active.
     *
     * @param onReminder true to activate, false to deactivate
     */
    @Override
    public void setOnReminder(boolean onReminder) {
        this.onReminder = onReminder;
    }

    /**
     * Checks if this reminder is active.
     *
     * @return true if active, false otherwise
     */
    public boolean getOnReminder() {
        return onReminder;
    }

    /**
     * Checks if reminder is due to fire.
     * Returns true if active and current time >= scheduled time.
     *
     * @return true if reminder should fire now, false otherwise
     */
    public boolean isDue() {
        if (!onReminder) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime target = LocalDateTime.of(remindAt.getDate(), remindAt.getTime());
        return !now.isBefore(target);
    }

    /**
     * Calculates and sets the next occurrence.
     * Adds interval repeatedly until target time is in the future.
     * Handles cases where multiple intervals have passed.
     */
    public void setNextSchedule() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime target = LocalDateTime.of(remindAt.getDate(), remindAt.getTime());

        while (!target.isAfter(now)) {
            target = target.plus(interval);
        }

        remindAt.setLocalDateTime(target);
    }

    /**
     * Marks as fired and schedules next occurrence.
     * Reminder remains active.
     */
    public void isFired() {
        setNextSchedule();
    }
}