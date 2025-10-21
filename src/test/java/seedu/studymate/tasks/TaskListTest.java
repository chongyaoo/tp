package seedu.studymate.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    // Test Adding Event Task
    @Test
    void testAddEvent() {
        LocalDate fromDate = LocalDate.of(2025, 10, 20);
        LocalDate toDate = LocalDate.of(2025, 10, 22);
        DateTimeArg fromDateTime = new DateTimeArg(fromDate);
        DateTimeArg toDateTime = new DateTimeArg(toDate);

        taskList.addEvent("Team meeting", fromDateTime, toDateTime);
        assertEquals(1, taskList.getCount());
        Task task = taskList.getTask(0);
        assertInstanceOf(Event.class, task);
        assertEquals("Team meeting", task.getName());
        assertFalse(task.getDone());
        Event eventTask = (Event) task;
        assertEquals(fromDateTime, eventTask.getFrom());
        assertEquals(toDateTime, eventTask.getTo());
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

    // --- Test Cases for Find ---

    // Helper function to populate a list with various tasks for testing find
    private void populateListForFind() {
        taskList.addToDo("read book");
        taskList.addToDo("submit assignment");
        LocalDate deadline = LocalDate.of(2025, 10, 25);
        DateTimeArg deadlineArg = new DateTimeArg(deadline);
        taskList.addDeadline("book report", deadlineArg);
        taskList.addToDo("buy groceries");
        LocalDate fromDate = LocalDate.of(2025, 11, 1);
        LocalDate toDate = LocalDate.of(2025, 11, 3);
        taskList.addEvent("reading club", new DateTimeArg(fromDate), new DateTimeArg(toDate));
    }

    // Test finding tasks with single matching result
    @Test
    void testFindSingleMatch() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("groceries");
        assertEquals(1, results.size());
        assertEquals("buy groceries", results.get(0).getName());
    }

    // Test finding tasks with multiple matching results
    @Test
    void testFindMultipleMatches() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("book");
        assertEquals(2, results.size());
        // Should find both "read book" and "book report"
        assertTrue(results.stream().anyMatch(t -> t.getName().equals("read book")));
        assertTrue(results.stream().anyMatch(t -> t.getName().equals("book report")));
    }

    // Test finding tasks with no matching results
    @Test
    void testFindNoMatches() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("nonexistent");
        assertEquals(0, results.size());
    }

    // Test finding in empty list
    @Test
    void testFindInEmptyList() {
        ArrayList<Task> results = taskList.findTasks("anything");
        assertEquals(0, results.size());
    }

    // Test finding with empty search string
    @Test
    void testFindWithEmptyString() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("");
        // Empty string matches all tasks
        assertEquals(5, results.size());
    }

    // Test finding matches task type markers
    @Test
    void testFindMatchesTaskTypeMarkers() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("[T]");
        // Should find all ToDo tasks (3 of them)
        assertEquals(3, results.size());
    }

    // Test finding matches completion status
    @Test
    void testFindMatchesCompletionStatus() {
        populateListForFind();
        // Mark the first task
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>(Collections.singletonList(0));
        taskList.mark(indexes);

        ArrayList<Task> markedResults = taskList.findTasks("[X]");
        assertEquals(1, markedResults.size());

        ArrayList<Task> unmarkedResults = taskList.findTasks("[ ]");
        assertEquals(4, unmarkedResults.size());
    }

    // Test finding with partial word match
    @Test
    void testFindPartialMatch() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("read");
        assertEquals(2, results.size());
        // Should find "read book" and "reading club"
        assertTrue(results.stream().anyMatch(t -> t.getName().equals("read book")));
        assertTrue(results.stream().anyMatch(t -> t.getName().equals("reading club")));
    }

    // Test finding is case-sensitive (based on contains() behavior)
    @Test
    void testFindCaseSensitive() {
        populateListForFind();
        ArrayList<Task> resultsLower = taskList.findTasks("book");
        ArrayList<Task> resultsUpper = taskList.findTasks("BOOK");

        assertEquals(2, resultsLower.size());
        assertEquals(0, resultsUpper.size()); // Case sensitive, so uppercase doesn't match
    }

    // Test finding matches dates in deadlines
    @Test
    void testFindMatchesDeadlineDate() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("2025-10-25");
        assertEquals(1, results.size());
        assertInstanceOf(Deadline.class, results.get(0));
    }

    // Test finding matches dates in events
    @Test
    void testFindMatchesEventDates() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("2025-11-01");
        assertEquals(1, results.size());
        assertInstanceOf(Event.class, results.get(0));
    }

    // Test finding with multiple words
    @Test
    void testFindMultipleWords() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("submit assignment");
        assertEquals(1, results.size());
        assertEquals("submit assignment", results.get(0).getName());
    }

    // Test finding all tasks returns correct order
    @Test
    void testFindPreservesOrder() {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("T");
        // Should find tasks in the order they were added
        assertTrue(results.size() >= 3);
        // Verify first result is the first ToDo task added
        assertEquals("read book", results.get(0).getName());
    }

    // --- Test Cases for getSorted() ---

    // Helper function to populate a list with deadlines and events for testing sorting
    private void populateListForSorting() {
        // Add tasks in non-chronological order
        taskList.addToDo("buy groceries"); // Should be excluded from sorted list

        LocalDate deadline1 = LocalDate.of(2025, 11, 15);
        taskList.addDeadline("submit report", new DateTimeArg(deadline1));

        LocalDate fromDate1 = LocalDate.of(2025, 10, 25);
        LocalDate toDate1 = LocalDate.of(2025, 10, 27);
        taskList.addEvent("team meeting", new DateTimeArg(fromDate1), new DateTimeArg(toDate1));

        LocalDate deadline2 = LocalDate.of(2025, 10, 30);
        taskList.addDeadline("assignment deadline", new DateTimeArg(deadline2));

        LocalDate fromDate2 = LocalDate.of(2025, 12, 1);
        LocalDate toDate2 = LocalDate.of(2025, 12, 5);
        taskList.addEvent("conference", new DateTimeArg(fromDate2), new DateTimeArg(toDate2));

        taskList.addToDo("read book"); // Should be excluded from sorted list
    }

    // Test getSorted returns only deadlines and events
    @Test
    void testGetSortedExcludesToDos() {
        populateListForSorting();
        ArrayList<Task> sorted = taskList.getSorted();

        // Should have 4 tasks (2 deadlines + 2 events), excluding 2 todos
        assertEquals(4, sorted.size());

        // All results should be either Deadline or Event
        for (Task task : sorted) {
            assertTrue(task instanceof Deadline || task instanceof Event);
        }
    }

    // Test getSorted returns tasks in chronological order
    @Test
    void testGetSortedCorrectOrder() {
        populateListForSorting();
        ArrayList<Task> sorted = taskList.getSorted();

        // Expected order: team meeting (Oct 25), assignment deadline (Oct 30),
        // submit report (Nov 15), conference (Dec 1)
        assertEquals("team meeting", sorted.get(0).getName());
        assertEquals("assignment deadline", sorted.get(1).getName());
        assertEquals("submit report", sorted.get(2).getName());
        assertEquals("conference", sorted.get(3).getName());
    }

    // Test getSorted with only deadlines
    @Test
    void testGetSortedOnlyDeadlines() {
        LocalDate deadline1 = LocalDate.of(2025, 12, 31);
        LocalDate deadline2 = LocalDate.of(2025, 11, 1);
        LocalDate deadline3 = LocalDate.of(2025, 11, 15);

        taskList.addDeadline("task C", new DateTimeArg(deadline1));
        taskList.addDeadline("task A", new DateTimeArg(deadline2));
        taskList.addDeadline("task B", new DateTimeArg(deadline3));

        ArrayList<Task> sorted = taskList.getSorted();

        assertEquals(3, sorted.size());
        assertEquals("task A", sorted.get(0).getName()); // Nov 1
        assertEquals("task B", sorted.get(1).getName()); // Nov 15
        assertEquals("task C", sorted.get(2).getName()); // Dec 31
    }

    // Test getSorted with only events
    @Test
    void testGetSortedOnlyEvents() {
        LocalDate from1 = LocalDate.of(2025, 12, 1);
        LocalDate to1 = LocalDate.of(2025, 12, 3);
        LocalDate from2 = LocalDate.of(2025, 10, 20);
        LocalDate to2 = LocalDate.of(2025, 10, 22);
        LocalDate from3 = LocalDate.of(2025, 11, 10);
        LocalDate to3 = LocalDate.of(2025, 11, 12);

        taskList.addEvent("event C", new DateTimeArg(from1), new DateTimeArg(to1));
        taskList.addEvent("event A", new DateTimeArg(from2), new DateTimeArg(to2));
        taskList.addEvent("event B", new DateTimeArg(from3), new DateTimeArg(to3));

        ArrayList<Task> sorted = taskList.getSorted();

        assertEquals(3, sorted.size());
        assertEquals("event A", sorted.get(0).getName()); // Oct 20
        assertEquals("event B", sorted.get(1).getName()); // Nov 10
        assertEquals("event C", sorted.get(2).getName()); // Dec 1
    }

    // Test getSorted with mixed deadlines and events
    @Test
    void testGetSortedMixedTasks() {
        LocalDate deadline1 = LocalDate.of(2025, 11, 5);
        LocalDate from1 = LocalDate.of(2025, 11, 1);
        LocalDate to1 = LocalDate.of(2025, 11, 3);
        LocalDate deadline2 = LocalDate.of(2025, 11, 10);

        taskList.addDeadline("deadline 1", new DateTimeArg(deadline1));
        taskList.addEvent("event 1", new DateTimeArg(from1), new DateTimeArg(to1));
        taskList.addDeadline("deadline 2", new DateTimeArg(deadline2));

        ArrayList<Task> sorted = taskList.getSorted();

        assertEquals(3, sorted.size());
        assertEquals("event 1", sorted.get(0).getName());      // Nov 1
        assertEquals("deadline 1", sorted.get(1).getName());   // Nov 5
        assertEquals("deadline 2", sorted.get(2).getName());   // Nov 10
    }

    // Test getSorted with empty list
    @Test
    void testGetSortedEmptyList() {
        ArrayList<Task> sorted = taskList.getSorted();
        assertEquals(0, sorted.size());
    }

    // Test getSorted with only todos (should return empty)
    @Test
    void testGetSortedOnlyToDos() {
        taskList.addToDo("task 1");
        taskList.addToDo("task 2");
        taskList.addToDo("task 3");

        ArrayList<Task> sorted = taskList.getSorted();
        assertEquals(0, sorted.size());
    }

    // Test getSorted with same dates
    @Test
    void testGetSortedSameDates() {
        LocalDate sameDate = LocalDate.of(2025, 11, 1);

        taskList.addDeadline("deadline A", new DateTimeArg(sameDate));
        taskList.addDeadline("deadline B", new DateTimeArg(sameDate));
        taskList.addEvent("event A", new DateTimeArg(sameDate), new DateTimeArg(sameDate));

        ArrayList<Task> sorted = taskList.getSorted();

        // All three should be included, order doesn't matter for same dates
        assertEquals(3, sorted.size());
    }

    // Test getSorted preserves original task list
    @Test
    void testGetSortedDoesNotModifyOriginal() {
        populateListForSorting();

        // Get original task order
        ArrayList<Task> original = taskList.getTasks();
        String firstTaskName = original.get(0).getName();

        // Call getSorted
        taskList.getSorted();

        // Original list should be unchanged
        ArrayList<Task> afterSort = taskList.getTasks();
        assertEquals(firstTaskName, afterSort.get(0).getName());
        assertEquals(original.size(), afterSort.size());
    }

    // --- Test Cases for Edit Methods ---

    // Test editing description of a ToDo task
    @Test
    void testEditDescriptionOfToDo() {
        taskList.addToDo("original task");
        taskList.editDesc(0, "updated task");

        assertEquals("updated task", taskList.getTask(0).getName());
        assertInstanceOf(ToDo.class, taskList.getTask(0));
    }

    // Test editing description of a Deadline task
    @Test
    void testEditDescriptionOfDeadline() {
        LocalDate deadline = LocalDate.of(2025, 11, 15);
        taskList.addDeadline("original deadline", new DateTimeArg(deadline));
        taskList.editDesc(0, "updated deadline");

        assertEquals("updated deadline", taskList.getTask(0).getName());
        assertInstanceOf(Deadline.class, taskList.getTask(0));
        // Deadline date should remain unchanged
        assertEquals(deadline, ((Deadline) taskList.getTask(0)).getDeadline().getDate());
    }

    // Test editing description of an Event task
    @Test
    void testEditDescriptionOfEvent() {
        LocalDate from = LocalDate.of(2025, 11, 1);
        LocalDate to = LocalDate.of(2025, 11, 3);
        taskList.addEvent("original event", new DateTimeArg(from), new DateTimeArg(to));
        taskList.editDesc(0, "updated event");

        assertEquals("updated event", taskList.getTask(0).getName());
        assertInstanceOf(Event.class, taskList.getTask(0));
        // Event dates should remain unchanged
        assertEquals(from, ((Event) taskList.getTask(0)).getFrom().getDate());
        assertEquals(to, ((Event) taskList.getTask(0)).getTo().getDate());
    }

    // Test editing deadline of a Deadline task
    @Test
    void testEditDeadlineOfDeadlineTask() throws Exception {
        LocalDate originalDate = LocalDate.of(2025, 11, 15);
        LocalDate newDate = LocalDate.of(2025, 12, 31);
        taskList.addDeadline("report", new DateTimeArg(originalDate));

        taskList.editDeadline(0, new DateTimeArg(newDate));

        Deadline deadline = (Deadline) taskList.getTask(0);
        assertEquals(newDate, deadline.getDeadline().getDate());
        assertEquals("report", deadline.getName()); // Description should remain unchanged
    }

    // Test editing deadline of a non-Deadline task throws exception
    @Test
    void testEditDeadlineOfToDoThrowsException() {
        taskList.addToDo("task");

        Exception exception = assertThrows(Exception.class, () -> {
            taskList.editDeadline(0, new DateTimeArg(LocalDate.of(2025, 11, 15)));
        });

        assertTrue(exception.getMessage().contains("not a deadline"));
    }

    // Test editing deadline of an Event throws exception
    @Test
    void testEditDeadlineOfEventThrowsException() {
        LocalDate from = LocalDate.of(2025, 11, 1);
        LocalDate to = LocalDate.of(2025, 11, 3);
        taskList.addEvent("meeting", new DateTimeArg(from), new DateTimeArg(to));

        Exception exception = assertThrows(Exception.class, () -> {
            taskList.editDeadline(0, new DateTimeArg(LocalDate.of(2025, 11, 15)));
        });

        assertTrue(exception.getMessage().contains("not a deadline"));
    }

    // Test editing from date of an Event task
    @Test
    void testEditFromDateOfEvent() throws Exception {
        LocalDate originalFrom = LocalDate.of(2025, 11, 1);
        LocalDate originalTo = LocalDate.of(2025, 11, 3);
        LocalDate newFrom = LocalDate.of(2025, 11, 5);
        taskList.addEvent("conference", new DateTimeArg(originalFrom), new DateTimeArg(originalTo));

        taskList.editFrom(0, new DateTimeArg(newFrom));

        Event event = (Event) taskList.getTask(0);
        assertEquals(newFrom, event.getFrom().getDate());
        assertEquals(originalTo, event.getTo().getDate()); // To date should remain unchanged
        assertEquals("conference", event.getName());
    }

    // Test editing from date of a non-Event task throws exception
    @Test
    void testEditFromOfToDoThrowsException() {
        taskList.addToDo("task");

        Exception exception = assertThrows(Exception.class, () -> {
            taskList.editFrom(0, new DateTimeArg(LocalDate.of(2025, 11, 15)));
        });

        assertTrue(exception.getMessage().contains("not an event"));
    }

    // Test editing from date of a Deadline throws exception
    @Test
    void testEditFromOfDeadlineThrowsException() {
        LocalDate deadline = LocalDate.of(2025, 11, 15);
        taskList.addDeadline("report", new DateTimeArg(deadline));

        Exception exception = assertThrows(Exception.class, () -> {
            taskList.editFrom(0, new DateTimeArg(LocalDate.of(2025, 11, 1)));
        });

        assertTrue(exception.getMessage().contains("not an event"));
    }

    // Test editing to date of an Event task
    @Test
    void testEditToDateOfEvent() throws Exception {
        LocalDate originalFrom = LocalDate.of(2025, 11, 1);
        LocalDate originalTo = LocalDate.of(2025, 11, 3);
        LocalDate newTo = LocalDate.of(2025, 11, 10);
        taskList.addEvent("workshop", new DateTimeArg(originalFrom), new DateTimeArg(originalTo));

        taskList.editTo(0, new DateTimeArg(newTo));

        Event event = (Event) taskList.getTask(0);
        assertEquals(originalFrom, event.getFrom().getDate()); // From date should remain unchanged
        assertEquals(newTo, event.getTo().getDate());
        assertEquals("workshop", event.getName());
    }

    // Test editing to date of a non-Event task throws exception
    @Test
    void testEditToOfToDoThrowsException() {
        taskList.addToDo("task");

        Exception exception = assertThrows(Exception.class, () -> {
            taskList.editTo(0, new DateTimeArg(LocalDate.of(2025, 11, 15)));
        });

        assertTrue(exception.getMessage().contains("not an event"));
    }

    // Test editing to date of a Deadline throws exception
    @Test
    void testEditToOfDeadlineThrowsException() {
        LocalDate deadline = LocalDate.of(2025, 11, 15);
        taskList.addDeadline("assignment", new DateTimeArg(deadline));

        Exception exception = assertThrows(Exception.class, () -> {
            taskList.editTo(0, new DateTimeArg(LocalDate.of(2025, 11, 20)));
        });

        assertTrue(exception.getMessage().contains("not an event"));
    }

    // Test editing multiple tasks in sequence
    @Test
    void testEditMultipleTasksInSequence() throws Exception {
        // Add various tasks
        taskList.addToDo("task 1");
        LocalDate deadline = LocalDate.of(2025, 11, 15);
        taskList.addDeadline("task 2", new DateTimeArg(deadline));
        LocalDate from = LocalDate.of(2025, 11, 20);
        LocalDate to = LocalDate.of(2025, 11, 22);
        taskList.addEvent("task 3", new DateTimeArg(from), new DateTimeArg(to));

        // Edit all tasks
        taskList.editDesc(0, "updated task 1");
        taskList.editDesc(1, "updated task 2");
        LocalDate newDeadline = LocalDate.of(2025, 12, 1);
        taskList.editDeadline(1, new DateTimeArg(newDeadline));
        taskList.editDesc(2, "updated task 3");
        LocalDate newFrom = LocalDate.of(2025, 11, 25);
        taskList.editFrom(2, new DateTimeArg(newFrom));

        // Verify changes
        assertEquals("updated task 1", taskList.getTask(0).getName());
        assertEquals("updated task 2", taskList.getTask(1).getName());
        assertEquals(newDeadline, ((Deadline) taskList.getTask(1)).getDeadline().getDate());
        assertEquals("updated task 3", taskList.getTask(2).getName());
        assertEquals(newFrom, ((Event) taskList.getTask(2)).getFrom().getDate());
        assertEquals(to, ((Event) taskList.getTask(2)).getTo().getDate());
    }

    // Test editing preserves task completion status
    @Test
    void testEditPreservesCompletionStatus() throws Exception {
        taskList.addToDo("task");
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>(Collections.singletonList(0));
        taskList.mark(indexes);
        assertTrue(taskList.getTask(0).getDone());

        taskList.editDesc(0, "updated task");

        // Completion status should remain marked
        assertTrue(taskList.getTask(0).getDone());
        assertEquals("updated task", taskList.getTask(0).getName());
    }

    // Test editing with same value works
    @Test
    void testEditWithSameValue() throws Exception {
        LocalDate deadline = LocalDate.of(2025, 11, 15);
        taskList.addDeadline("report", new DateTimeArg(deadline));

        taskList.editDeadline(0, new DateTimeArg(deadline));

        assertEquals(deadline, ((Deadline) taskList.getTask(0)).getDeadline().getDate());
        assertEquals("report", taskList.getTask(0).getName());
    }

    // Test editing description to empty string (if allowed)
    @Test
    void testEditDescriptionToEmptyString() {
        taskList.addToDo("task");
        taskList.editDesc(0, "");

        assertEquals("", taskList.getTask(0).getName());
    }

    // Test editing last task in list
    @Test
    void testEditLastTask() {
        taskList.addToDo("task 1");
        taskList.addToDo("task 2");
        taskList.addToDo("task 3");

        taskList.editDesc(2, "updated last task");

        assertEquals("updated last task", taskList.getTask(2).getName());
        assertEquals(3, taskList.getCount());
    }
}
