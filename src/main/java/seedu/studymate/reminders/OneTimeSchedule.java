package seedu.studymate.reminders;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.DateTimeArg;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Schedule that fires exactly once at a specified time.
 * After firing, automatically becomes inactive.
 * Fires only if: not yet fired, currently active, and current time >= scheduled time.
 */
public final class OneTimeSchedule implements Schedule {
    private final DateTimeArg remindAt;
    private boolean isFired;
    private boolean onReminder;
    private final Clock clock;

    /**
     * Constructs a OneTimeSchedule.
     * Created in active state (onReminder = true) and unfired state (isFired = false).
     *
     * @param remindAt When to fire this reminder
     * @param clock Clock for time comparisons (enables testing)
     */
    public OneTimeSchedule(DateTimeArg remindAt, Clock clock) {
        this.remindAt = remindAt;
        this.clock = clock;
        this.isFired = false;
        this.onReminder = true;
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
     * @return true if active, false if disabled
     */
    public boolean getOnReminder() {
        return onReminder;
    }

    /**
     * Checks if reminder is due to fire.
     * Returns true only if: not fired, active, and current time >= scheduled time.
     *
     * @return true if reminder should fire now, false otherwise
     */
    public boolean isDue() {
        if (isFired || !onReminder) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime target = LocalDateTime.of(remindAt.getDate(), remindAt.getTime());
        return !now.isBefore(target);
    }

    /**
     * Marks as fired and automatically deactivates.
     * Sets isFired = true and onReminder = false.
     */
    public void isFired() {
        this.onReminder = false;
        this.isFired = true;
    }

    /**
     * Manually sets the fired state.
     * Used when loading from storage or resetting after snooze.
     *
     * @param isFired true to mark as fired, false to reset
     */
    public void setFired(boolean isFired) {
        this.isFired = isFired;
    }

    /**
     * Checks if this reminder has been fired.
     *
     * @return true if fired, false if pending
     */
    public boolean getFired() {
        return isFired;
    }

    /**
     * Delays this reminder by the specified duration.
     * Resets to active, unfired state with new trigger time.
     * New time must be in the future.
     *
     * @param duration Duration to delay by
     * @throws StudyMateException if new time would be in past or present
     */
    public void snooze(Duration duration) throws StudyMateException {
        LocalDateTime newDateTime = remindAt.getDateTime().plus(duration);
        LocalDateTime now = LocalDateTime.now(clock);
        if (newDateTime.isBefore(now) || newDateTime.isEqual(now)) {
            throw new StudyMateException("Snooze duration too short! " +
                    "New reminder time (" + newDateTime + ") is not in the future.");
        }
        remindAt.setLocalDateTime(newDateTime);
        this.isFired = false;
        this.onReminder = true;
    }
}