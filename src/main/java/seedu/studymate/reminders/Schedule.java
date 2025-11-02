package seedu.studymate.reminders;

import seedu.studymate.exceptions.StudyMateException;

import java.time.Duration;

/**
 * Interface defining scheduling behavior for reminders.
 * Provides default implementations that can be overridden by concrete schedule types.
 * Supports both one-time and recurring schedules.
 */
public interface Schedule {
    /**
     * Returns the interval between recurring reminders.
     *
     * @return The duration between repetitions, or null for non-recurring schedules
     */
    default Duration interval() {
        return null;
    }

    /**
     * Checks if this is a recurring schedule.
     *
     * @return true if recurring, false otherwise
     */
    default boolean isRecurring() {
        return false;
    }

    /**
     * Checks if the reminder is currently due to fire.
     *
     * @return true if should fire now, false otherwise
     */
    default boolean isDue() {
        return false;
    }

    /**
     * Marks the reminder as fired and performs post-firing actions.
     * For one-time: deactivates. For recurring: reschedules to next occurrence.
     */
    default void isFired() {
    }

    /**
     * Sets whether this reminder has been fired.
     *
     * @param isDone true if fired, false to reset
     */
    default void setFired(boolean isDone) {
    }

    /**
     * Checks if this reminder has been fired.
     *
     * @return true if fired, false otherwise
     */
    default boolean getFired() {
        return false;
    }

    /**
     * Sets whether this reminder is active.
     *
     * @param onReminder true to activate, false to deactivate
     */
    default void setOnReminder(boolean onReminder) {
    }

    /**
     * Checks if this reminder is active.
     *
     * @return true if active, false otherwise
     */
    default boolean getOnReminder() {
        return false;
    }

    /**
     * Delays the reminder by the specified duration.
     *
     * @param duration The delay duration
     * @throws StudyMateException if new snooze timing is not in the future
     */
    default void snooze(Duration duration) throws StudyMateException {
    }
}
