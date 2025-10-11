package seedu.studymate.ui;

import seedu.studymate.tasks.Reminder;
import seedu.studymate.tasks.ReminderList;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.tasks.Task;

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
            System.out.println((i+1) + ". " + taskList.getTask(i).toString());
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
            System.out.println((i+1) + ". " + reminderList.getReminder(i).toString());
        }
        System.out.println(LINE);
    }

    /**
     * Prints a confirmation message after a task has been added
     *
     * @param task The task that was added
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
        for (Task task: tasks) {
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
     * @param count The current number of tasks in the list
     */
    public static void sendDeleteReminderMessage(List<Reminder> reminders, int count) {
        System.out.println(LINE);
        System.out.println("Got it. I've deleted these reminders:");
        for (Reminder reminder: reminders) {
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
        for (Task task: tasks) {
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
        for (Task task: tasks) {
            System.out.println(task.toString());
        }
        System.out.println(LINE);
    }

    // TODO
    public static void sendTimerStartMessage() {
        System.out.println(LINE);
        System.out.println("Timer start");
        System.out.println(LINE);
    }

    public static void sendTimerPauseMessage() {
        System.out.println(LINE);
        System.out.println("Timer pause");
        System.out.println(LINE);
    }

    public static void sendTimerResumeMessage() {
        System.out.println(LINE);
        System.out.println("Timer resume");
        System.out.println(LINE);
    }

    public static void sendTimerResetMessage() {
        System.out.println(LINE);
        System.out.println("Timer reset");
        System.out.println(LINE);
    }

    public static void sendTimerStatMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

    public static void sendTimerEndedMessage() {
        System.out.println(LINE);
        System.out.println("Timer ended");
        System.out.println(LINE);
    }
}
