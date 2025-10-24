package seedu.studymate.reminders;

import seedu.studymate.database.DataFormatting;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageFormatting;

import java.time.Duration;

/**
 * A reminder has a DateTime for reminding, and the task to remind
 */
public class Reminder {
    protected final String name;
    protected Schedule schedule;
    protected DateTimeArg remindAt;

    /**
     * Constructs a Reminder with default status !isReminded
     *
     * @param name The name of the task
     **/
    public Reminder(String name, DateTimeArg dateTime) { //One-Time Schedule
        this.schedule = new OneTimeSchedule(dateTime);
        this.name = name;
        this.remindAt = dateTime;
    }

    public Reminder(String name, DateTimeArg dateTime, Duration interval) { //Recurring Schedule
        this.schedule = new RecurringSchedule(dateTime, interval);
        this.name = name;
        this.remindAt = dateTime;
    }

    // Package-private constructor for testing with custom Schedule
    Reminder(String name, DateTimeArg dateTime, Schedule schedule) {
        this.name = name;
        this.remindAt = dateTime;
        this.schedule = schedule;
    }

    /**
     * Returns the name of the task
     *
     * @return The name of the task as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the completion status of the task
     *
     * @param onReminder A boolean indicating if the task is done (true) or not (false)
     */
    public void setOnReminder(Boolean onReminder) {
        this.schedule.setOnReminder(onReminder);
    }

    public Boolean getOnReminder() {
        return this.schedule.getOnReminder();
    }

    public Boolean isDue() {
        return schedule.isDue();
    }

    public void isFired() {
        schedule.isFired();
        if (!schedule.isRecurring()) {
            schedule.setOnReminder(false);
        }
    }

    public boolean isRecurring() {
        return schedule.isRecurring();
    }

    public void snooze(Duration duration) throws StudyMateException {
        schedule.snooze(duration);
    }

    /**
     * Returns a string representation of the task suitable for saving to a file.
     * This method is intended to be overridden by subclasses to provide specific formatting.
     *
     * @return An empty String
     */
    public String toSaveString() {
        if (schedule.isRecurring()) {
            return DataFormatting.recurringReminderSaveString(schedule.getOnReminder(),
                    name, remindAt, schedule.interval());
        }
        return DataFormatting.oneTimeReminderSaveString(schedule.getOnReminder(), name, remindAt);
    }

    public String toString() {
        if (schedule.isRecurring()) {
            return MessageFormatting.recReminderString(schedule.getOnReminder(), name, remindAt, schedule.interval());
        }
        return MessageFormatting.oneTimeReminderString(schedule.getOnReminder(), name, remindAt);
    }
}
