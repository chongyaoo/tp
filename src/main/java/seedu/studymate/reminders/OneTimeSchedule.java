package seedu.studymate.reminders;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.DateTimeArg;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

public final class OneTimeSchedule implements Schedule {
    private final DateTimeArg remindAt;
    private boolean isFired;
    private boolean onReminder;
    private final Clock clock;

    public OneTimeSchedule(DateTimeArg remindAt, Clock clock) {
        this.remindAt = remindAt;
        this.clock = clock;
        this.isFired = false; //when reminder first created, OneTimeSchedule should not be fired
        this.onReminder = true;
    }

    @Override
    public void setOnReminder(boolean onReminder) {
        this.onReminder = onReminder;
    }

    public boolean getOnReminder() {
        return onReminder;
    }

    public boolean isDue() { //checking whether reminder is due to be fired
        LocalDateTime now = LocalDateTime.now(clock); //use injected clock

        LocalDateTime target = LocalDateTime.of(
                remindAt.getDate(),
                remindAt.getTime()
        );
        return !now.isBefore(target) && !isFired && onReminder;
    }

    public void isFired() {
        this.onReminder = false; //Automatically turns off reminder
        this.isFired = true;
    }

    public void snooze(Duration duration) throws StudyMateException {
        LocalDateTime newDateTime = remindAt.getDateTime().plus(duration);
        LocalDateTime now = LocalDateTime.now(clock); //use injected clock
        if (newDateTime.isBefore(now) || newDateTime.isEqual(now)) {
            // New time is in the past or now
            throw new StudyMateException("Snooze duration too short! " +
                    "New reminder time (" + newDateTime + ") is not in the future.");
        }
        // New time is in the future - proceed
        remindAt.setLocalDateTime(newDateTime);
        this.isFired = false;
        this.onReminder = true;
    }
}
