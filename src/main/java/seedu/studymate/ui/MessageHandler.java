package seedu.studymate.ui;

import seedu.studymate.tasks.TaskList;
import seedu.studymate.tasks.Task;

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
     * Prints the task lis
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
     * @param task The task that was deleted
     * @param count The current number of tasks in the list
     */
    public static void sendDeleteTaskMessage(Task task, int count) {
        System.out.println(LINE);
        System.out.println("Got it. I've deleted this task:");
        System.out.println(task.toString());
        if (count == 1) {
            System.out.println("Now you have 1 task in the task list.");
        } else {
            System.out.println("Now you have " + count + " tasks in the task list.");
        }
        System.out.println(LINE);
    }

    /**
     * Prints a message to confirm that a task has been marked as done
     *
     * @param task The task that was marked
     */
    public static void sendMarkMessage(Task task) {
        System.out.println(LINE);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task.toString());
        System.out.println(LINE);
    }

    /**
     * Prints a message to confirm that a task has been unmarked
     *
     * @param task The task that was unmarked
     */
    public static void sendUnmarkMessage(Task task) {
        System.out.println(LINE);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(task.toString());
        System.out.println(LINE);
    }
}
