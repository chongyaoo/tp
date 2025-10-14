package seedu.studymate.reminders;

import seedu.studymate.parser.DateTimeArg;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public interface Schedule {
    //DateTimeArg nextTriggerAfter(Instant now); // when should it fire next?

    default void snooze(Duration d) {}

    default boolean isRecurring() {
        return false;
    }

    default boolean isDue() {
        return false;
    };

    default void isFired() {};
}
