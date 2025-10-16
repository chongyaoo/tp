package seedu.studymate.tasks;

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
public class TaskList {

    private final ArrayList<Task> taskList;
    private final static Logger logger = Logger.getLogger("TaskList Logger");

    /**
     * Constructs an empty TaskList
     */
    public TaskList() {
        taskList = new ArrayList<>();
        logger.log(Level.INFO, "Created tasklist");
    }

    /**
     * Retrieves a task at a specific index
     *
     * @param index The index of the task to retrieve
     * @return The task at the specified index
     */
    public Task getTask(int index) {
        logger.log(Level.INFO, "Task retrieved: " + taskList.get(index).toString());
        return taskList.get(index);
    }

    /**
     * Returns the total number of tasks in the list
     *
     * @return The number of tasks
     */
    public int getCount() {
        logger.log(Level.INFO, "Task list count retrieved: " + taskList.size());
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
        assert (taskList.contains(newTask));
        logger.log(Level.INFO, "Added To Do: " + newTask.toString());
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
        assert (taskList.contains(newTask));
        logger.log(Level.INFO, "Added Deadline: " + newTask.toString());
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
        for (Integer index : sortedIndexes) {
            tasks.add(taskList.get(index));
            taskList.remove(index.intValue());
        }
        int i = 0;
        for (Integer index : sortedIndexes) {
            assert (!taskList.contains(tasks.get(i)));
            logger.log(Level.INFO, "Deleted: " + tasks.get(i).toString());
            i += 1;
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
        for (Integer index : indexes) {
            taskList.get(index).setDone(true);
            assert(taskList.get(index).getDone());
            logger.log(Level.INFO, "Marked task: " + taskList.get(index).toString());
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
        for (Integer index : indexes) {
            taskList.get(index).setDone(false);
            assert(!taskList.get(index).getDone());
            logger.log(Level.INFO, "Unmarked task: " + taskList.get(index).toString());
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
        logger.log(Level.INFO, "Task List Retrieved");
        return taskList;
    }
}
