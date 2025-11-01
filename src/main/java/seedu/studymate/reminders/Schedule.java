package seedu.studymate.reminders;

import seedu.studymate.exceptions.StudyMateException;

import java.time.Duration;

public interface Schedule {
    default Duration interval() {
        return null;
    }

    default boolean isRecurring() {
        return false;
    }

    default boolean isDue() {
        return false;
    }

    default void isFired() {
    }

    default void setFired(boolean isDone) {
    }

    default boolean getFired() {
        return false;
    }

    default void setOnReminder(boolean onReminder) {
    }

    default boolean getOnReminder() {
        return false;
    }

    default void snooze(Duration duration) throws StudyMateException {
    }
}
