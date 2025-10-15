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
    }

    /**
     * Adds a Deadline task to the list
     *
     * @param task     The description of the deadline
     * @param deadline The deadline time
     */
    public void addDeadline(String task, DateTimeArg deadline) {
        Deadline newTask = new Deadline(task, deadline);
        taskList.add(newTask);
    }

    /**
     * Deletes a task from the list at a specific task number
     *
     * @param indexes The list of task indexes to delete
     */
    public void delete(LinkedHashSet<Integer> indexes) {
        ArrayList<Task> tasks = new ArrayList<>();
        // sort indexes in reverse order to prevent index mashups
        List<Integer> sortedIndexes = indexes.stream().sorted(Comparator.reverseOrder()).toList();
        for (Integer index: sortedIndexes) {
            tasks.add(taskList.get(index));
            taskList.remove(index.intValue());
        }
        MessageHandler.sendDeleteTaskMessage(tasks, taskList.size());
    }

    /**
     * Marks a task as done
     *
     * @param indexes The list of task indexes to mark as done
     */
    public void mark(LinkedHashSet<Integer> indexes) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer index:indexes) {
            taskList.get(index).setDone(true);
            tasks.add(taskList.get(index));
        }
        MessageHandler.sendMarkMessage(tasks);
    }

    /**
     * Unmarks a task as not done
     *
     * @param indexes The list of task indexes to mark as not done
     */
    public void unmark(LinkedHashSet<Integer> indexes) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer index:indexes) {
            taskList.get(index).setDone(false);
            tasks.add(taskList.get(index));
        }
        MessageHandler.sendUnmarkMessage(tasks);
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
