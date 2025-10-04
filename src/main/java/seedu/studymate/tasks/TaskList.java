package seedu.studymate.tasks;

import java.util.ArrayList;

import seedu.studymate.ui.MessageHandler;

/**
 * Represents a list of tasks
 * It provides methods for adding, deleting, marking, and unmarking tasks
 */
public class TaskList {

    private final ArrayList<Task> taskList;

    /**
     * Constructs an empty TaskList
     */
    public TaskList() {
        taskList = new ArrayList<>();
    }

    /**
     * Retrieves a task at a specific index
     *
     * @param index The index of the task to retrieve
     * @return The task at the specified index
     */
    public Task getTask(int index) {
        return taskList.get(index);
    }

    /**
     * Returns the total number of tasks in the list
     *
     * @return The number of tasks
     */
    public int getCount() {
        return taskList.size();
    }

    /**
     * Adds a to-do task to the list
     *
     * @param task The description of the to-do
     */
    public void addToDo(String task) {
        ToDo newTask = new ToDo(task);
        taskList.add(newTask);
        MessageHandler.sendAddTaskMessage(newTask, getCount());
    }

    /**
     * Adds a Deadline task to the list
     *
     * @param task     The description of the deadline
     * @param deadline The deadline time
     */
    public void addDeadline(String task, String deadline) {
        Deadline newTask = new Deadline(task, deadline);
        taskList.add(newTask);
        MessageHandler.sendAddTaskMessage(newTask, getCount());
    }

    /**
     * Deletes a task from the list at a specific task number
     *
     * @param taskNumber The number of the task to delete
     */
    public void delete(int taskNumber) {
        Task deletedTask = taskList.get(taskNumber - 1);
        taskList.remove(taskNumber - 1);
        MessageHandler.sendDeleteTaskMessage(deletedTask, getCount());
    }

    /**
     * Marks a task as done
     *
     * @param taskNumber The number of the task to mark
     */
    public void mark(int taskNumber) {
        taskList.get(taskNumber - 1).setDone(true);
        MessageHandler.sendMarkMessage(taskList.get(taskNumber - 1));
    }

    /**
     * Unmarks a task as not done
     *
     * @param taskNumber The number of the task to unmark
     */
    public void unmark(int taskNumber) {
        taskList.get(taskNumber - 1).setDone(false);
        MessageHandler.sendUnmarkMessage(taskList.get(taskNumber - 1));
    }

    /**
     * Returns the list of all tasks in this TaskList.
     *
     * @return An ArrayList containing all Task objects in the list.
     */
    public ArrayList<Task> getTasks() {
        return taskList;
    }

}
