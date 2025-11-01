package seedu.studymate.tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageHandler;
import seedu.studymate.exceptions.StudyMateException;

/**
 * Represents a list of tasks
 * It provides methods for adding, deleting, marking, and unmarking tasks
 */
public class TaskList {
    private static final Logger logger = Logger.getLogger("TaskList Logger");
    private static final int cap = 10000;
    private final ArrayList<Task> taskList;

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
    public void addToDo(String task) throws StudyMateException {
        ToDo newTask = new ToDo(task);
        if (taskList.size() >= cap) {
            throw new StudyMateException("Too many tasks! Please delete some to add in more.");
        }
        taskList.add(newTask);
        assert (taskList.contains(newTask));
        logger.log(Level.INFO, "Added To Do: " + newTask);
    }

    /**
     * Adds a Deadline task to the list
     *
     * @param task     The description of the deadline
     * @param deadline The deadline time
     */
    public void addDeadline(String task, DateTimeArg deadline) throws StudyMateException {
        Deadline newTask = new Deadline(task, deadline);
        if (taskList.size() >= cap) {
            throw new StudyMateException("Too many tasks! Please delete some to add in more.");
        }
        taskList.add(newTask);
        assert (taskList.contains(newTask));
        logger.log(Level.INFO, "Added Deadline: " + newTask);
    }

    /**
     * Adds an Event task to the list
     *
     * @param task The description of the deadline
     * @param from The DateTimeArg from
     * @param to The DateTimeArg to
     */
    public void addEvent(String task, DateTimeArg from, DateTimeArg to) throws StudyMateException {
        Event newTask = new Event(task, from, to);
        if (taskList.size() >= cap) {
            throw new StudyMateException("Too many tasks! Please delete some to add in more.");
        }
        taskList.add(newTask);
        assert (taskList.contains(newTask));
        logger.log(Level.INFO, "Added Event: " + newTask);
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
        for (Task task : tasks) {
            assert (!taskList.contains(task));
            logger.log(Level.INFO, "Deleted: " + task);
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
     * @param subStr the substring to search by
     * @return An ArrayList containing all Task objects in the list.
     */
    public ArrayList<Task> findTasks(String subStr) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : taskList) {
            if (task.getName().contains(subStr)) {
                result.add(task);
            }
        }
        return result;
    }

    /**
     * Returns the list of all tasks that matches given substring.
     *
     * @return An ArrayList containing all Task objects in the list.
     */
    public ArrayList<Task> getTasks() {
        logger.log(Level.INFO, "Task List Retrieved");
        return taskList;
    }

    /**
     * Returns a sorted list of deadlines and events by date.
     * Deadlines are sorted by their deadline date, events by their from date.
     * Sorting is from soonest to furthest.
     *
     * @return An ArrayList containing Deadline and Event tasks sorted by date
     */
    public ArrayList<Task> getSorted() {
        ArrayList<Task> result = new ArrayList<>();

        // Collect only deadlines and events
        for (Task task : taskList) {
            if (task instanceof Deadline || task instanceof Event) {
                result.add(task);
            }
        }

        // Sort by date - soonest first
        result.sort((task1, task2) -> {
            DateTimeArg date1 = getTaskDate(task1);
            DateTimeArg date2 = getTaskDate(task2);
            return date1.compareTo(date2);
        });

        logger.log(Level.INFO, "Sorted task list retrieved with " + result.size() + " tasks");
        return result;
    }

    /**
     * Helper method to extract the date from a task for sorting purposes.
     * Returns deadline date for Deadline tasks, from date for Event tasks.
     *
     * @param task The task to extract date from
     * @return The DateTimeArg to use for sorting, or null if not a Deadline/Event
     */
    private DateTimeArg getTaskDate(Task task) {
        if (task instanceof Deadline) {
            return ((Deadline) task).getDeadline();
        } else if (task instanceof Event) {
            return ((Event) task).getFrom();
        }
        return null;
    }

    /**
     * Edits the description of a task at the specified index.
     *
     * @param index The index of the task to edit
     * @param newDesc The new description for the task
     */
    public void editDesc(int index, String newDesc) {
        Task task = taskList.get(index);
        task.setName(newDesc);
        logger.log(Level.INFO, "Edited description of task at index " + index + " to: " + newDesc);
        MessageHandler.sendEditDescMessage(task);
    }

    /**
     * Edits the deadline of a Deadline task at the specified index.
     *
     * @param index The index of the task to edit
     * @param newDeadline The new deadline for the task
     * @throws seedu.studymate.exceptions.StudyMateException If the task is not a Deadline
     */
    public void editDeadline(int index, DateTimeArg newDeadline) throws StudyMateException {
        Task task = taskList.get(index);
        if (!(task instanceof Deadline)) {
            throw new seedu.studymate.exceptions.StudyMateException("Task is not a deadline!");
        }
        ((Deadline) task).setDeadline(newDeadline);
        logger.log(Level.INFO, "Edited deadline of task at index " + index + " to: " + newDeadline);
        MessageHandler.sendEditDeadlineMessage(task, newDeadline);
    }

    /**
     * Edits the from date of an Event task at the specified index.
     *
     * @param index The index of the task to edit
     * @param newFrom The new from date for the event
     * @throws seedu.studymate.exceptions.StudyMateException If the task is not an Event
     */
    public void editFrom(int index, DateTimeArg newFrom) throws StudyMateException {
        Task task = taskList.get(index);
        if (!(task instanceof Event)) {
            throw new seedu.studymate.exceptions.StudyMateException("Task is not an event!");
        }
        ((Event) task).setFrom(newFrom);
        logger.log(Level.INFO, "Edited from date of task at index " + index + " to: " + newFrom);
        MessageHandler.sendEditFromMessage(task, newFrom);
    }

    /**
     * Edits the to date of an Event task at the specified index.
     *
     * @param index The index of the task to edit
     * @param newTo The new to date for the event
     * @throws seedu.studymate.exceptions.StudyMateException If the task is not an Event
     */
    public void editTo(int index, DateTimeArg newTo) throws StudyMateException {
        Task task = taskList.get(index);
        if (!(task instanceof Event)) {
            throw new seedu.studymate.exceptions.StudyMateException("Task is not an event!");
        }
        ((Event) task).setTo(newTo);
        logger.log(Level.INFO, "Edited to date of task at index " + index + " to: " + newTo);
        MessageHandler.sendEditToMessage(task, newTo);
    }
}
