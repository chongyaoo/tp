package seedu.studymate.reminders;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageHandler;

/**
 * Represents a list of tasks
 * It provides methods for adding, deleting, marking, and unmarking tasks
 */
public class ReminderList {
    private static final Logger logger = Logger.getLogger("TaskList Logger");
    private final ArrayList<Reminder> reminderList;

    /**
     * Constructs an empty TaskList
     */
    public ReminderList() {
        reminderList = new ArrayList<>();
    }

    public synchronized void addReminderRec(String name, DateTimeArg dateTime, Duration interval) {
        Reminder newReminder = new Reminder(name, dateTime, interval);
        reminderList.add(newReminder);
        assert(reminderList.contains(newReminder));
    }

    public synchronized void addReminderOneTime(String name, DateTimeArg dateTime) {
        Reminder newReminder = new Reminder(name, dateTime);
        reminderList.add(newReminder);
        assert(reminderList.contains(newReminder));
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
        for (Integer index: sortedIndexes) {
            reminders.add(reminderList.get(index));
            reminderList.remove(index.intValue());
        }
        for (Reminder reminder : reminders) {
            assert (!reminderList.contains(reminder));
            logger.log(Level.INFO, "Deleted: " + reminder.toString());
        }
        MessageHandler.sendDeleteReminderMessage(reminders, reminderList.size());
    }

    public synchronized List<Reminder> getReminders() {
        return reminderList;
    }

}
