package seedu.studymate.tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageHandler;

/**
 * Represents a list of tasks
 * It provides methods for adding, deleting, marking, and unmarking tasks
 */
public class ReminderList {

    private final ArrayList<Reminder> reminderList;

    /**
     * Constructs an empty TaskList
     */
    public ReminderList() {
        reminderList = new ArrayList<>();
    }

    public void addReminder(String name, DateTimeArg dateTime) {
        Reminder newReminder = new Reminder(name, dateTime);
        reminderList.add(newReminder);
        //MessageHandler.sendAddTaskMessage(newTask, getCount());
    }

    public int getCount() {
        return reminderList.size();
    }

    public Reminder getReminder(int index) {
        return reminderList.get(index);
    }

    public void delete(LinkedHashSet<Integer> indexes) {
        ArrayList<Reminder> reminders = new ArrayList<>();
        // sort indexes in reverse order to prevent index mashups
        List<Integer> sortedIndexes = indexes.stream().sorted(Comparator.reverseOrder()).toList();
        for (Integer index: sortedIndexes) {
            reminders.add(reminderList.get(index));
            reminderList.remove(index.intValue());
        }
        MessageHandler.sendDeleteReminderMessage(reminders, reminderList.size());
    }

    public List<Reminder> getReminders() {
        return reminderList;
    }

}
