package seedu.studymate.reminders;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.DateTimeArg;

import java.time.Duration;

public interface Schedule {
    //DateTimeArg nextTriggerAfter(Instant now); // when should it fire next?

    default Duration interval() {
        return null;
    }

    default boolean isRecurring() {
        return false;
    }

    default boolean isDue() {
        return false;
    }

    default void isFired() {}

    default void setRemindAt(DateTimeArg remindAt) {}

    default void setOnReminder(boolean onReminder) {}

    default boolean getOnReminder() {return false;}

    default void snooze(Duration duration) throws StudyMateException {};
}
