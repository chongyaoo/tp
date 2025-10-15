package seedu.studymate.reminders;

import seedu.studymate.parser.DateTimeArg;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;


public class RecurringSchedule implements Schedule {
    private final DateTimeArg remindAt;
    private final Duration interval;

    public RecurringSchedule(DateTimeArg remindAt, Duration interval) {
        this.remindAt = remindAt;
        this.interval = interval;
    }

    public Duration interval() {
        return interval;
    }

    @Override
    public boolean isRecurring() {
        return true;
    }

    public boolean isDue() { //checking whether reminder is due to be fired
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        LocalTime nowTime = LocalTime.now(zone);
        LocalDateTime now = LocalDateTime.of(today, nowTime); //obtain local date/time

        LocalDateTime target = LocalDateTime.of(
                remindAt.getDate(),
                remindAt.getTime()
        );
        return !now.isBefore(target);
    }

    public void setNextSchedule() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        LocalTime nowTime = LocalTime.now(zone);
        LocalDateTime now = LocalDateTime.of(today, nowTime); //obtain local date/time

        LocalDateTime target = LocalDateTime.of(
                remindAt.getDate(),
                remindAt.getTime()
        );

        while (target.isBefore(now)) {
            target = target.plus(interval);
        }

        remindAt.setLocalDateTime(target);
    }

    public void isFired() {
        setNextSchedule();
    }

}
