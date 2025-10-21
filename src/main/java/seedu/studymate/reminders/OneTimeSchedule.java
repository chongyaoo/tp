package seedu.studymate.reminders;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.DateTimeArg;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import seedu.studymate.ui.MessageHandler;

public final class OneTimeSchedule implements Schedule {
    private final DateTimeArg remindAt;
    private boolean isFired;
    private boolean onReminder;

    public OneTimeSchedule(DateTimeArg remindAt) {
        this.remindAt = remindAt;
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
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        LocalTime nowTime = LocalTime.now(zone);
        LocalDateTime now = LocalDateTime.of(today, nowTime); //obtain local date/time

        LocalDateTime target = LocalDateTime.of(
                remindAt.getDate(),
                remindAt.getTime()
        );
        System.out.println("This is due: " + (!now.isBefore(target) && !isFired && onReminder));
        return !now.isBefore(target) && !isFired && onReminder;
    }

    public void isFired() {
        this.onReminder = false; //Automatically turns off reminder
        this.isFired = true;
    }

    public void snooze(Duration duration) throws StudyMateException{
        LocalDateTime newDateTime = remindAt.getDateTime().plus(duration);
        LocalDateTime now = LocalDateTime.now();
        if (newDateTime.isBefore(now) || newDateTime.isEqual(now)) {
            // New time is in the past or now
            throw new StudyMateException("Snooze duration too short! New reminder time (" + newDateTime + ") is not in the future.");
        }
        // New time is in the future - proceed
        remindAt.setLocalDateTime(newDateTime);
        this.isFired = false;
        this.onReminder = true;
    }
}
