package seedu.studymate.reminders;

import seedu.studymate.parser.DateTimeArg;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;


public class RecurringSchedule implements Schedule {
    private final DateTimeArg remindAt;
    private final Duration interval;
    private Boolean onReminder;
    private final Clock clock;

    // Default constructor for production use
    public RecurringSchedule(DateTimeArg remindAt, Duration interval) {
        this(remindAt, interval, Clock.systemDefaultZone());
    }

    // Package-private constructor for testing
    RecurringSchedule(DateTimeArg remindAt, Duration interval, Clock clock) {
        this.remindAt = remindAt;
        this.interval = interval;
        this.onReminder = true;
        this.clock = clock;
    }

    public Duration interval() {
        return interval;
    }

    @Override
    public void setOnReminder(boolean onReminder) {
        this.onReminder = onReminder;
    }

    public boolean getOnReminder() {
        return onReminder;
    }

    public boolean isRecurring() {
        return true;
    }

    public boolean isDue() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime target = LocalDateTime.of(
                remindAt.getDate(),
                remindAt.getTime()
        );
        return !now.isBefore(target) && onReminder;
    }

    public void setNextSchedule() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime target = LocalDateTime.of(
                remindAt.getDate(),
                remindAt.getTime()
        );

        while (!target.isAfter(now)) {
            target = target.plus(interval);
        }

        remindAt.setLocalDateTime(target);
    }

    public void isFired() {
        setNextSchedule();
    }
}
