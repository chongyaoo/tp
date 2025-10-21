package seedu.studymate.ui;

import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.reminders.Reminder;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all messages and interactions with the user interface by printing to the console
 * This class provides a centralised way to display information
 * such as task lists, status updates, and error messages
 */
public class MessageHandler {
    private static final String LINE = "____________________________________________________________";

    /**
     * Prints a series of messages, enclosed within a decorative line
     *
     * @param messages An array of strings to be printed
     */
    public static void sendMessage(String... messages) {
        System.out.println(LINE);
        for (String message : messages) {
            System.out.println(message);
        }
        System.out.println(LINE);
    }

    /**
     * Prints the task list
     *
     * @param taskList The TaskList object to be printed
     */
    public static void sendTaskList(TaskList taskList) {
        if (taskList.getCount() == 0) {
            sendMessage("Task list is empty!");
            return;
        }
        System.out.println(LINE);
        System.out.println("Here are the tasks in your task list:");
        for (int i = 0; i < taskList.getCount(); i++) {
            System.out.println((i + 1) + ". " + taskList.getTask(i).toString());
        }
        System.out.println(LINE);
    }

    /**
     * Prints the sorted task list
     *
     * @param taskList The TaskList object to be printed
     */
    public static void sendSortedTaskList(ArrayList<Task> taskList) {
        if (taskList.isEmpty()) {
            sendMessage("Task list has no deadlines or events!");
            return;
        }
        System.out.println(LINE);
        System.out.println("Here are the deadlines and events in your task list," +
                " sorted by their deadlines and/or start times:");
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println((i + 1) + ". " + taskList.get(i).toString());
        }
        System.out.println(LINE);
    }

    /**
     * Prints the resulting tasks found
     *
     * @param taskList The TaskList object to be printed
     */
    public static void sendFindResults(ArrayList<Task> taskList) {
        if (taskList.isEmpty()) {
            sendMessage("No results found!");
            return;
        }
        System.out.println(LINE);
        System.out.println("Here are the tasks with the matching substring found!:");
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println((i + 1) + ". " + taskList.get(i).toString());
        }
        System.out.println(LINE);
    }

    /**
     * Prints the Reminder list
     *
     * @param reminderList The TaskList object to be printed
     */
    public static void sendReminderList(ReminderList reminderList) {
        if (reminderList.getCount() == 0) {
            sendMessage("Reminders list is empty!");
            return;
        }
        System.out.println(LINE);
        System.out.println("Here are your Reminders:");
        for (int i = 0; i < reminderList.getCount(); i++) {
            System.out.println((i + 1) + ". " + reminderList.getReminder(i).toString());
        }
        System.out.println(LINE);
    }

    /**
     * Prints a confirmation message after a task has been added
     *
     * @param task  The task that was added
     * @param count The current number of tasks in the list
     */
    public static void sendAddTaskMessage(Task task, int count) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println(task.toString());
        if (count == 1) {
            System.out.println("Now you have 1 task in the task list.");
        } else {
            System.out.println("Now you have " + count + " tasks in the task list.");
        }
        System.out.println(LINE);
    }

    /**
     * Prints a confirmation message after a task has been deleted
     *
     * @param tasks The list of tasks that was deleted
     * @param count The current number of tasks in the list
     */
    public static void sendDeleteTaskMessage(List<Task> tasks, int count) {
        System.out.println(LINE);
        System.out.println("Got it. I've deleted these tasks:");
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
        if (count == 1) {
            System.out.println("Now you have 1 task in the task list.");
        } else {
            System.out.println("Now you have " + count + " tasks in the task list.");
        }
        System.out.println(LINE);
    }

    /**
     * Prints a confirmation message after a reminder has been deleted
     *
     * @param reminders The list of tasks that was deleted
     * @param count     The current number of tasks in the list
     */
    public static void sendDeleteReminderMessage(List<Reminder> reminders, int count) {
        System.out.println(LINE);
        System.out.println("Got it. I've deleted these reminders:");
        for (Reminder reminder : reminders) {
            System.out.println(reminder.toString());
        }
        if (count == 1) {
            System.out.println("Now you have 1 reminder in the Reminders list.");
        } else {
            System.out.println("Now you have " + count + " reminders in the Reminders list.");
        }
        System.out.println(LINE);
    }

    /**
     * Prints a message to confirm that a task has been marked as done
     *
     * @param tasks The list of tasks that was marked
     */
    public static void sendMarkMessage(List<Task> tasks) {
        System.out.println(LINE);
        System.out.println("Nice! I've marked these tasks as done:");
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
        System.out.println(LINE);
    }

    /**
     * Prints a message to confirm that a task has been unmarked
     *
     * @param tasks The list of task that was unmarked
     */
    public static void sendUnmarkMessage(List<Task> tasks) {
        System.out.println(LINE);
        System.out.println("OK, I've marked these tasks as not done yet:");
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
        System.out.println(LINE);
    }

    /**
     * Prints a message to confirm that a task has been unmarked
     *
     * @param task The task who had its description edited
     */
    public static void sendEditDescMessage(Task task) {
        System.out.println(LINE);
        System.out.println("OK, I've edited the description of the task to:");
        System.out.println(task);
        System.out.println(LINE);
    }

    /**
     * Prints a message to confirm that a task has been unmarked
     *
     * @param task The task who had its description edited
     */
    public static void sendEditDeadlineMessage(Task task, DateTimeArg dateTimeArg) {
        System.out.println(LINE);
        System.out.println("OK, I've edited the deadline of the deadline " + task.getName() + "to:");
        System.out.println(dateTimeArg);
        System.out.println(LINE);
    }

    /**
     * Prints a message to confirm that a task has been unmarked
     *
     * @param task The task who had its description edited
     */
    public static void sendEditFromMessage(Task task, DateTimeArg dateTimeArg) {
        System.out.println(LINE);
        System.out.println("OK, I've edited the from date of the event " + task.getName() + "to:");
        System.out.println(dateTimeArg);
        System.out.println(LINE);
    }

    /**
     * Prints a message to confirm that a task has been unmarked
     *
     * @param task The task who had its description edited
     */
    public static void sendEditToMessage(Task task, DateTimeArg dateTimeArg) {
        System.out.println(LINE);
        System.out.println("OK, I've edited the to date of the event " + task.getName() + "to:");
        System.out.println(dateTimeArg);
        System.out.println(LINE);
    }

    /**
     * Prints a confirmation message after a recurring Reminder has been added
     *
     * @param reminder  The task that was added
     * @param count The current number of reminders in the list
     */
    public static void sendAddReminderRecMessage(Reminder reminder, int count) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this recurring reminder:");
        System.out.println(reminder.toString());
        if (count == 1) {
            System.out.println("Now you have 1 reminder in the reminder list.");
        } else {
            System.out.println("Now you have " + count + " reminders in the reminder list.");
        }
        System.out.println(LINE);
    }

    /**
     * Prints a confirmation message after a recurring Reminder has been added
     *
     * @param reminder  The task that was added
     * @param count The current number of reminders in the list
     */
    public static void sendAddReminderOneTimeMessage(Reminder reminder, int count) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this One-Time reminder:");
        System.out.println(reminder.toString());
        if (count == 1) {
            System.out.println("Now you have 1 reminder in the reminder list.");
        } else {
            System.out.println("Now you have " + count + " reminders in the reminder list.");
        }
        System.out.println(LINE);
    }

    public static void sendRemindersDone(List<Reminder> reminders) {
        System.out.println(LINE);
        System.out.println("The following reminders are done!");
        for (Reminder reminder : reminders) {
            System.out.println(reminder);
        }
        System.out.println(LINE);
    }

    // TODO
    public static void sendTimerStartMessage(int duration, String label) {
        System.out.println(LINE);
        String formattedTime = formatDurationString(duration * 60L); // Convert minutes to seconds
        String output = "# TIMER\n" + "# RUNNING " + formattedTime + " left - " + label;
        System.out.println(output);
        System.out.println(LINE);
    }

    public static void sendTimerPauseMessage(long remainingTime, String label) {
        System.out.println(LINE);
        String formattedTime = formatDurationString(remainingTime);
        String output = "# TIMER\n" + "# PAUSED " + formattedTime + " left - " + label;
        System.out.println(output);
        System.out.println(LINE);
    }

    public static void sendTimerResumeMessage(long remainingTime, String label) {
        System.out.println(LINE);
        String formattedTime = formatDurationString(remainingTime);
        String output = "# TIMER\n" + "# RUNNING " + formattedTime + " left - " + label;
        System.out.println(output);
        System.out.println(LINE);
    }

    public static void sendTimerResetMessage() {
        System.out.println(LINE);
        System.out.println("# TIMER\n" + "# RESET TIMER");
        System.out.println(LINE);
    }

    public static void sendTimerStatMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

    public static void sendTimerEndedMessage() {
        System.out.println(LINE);
        System.out.println("# TIMER\n" + "# TIMER HAS ENDED");
        System.out.println(LINE);
    }

    private static String formatDurationString(long totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        return String.format("%d:%02d", minutes, seconds);
    }
}
