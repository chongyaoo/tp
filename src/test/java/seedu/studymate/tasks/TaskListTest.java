package seedu.studymate.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.studymate.parser.DateTimeArg;

public class TaskListTest {
    TaskList taskList;

    @BeforeEach
    public void setup() {
        taskList = new TaskList();
    }

    // --- Test Cases for Adding Tasks ---

    // Test Adding To Do Task
    @Test
    void testAddToDo() {
        taskList.addToDo("read book");
        assertEquals(1, taskList.getCount());
        Task task = taskList.getTask(0);
        assertInstanceOf(ToDo.class, task);
        assertEquals("read book", task.getName());
        assertFalse(task.getDone());
    }

    // Test Adding Deadline Task
    @Test
    void testAddDeadline() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg deadlineTime = new DateTimeArg(date, time);

        taskList.addDeadline("submit report", deadlineTime);
        assertEquals(1, taskList.getCount());
        Task task = taskList.getTask(0);
        assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", task.getName());
        assertFalse(task.getDone());
        Deadline deadlineTask = (Deadline) task;
        assertEquals(deadlineTime, deadlineTask.getDeadline());

    }

    // --- Test Cases for Mark/Unmark ---

    // Helper Function to test marking/unmarking
    private void populateListForMarking() {
        taskList.addToDo("Task 1");
        taskList.addToDo("Task 2");
        taskList.addToDo("Task 3");
    }

    // Test marking 1 task
    @Test
    void testMark() {
        populateListForMarking();

        // Mark Task 2
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>(Collections.singletonList(1));
        taskList.mark(indexes);

        assertTrue(taskList.getTask(1).getDone());
        assertFalse(taskList.getTask(0).getDone());
        assertFalse(taskList.getTask(2).getDone());
    }

    // Test marking 2 tasks
    @Test
    void testMarkMultiple() {
        populateListForMarking();

        // Mark Task 1 and Task 3
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        indexes.add(2);
        taskList.mark(indexes);

        assertTrue(taskList.getTask(0).getDone());
        assertFalse(taskList.getTask(1).getDone());
        assertTrue(taskList.getTask(2).getDone());
    }

    // Test unmarking 1 task
    @Test
    void testUnmark() {
        populateListForMarking();

        // Mark all tasks first
        LinkedHashSet<Integer> allIndexes = new LinkedHashSet<>();
        allIndexes.add(0);
        allIndexes.add(1);
        allIndexes.add(2);
        taskList.mark(allIndexes);
        assertTrue(taskList.getTask(1).getDone());

        // Unmark Task 2 (index 1)
        LinkedHashSet<Integer> unmarkIndexes = new LinkedHashSet<>(Collections.singletonList(1));
        taskList.unmark(unmarkIndexes);

        assertFalse(taskList.getTask(1).getDone());
        assertTrue(taskList.getTask(0).getDone()); // Check others remain marked
    }

    // --- Test Cases for Deletion ---

    // Helper function to populate a list to test for deletion
    private void populateListForDeletion() {
        taskList.addToDo("Task A"); // 0
        taskList.addToDo("Task B"); // 1
        taskList.addToDo("Task C"); // 2
        taskList.addToDo("Task D"); // 3
    }

    // Test deleting 1 task
    @Test
    void testDeleteSingleTask() {
        populateListForDeletion();
        assertEquals(4, taskList.getCount());

        // Delete Task C
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>(Collections.singletonList(2));
        taskList.delete(indexes);

        assertEquals(3, taskList.getCount());
        assertEquals("Task A", taskList.getTask(0).getName());
        assertEquals("Task B", taskList.getTask(1).getName()); // Task B remains at index 1
        assertEquals("Task D", taskList.getTask(2).getName()); // Task D shifts from 3 to 2
    }

    // Test deleting 2 tasks
    @Test
    void testDeleteMultipleTasks() {
        populateListForDeletion();

        // Delete Task B and Task D
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(1);
        indexes.add(3);
        taskList.delete(indexes);

        assertEquals(2, taskList.getCount());
        assertEquals("Task A", taskList.getTask(0).getName());
        assertEquals("Task C", taskList.getTask(1).getName()); // Task C shifts from 2 to 1
    }

    // --- Test Cases for Getters ---

    // Test getting empty list count
    @Test
    void testGetCountEmpty() {
        assertEquals(0, taskList.getCount());
    }

    // Test getting taskList count
    @Test
    void testGetTasks() {
        populateListForDeletion();
        ArrayList<Task> tasks = taskList.getTasks();
        assertEquals(4, tasks.size());
        assertEquals("Task A", tasks.get(0).getName());
    }
}
