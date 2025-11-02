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
 * Manages collection of reminders with thread-safe operations.
 * Supports adding, deleting, activating/deactivating, and snoozing reminders.
 * Maximum capacity: 10,000 reminders.
 */

public class ReminderList {
    private static final Logger logger = Logger.getLogger("TaskList Logger");
    private static final int cap = 10000;
    private final ArrayList<Reminder> reminderList;
    private final Clock clock;

    /**
     * Constructs ReminderList with specified clock.
     *
     * @param clock Clock for creating reminders
     */
    public ReminderList(Clock clock) {
        reminderList = new ArrayList<>();
        this.clock = clock;
    }

    /**
     * Constructs ReminderList with system default clock.
     */
    public ReminderList() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Adds a recurring reminder.
     *
     * @param name Reminder description
     * @param dateTime When to first fire
     * @param interval Duration between firings
     * @throws StudyMateException if capacity reached
     */
    public synchronized void addReminderRec(String name, DateTimeArg dateTime, Duration interval)
            throws StudyMateException {
        Reminder newReminder = new Reminder(name, dateTime, interval, clock);
        if (reminderList.size() >= cap) {
            throw new StudyMateException("Too many reminders! Please delete some to add in more.");
        }
        reminderList.add(newReminder);
        assert (reminderList.contains(newReminder));
    }

    /**
     * Adds a one-time reminder with specified fired status.
     * Used when loading from storage.
     *
     * @param name Reminder description
     * @param dateTime When to fire
     * @param isFired Whether already fired
     * @throws StudyMateException if capacity reached
     */
    public synchronized void addReminderOneTime(String name, DateTimeArg dateTime, boolean isFired)
            throws StudyMateException {
        Reminder newReminder = new Reminder(name, dateTime, clock, isFired);
        if (reminderList.size() >= cap) {
            throw new StudyMateException("Too many reminders! Please delete some to add in more.");
        }
        reminderList.add(newReminder);
        assert (reminderList.contains(newReminder));
    }

    /**
     * Adds a one-time reminder (unfired).
     *
     * @param name Reminder description
     * @param dateTime When to fire
     * @throws StudyMateException if capacity reached
     */
    public void addReminderOneTime(String name, DateTimeArg dateTime) throws StudyMateException {
        addReminderOneTime(name, dateTime, false);
    }

    /**
     * Returns total number of reminders.
     *
     * @return Reminder count
     */
    public synchronized int getCount() {
        return reminderList.size();
    }

    /**
     * Retrieves reminder at specified index.
     *
     * @param index Zero-based position
     * @return The reminder
     */
    public synchronized Reminder getReminder(int index) {
        return reminderList.get(index);
    }

    /**
     * Indicates this is a recurring schedule.
     *
     * @return Always true
     */
    public boolean isRecurring() {
        return true;
    }

    /**
     * Deletes reminders at specified indexes.
     * Processes in reverse order to prevent index shifting issues.
     * Sends notifications via MessageHandler.
     *
     * @param indexes Positions to delete
     */
    public synchronized void delete(LinkedHashSet<Integer> indexes) {
        ArrayList<Reminder> reminders = new ArrayList<>();
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

    /**
     * Activates reminders at specified indexes.
     * Separates newly-activated and already-active reminders for appropriate messaging.
     *
     * @param indexes Positions to activate
     */
    public synchronized void turnOnReminders(LinkedHashSet<Integer> indexes) {
        ArrayList<Reminder> isTurnOnReminders = new ArrayList<>();
        ArrayList<Reminder> alreadyTurnOnReminders = new ArrayList<>();
        List<Integer> sortedIndexes = indexes.stream().sorted(Comparator.reverseOrder()).toList();
        for (Integer index : sortedIndexes) {
            Reminder reminder = reminderList.get(index);
            if (!reminder.getOnReminder()) {
                reminder.setOnReminder(true);
                isTurnOnReminders.add(reminder);
            } else {
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

    /**
     * Deactivates reminders at specified indexes.
     * Separates newly-deactivated and already-inactive reminders for appropriate messaging.
     *
     * @param indexes Positions to deactivate
     */
    public synchronized void turnOffReminders(LinkedHashSet<Integer> indexes) {
        ArrayList<Reminder> isTurnOffReminders = new ArrayList<>();
        ArrayList<Reminder> alreadyTurnOffReminders = new ArrayList<>();
        List<Integer> sortedIndexes = indexes.stream().sorted(Comparator.reverseOrder()).toList();
        for (Integer index : sortedIndexes) {
            Reminder reminder = reminderList.get(index);
            if (reminder.getOnReminder()) {
                reminder.setOnReminder(false);
                isTurnOffReminders.add(reminder);
            } else {
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

    /**
     * Snoozes reminder at specified index.
     * Only works for one-time reminders. Sends error for recurring reminders.
     *
     * @param index Position
     * @param snoozeDuration Duration to delay by
     */
    public synchronized void handleSnooze(int index, Duration snoozeDuration) {
        Reminder reminder = reminderList.get(index);
        if (reminder.isRecurring()) {
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

    /**
     * Returns defensive copy of all reminders.
     *
     * @return List of all reminders
     */
    public synchronized List<Reminder> getReminders() {
        return new ArrayList<>(reminderList);
    }

    /**
     * Finds index of specified reminder.
     *
     * @param r The reminder to find
     * @return Zero-based index, or -1 if not found
     */
    public synchronized int getReminderIndex(Reminder r) {
        return reminderList.indexOf(r);
    }
}