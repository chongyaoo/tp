package seedu.studymate.reminders;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageHandler;

/**
 * Represents a list of tasks
 * It provides methods for adding, deleting, marking, and unmarking tasks
 */
public class ReminderList {
    private static final Logger logger = Logger.getLogger("TaskList Logger");
    private static final int cap = 10000;
    private final ArrayList<Reminder> reminderList;
    private final Clock clock;

    /**
     * Constructs an empty TaskList
     */
    public ReminderList(Clock clock) {
        reminderList = new ArrayList<>();
        this.clock = clock;
    }

    public ReminderList() {
        this(Clock.systemDefaultZone());
    }

    public synchronized void addReminderRec(String name, DateTimeArg dateTime, Duration interval)
            throws StudyMateException {
        Reminder newReminder = new Reminder(name, dateTime, interval, clock);
        if (reminderList.size() >= cap) {
            throw new StudyMateException("Too many reminders! Please delete some to add in more.");
        }
        reminderList.add(newReminder);
        assert (reminderList.contains(newReminder));
    }

    public synchronized void addReminderOneTime(String name, DateTimeArg dateTime, boolean isFired)
            throws StudyMateException {
        Reminder newReminder = new Reminder(name, dateTime, clock, isFired);
        if (reminderList.size() >= cap) {
            throw new StudyMateException("Too many reminders! Please delete some to add in more.");
        }
        reminderList.add(newReminder);
        assert (reminderList.contains(newReminder));
    }

    public void addReminderOneTime(String name, DateTimeArg dateTime) throws StudyMateException {
        addReminderOneTime(name, dateTime, false);
    }

    public synchronized int getCount() {
        return reminderList.size();
    }

    public synchronized Reminder getReminder(int index) {
        return reminderList.get(index);
    }

    public synchronized void delete(LinkedHashSet<Integer> indexes) {
        ArrayList<Reminder> reminders = new ArrayList<>();
        // sort indexes in reverse order to prevent index mashups
        List<Integer> sortedIndexes = indexes.stream().sorted(Comparator.reverseOrder()).toList();
        for (Integer index : sortedIndexes) {
            reminders.add(reminderList.get(index));
            reminderList.remove(index.intValue());
        }
        for (Reminder reminder : reminders) {
            assert (!reminderList.contains(reminder));
            logger.log(Level.INFO, "Deleted: " + reminder.toString());
        }
        MessageHandler.sendDeleteReminderMessage(reminders, reminderList.size());
    }

    public synchronized void turnOnReminders(LinkedHashSet<Integer> indexes) {
        ArrayList<Reminder> isTurnOnReminders = new ArrayList<>();
        ArrayList<Reminder> alreadyTurnOnReminders = new ArrayList<>();
        // sort indexes in reverse order to prevent index mashups
        List<Integer> sortedIndexes = indexes.stream().sorted(Comparator.reverseOrder()).toList();
        for (Integer index : sortedIndexes) {
            Reminder reminder = reminderList.get(index);
            if (!reminder.getOnReminder()) { //Has not been turned on
                reminder.setOnReminder(true);
                isTurnOnReminders.add(reminder);
            } else { //already turned on
                alreadyTurnOnReminders.add(reminder);
            }
            assert (reminder.getOnReminder());
            logger.log(Level.INFO, "Turned on: " + reminder);
        }
        if (!isTurnOnReminders.isEmpty()) {
            MessageHandler.sendIsTurnOnReminderMessage(isTurnOnReminders);
        }
        if (!alreadyTurnOnReminders.isEmpty()) {
            MessageHandler.sendAlreadyTurnOnReminderMessage(alreadyTurnOnReminders);
        }
    }

    public synchronized void turnOffReminders(LinkedHashSet<Integer> indexes) {
        ArrayList<Reminder> isTurnOffReminders = new ArrayList<>();
        ArrayList<Reminder> alreadyTurnOffReminders = new ArrayList<>();
        // sort indexes in reverse order to prevent index mashups
        List<Integer> sortedIndexes = indexes.stream().sorted(Comparator.reverseOrder()).toList();
        for (Integer index : sortedIndexes) {
            Reminder reminder = reminderList.get(index);
            if (reminder.getOnReminder()) { //Has not been turned off
                reminder.setOnReminder(false);
                isTurnOffReminders.add(reminder);
            } else { //already turned on
                alreadyTurnOffReminders.add(reminder);
            }
            assert (!reminder.getOnReminder());
            logger.log(Level.INFO, "Turned off: " + reminder);
        }
        if (!isTurnOffReminders.isEmpty()) {
            MessageHandler.sendIsTurnOffReminderMessage(isTurnOffReminders);
        }
        if (!alreadyTurnOffReminders.isEmpty()) {
            MessageHandler.sendAlreadyTurnOffReminderMessage(alreadyTurnOffReminders);
        }
    }

    public synchronized void handleSnooze(int index, Duration snoozeDuration) {
        Reminder reminder = reminderList.get(index);
        if (reminder.isRecurring()) { //Reminder is recurring, and cannot be snoozed
            MessageHandler.sendRecUnableToSnoozeError(reminder);
            return;
        }
        try {
            reminder.snooze(snoozeDuration);
            MessageHandler.sendSnoozeMessage(reminder);
        } catch (StudyMateException e) {
            System.out.println(e.getMessage());
        }
    }

    public synchronized List<Reminder> getReminders() {
        return new ArrayList<>(reminderList);
    }

    public synchronized int getReminderIndex(Reminder r) {
        return reminderList.indexOf(r);
    }
}
