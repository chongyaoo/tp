package seedu.studymate.reminders;

import seedu.studymate.parser.DateTimeArg;

import java.time.*;

public final class OneTimeSchedule implements Schedule {
    private final DateTimeArg remindAt;

    private boolean isFired;

    public OneTimeSchedule(DateTimeArg remindAt) {
        this.remindAt = remindAt;
        this.isFired = false; //when reminder first created, OneTimeSchedule should not be fired
    }

    public void setSchedule() {
        isFired = false;
    }

    @Override

    public boolean isDue() { //checking whether reminder is due to be fired
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        LocalTime nowTime = LocalTime.now(zone);
        LocalDateTime now = LocalDateTime.of(today, nowTime); //obtain local date/time

        LocalDateTime target = LocalDateTime.of(
                remindAt.getDate(),
                remindAt.getTime()
        );
        return !now.isBefore(target) && !isFired;
    }

    public void isFired() {
        this.isFired = true;
    }

    public void snooze(Duration duration) {
        LocalDateTime localDateTime = remindAt.getDateTime().plus(duration);
        remindAt.setLocalDateTime(localDateTime);
        this.isFired = false;
    }

}
