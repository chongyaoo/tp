package seedu.studymate.reminders;

import seedu.studymate.parser.DateTimeArg;

import java.time.Duration;

public interface Schedule {
    //DateTimeArg nextTriggerAfter(Instant now); // when should it fire next?

    default Duration interval() {
        return null;
    }

    default void snooze(Duration d) {}

    default boolean isRecurring() {
        return false;
    }

    default boolean isDue() {
        return false;
    }

    default void isFired() {}

    default void setRemindAt(DateTimeArg remindAt) {}

}
