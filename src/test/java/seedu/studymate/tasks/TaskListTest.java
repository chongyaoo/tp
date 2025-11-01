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

import seedu.studymate.exceptions.StudyMateException;
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
    void testAddToDo() throws StudyMateException {
        taskList.addToDo("read book");
        assertEquals(1, taskList.getCount());
        Task task = taskList.getTask(0);
        assertInstanceOf(ToDo.class, task);
        assertEquals("read book", task.getName());
        assertFalse(task.getDone());
    }

    // Test Adding Deadline Task
    @Test
    void testAddDeadline() throws StudyMateException {
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
    void testAddEvent() throws StudyMateException {
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 10, 20, 9, 0);
        LocalDateTime toDateTime = LocalDateTime.of(2025, 10, 22, 17, 0);
        DateTimeArg fromDateTimeArg = new DateTimeArg(fromDateTime.toLocalDate(), fromDateTime.toLocalTime());
        DateTimeArg toDateTimeArg = new DateTimeArg(toDateTime.toLocalDate(), toDateTime.toLocalTime());

        taskList.addEvent("Team meeting", fromDateTimeArg, toDateTimeArg);
        assertEquals(1, taskList.getCount());
        Task task = taskList.getTask(0);
        assertInstanceOf(Event.class, task);
        assertEquals("Team meeting", task.getName());
        assertFalse(task.getDone());
        Event eventTask = (Event) task;
        assertEquals(fromDateTimeArg, eventTask.getFrom());
        assertEquals(toDateTimeArg, eventTask.getTo());
    }

    // --- Test Cases for Mark/Unmark ---

    // Helper Function to test marking/unmarking
    private void populateListForMarking() throws StudyMateException {
        taskList.addToDo("Task 1");
        taskList.addToDo("Task 2");
        taskList.addToDo("Task 3");
    }

    // Test marking 1 task
    @Test
    void testMark() throws StudyMateException {
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
    void testMarkMultiple() throws StudyMateException {
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
    void testUnmark() throws StudyMateException {
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
    private void populateListForDeletion() throws StudyMateException {
        taskList.addToDo("Task A"); // 0
        taskList.addToDo("Task B"); // 1
        taskList.addToDo("Task C"); // 2
        taskList.addToDo("Task D"); // 3
    }

    // Test deleting 1 task
    @Test
    void testDeleteSingleTask() throws StudyMateException {
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
    void testDeleteMultipleTasks() throws StudyMateException {
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
    void testGetTasks() throws StudyMateException {
        populateListForDeletion();
        ArrayList<Task> tasks = taskList.getTasks();
        assertEquals(4, tasks.size());
        assertEquals("Task A", tasks.get(0).getName());
    }

    // --- Test Cases for Find ---

    // Helper function to populate a list with various tasks for testing find
    private void populateListForFind() throws StudyMateException {
        taskList.addToDo("read book");
        taskList.addToDo("submit assignment");
        LocalDateTime deadlineDateTime = LocalDateTime.of(2025, 10, 25, 23, 59);
        DateTimeArg deadlineArg = new DateTimeArg(deadlineDateTime.toLocalDate(), deadlineDateTime.toLocalTime());
        taskList.addDeadline("book report", deadlineArg);
        taskList.addToDo("buy groceries");
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 11, 1, 14, 0);
        LocalDateTime toDateTime = LocalDateTime.of(2025, 11, 3, 16, 0);
        taskList.addEvent("reading club", new DateTimeArg(fromDateTime.toLocalDate(), fromDateTime.toLocalTime()),
                          new DateTimeArg(toDateTime.toLocalDate(), toDateTime.toLocalTime()));
    }

    // Test finding tasks with single matching result
    @Test
    void testFindSingleMatch() throws StudyMateException {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("groceries");
        assertEquals(1, results.size());
        assertEquals("buy groceries", results.get(0).getName());
    }

    // Test finding tasks with multiple matching results
    @Test
    void testFindMultipleMatches() throws StudyMateException {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("book");
        assertEquals(2, results.size());
        // Should find both "read book" and "book report"
        assertTrue(results.stream().anyMatch(t -> t.getName().equals("read book")));
        assertTrue(results.stream().anyMatch(t -> t.getName().equals("book report")));
    }

    // Test finding tasks with no matching results
    @Test
    void testFindNoMatches() throws StudyMateException {
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
    void testFindWithEmptyString() throws StudyMateException {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("");
        // Empty string matches all tasks
        assertEquals(5, results.size());
    }

    // Test finding with partial word match
    @Test
    void testFindPartialMatch() throws StudyMateException {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("read");
        assertEquals(2, results.size());
        // Should find "read book" and "reading club"
        assertTrue(results.stream().anyMatch(t -> t.getName().equals("read book")));
        assertTrue(results.stream().anyMatch(t -> t.getName().equals("reading club")));
    }

    // Test finding is case-sensitive (based on contains() behavior)
    @Test
    void testFindCaseSensitive() throws StudyMateException {
        populateListForFind();
        ArrayList<Task> resultsLower = taskList.findTasks("book");
        ArrayList<Task> resultsUpper = taskList.findTasks("BOOK");

        assertEquals(2, resultsLower.size());
        assertEquals(0, resultsUpper.size()); // Case sensitive, so uppercase doesn't match
    }

    // Test finding with multiple words
    @Test
    void testFindMultipleWords() throws StudyMateException {
        populateListForFind();
        ArrayList<Task> results = taskList.findTasks("submit assignment");
        assertEquals(1, results.size());
        assertEquals("submit assignment", results.get(0).getName());
    }

    // --- Test Cases for getSorted() ---

    // Helper function to populate a list with deadlines and events for testing sorting
    private void populateListForSorting() throws StudyMateException {
        // Add tasks in non-chronological order
        taskList.addToDo("buy groceries"); // Should be excluded from sorted list

        LocalDateTime deadline1 = LocalDateTime.of(2025, 11, 15, 17, 0);
        taskList.addDeadline("submit report", new DateTimeArg(deadline1.toLocalDate(), deadline1.toLocalTime()));

        LocalDateTime fromDate1 = LocalDateTime.of(2025, 10, 25, 9, 0);
        LocalDateTime toDate1 = LocalDateTime.of(2025, 10, 27, 17, 0);
        taskList.addEvent("team meeting", new DateTimeArg(fromDate1.toLocalDate(), fromDate1.toLocalTime()),
                          new DateTimeArg(toDate1.toLocalDate(), toDate1.toLocalTime()));

        LocalDateTime deadline2 = LocalDateTime.of(2025, 10, 30, 23, 59);
        taskList.addDeadline("assignment deadline", new DateTimeArg(deadline2.toLocalDate(), deadline2.toLocalTime()));

        LocalDateTime fromDate2 = LocalDateTime.of(2025, 12, 1, 8, 0);
        LocalDateTime toDate2 = LocalDateTime.of(2025, 12, 5, 18, 0);
        taskList.addEvent("conference", new DateTimeArg(fromDate2.toLocalDate(), fromDate2.toLocalTime()),
                          new DateTimeArg(toDate2.toLocalDate(), toDate2.toLocalTime()));

        taskList.addToDo("read book"); // Should be excluded from sorted list
    }

    // Test getSorted returns only deadlines and events
    @Test
    void testGetSortedExcludesToDos() throws StudyMateException {
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
    void testGetSortedCorrectOrder() throws StudyMateException {
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
    void testGetSortedOnlyDeadlines() throws StudyMateException {
        LocalDateTime deadline1 = LocalDateTime.of(2025, 12, 31, 23, 59);
        LocalDateTime deadline2 = LocalDateTime.of(2025, 11, 1, 9, 0);
        LocalDateTime deadline3 = LocalDateTime.of(2025, 11, 15, 17, 0);

        taskList.addDeadline("task C", new DateTimeArg(deadline1.toLocalDate(), deadline1.toLocalTime()));
        taskList.addDeadline("task A", new DateTimeArg(deadline2.toLocalDate(), deadline2.toLocalTime()));
        taskList.addDeadline("task B", new DateTimeArg(deadline3.toLocalDate(), deadline3.toLocalTime()));

        ArrayList<Task> sorted = taskList.getSorted();

        assertEquals(3, sorted.size());
        assertEquals("task A", sorted.get(0).getName()); // Nov 1
        assertEquals("task B", sorted.get(1).getName()); // Nov 15
        assertEquals("task C", sorted.get(2).getName()); // Dec 31
    }

    // Test getSorted with only events
    @Test
    void testGetSortedOnlyEvents() throws StudyMateException {
        LocalDateTime from1 = LocalDateTime.of(2025, 12, 1, 9, 0);
        LocalDateTime to1 = LocalDateTime.of(2025, 12, 3, 17, 0);
        LocalDateTime from2 = LocalDateTime.of(2025, 10, 20, 10, 0);
        LocalDateTime to2 = LocalDateTime.of(2025, 10, 22, 16, 0);
        LocalDateTime from3 = LocalDateTime.of(2025, 11, 10, 8, 30);
        LocalDateTime to3 = LocalDateTime.of(2025, 11, 12, 18, 0);

        taskList.addEvent("event C", new DateTimeArg(from1.toLocalDate(), from1.toLocalTime()),
                          new DateTimeArg(to1.toLocalDate(), to1.toLocalTime()));
        taskList.addEvent("event A", new DateTimeArg(from2.toLocalDate(), from2.toLocalTime()),
                          new DateTimeArg(to2.toLocalDate(), to2.toLocalTime()));
        taskList.addEvent("event B", new DateTimeArg(from3.toLocalDate(), from3.toLocalTime()),
                          new DateTimeArg(to3.toLocalDate(), to3.toLocalTime()));

        ArrayList<Task> sorted = taskList.getSorted();

        assertEquals(3, sorted.size());
        assertEquals("event A", sorted.get(0).getName()); // Oct 20
        assertEquals("event B", sorted.get(1).getName()); // Nov 10
        assertEquals("event C", sorted.get(2).getName()); // Dec 1
    }

    // Test getSorted with mixed deadlines and events
    @Test
    void testGetSortedMixedTasks() throws StudyMateException {
        LocalDateTime deadline1 = LocalDateTime.of(2025, 11, 5, 10, 0);
        LocalDateTime from1 = LocalDateTime.of(2025, 11, 1, 9, 0);
        LocalDateTime to1 = LocalDateTime.of(2025, 11, 3, 17, 0);
        LocalDateTime deadline2 = LocalDateTime.of(2025, 11, 10, 15, 30);

        taskList.addDeadline("deadline 1", new DateTimeArg(deadline1.toLocalDate(), deadline1.toLocalTime()));
        taskList.addEvent("event 1", new DateTimeArg(from1.toLocalDate(), from1.toLocalTime()),
                          new DateTimeArg(to1.toLocalDate(), to1.toLocalTime()));
        taskList.addDeadline("deadline 2", new DateTimeArg(deadline2.toLocalDate(), deadline2.toLocalTime()));

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
    void testGetSortedOnlyToDos() throws StudyMateException {
        taskList.addToDo("task 1");
        taskList.addToDo("task 2");
        taskList.addToDo("task 3");

        ArrayList<Task> sorted = taskList.getSorted();
        assertEquals(0, sorted.size());
    }

    // Test getSorted with same dates
    @Test
    void testGetSortedSameDates() throws StudyMateException {
        LocalDateTime sameDateTime = LocalDateTime.of(2025, 11, 1, 10, 0);

        taskList.addDeadline("deadline A", new DateTimeArg(sameDateTime.toLocalDate(), sameDateTime.toLocalTime()));
        taskList.addDeadline("deadline B", new DateTimeArg(sameDateTime.toLocalDate(), sameDateTime.toLocalTime()));
        taskList.addEvent("event A", new DateTimeArg(sameDateTime.toLocalDate(), sameDateTime.toLocalTime()),
                          new DateTimeArg(sameDateTime.toLocalDate(), sameDateTime.toLocalTime()));

        ArrayList<Task> sorted = taskList.getSorted();

        // All three should be included, order doesn't matter for same dates
        assertEquals(3, sorted.size());
    }

    // Test getSorted preserves original task list
    @Test
    void testGetSortedDoesNotModifyOriginal() throws StudyMateException {
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
    void testEditDescriptionOfToDo() throws StudyMateException {
        taskList.addToDo("original task");
        taskList.editDesc(0, "updated task");

        assertEquals("updated task", taskList.getTask(0).getName());
        assertInstanceOf(ToDo.class, taskList.getTask(0));
    }

    // Test editing description of a Deadline task
    @Test
    void testEditDescriptionOfDeadline() throws StudyMateException {
        LocalDateTime deadline = LocalDateTime.of(2025, 11, 15, 14, 30);
        taskList.addDeadline("original deadline", new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime()));
        taskList.editDesc(0, "updated deadline");

        assertEquals("updated deadline", taskList.getTask(0).getName());
        assertInstanceOf(Deadline.class, taskList.getTask(0));
        // Deadline date should remain unchanged
        assertEquals(deadline.toLocalDate(), ((Deadline) taskList.getTask(0)).getDeadline().getDate());
    }

    // Test editing description of an Event task
    @Test
    void testEditDescriptionOfEvent() throws StudyMateException {
        LocalDateTime from = LocalDateTime.of(2025, 11, 1, 9, 0);
        LocalDateTime to = LocalDateTime.of(2025, 11, 3, 17, 0);
        taskList.addEvent("original event", new DateTimeArg(from.toLocalDate(), from.toLocalTime()),
                          new DateTimeArg(to.toLocalDate(), to.toLocalTime()));
        taskList.editDesc(0, "updated event");

        assertEquals("updated event", taskList.getTask(0).getName());
        assertInstanceOf(Event.class, taskList.getTask(0));
        // Event dates should remain unchanged
        assertEquals(from.toLocalDate(), ((Event) taskList.getTask(0)).getFrom().getDate());
        assertEquals(to.toLocalDate(), ((Event) taskList.getTask(0)).getTo().getDate());
    }

    // Test editing deadline of a Deadline task
    @Test
    void testEditDeadlineOfDeadlineTask() throws StudyMateException {
        LocalDateTime originalDateTime = LocalDateTime.of(2025, 11, 15, 10, 0);
        LocalDateTime newDateTime = LocalDateTime.of(2025, 12, 31, 23, 59);
        taskList.addDeadline("report", new DateTimeArg(originalDateTime.toLocalDate(), originalDateTime.toLocalTime()));

        taskList.editDeadline(0, new DateTimeArg(newDateTime.toLocalDate(), newDateTime.toLocalTime()));

        Deadline deadline = (Deadline) taskList.getTask(0);
        assertEquals(newDateTime.toLocalDate(), deadline.getDeadline().getDate());
        assertEquals("report", deadline.getName()); // Description should remain unchanged
    }

    // Test editing deadline of a non-Deadline task throws exception
    @Test
    void testEditDeadlineOfToDoThrowsException() throws StudyMateException {
        taskList.addToDo("task");

        Exception exception = assertThrows(Exception.class, () -> {
            LocalDateTime deadline = LocalDateTime.of(2025, 11, 15, 10, 0);
            taskList.editDeadline(0, new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime()));
        });

        assertTrue(exception.getMessage().contains("not a deadline"));
    }

    // Test editing deadline of an Event throws exception
    @Test
    void testEditDeadlineOfEventThrowsException() throws StudyMateException {
        LocalDateTime from = LocalDateTime.of(2025, 11, 1, 9, 0);
        LocalDateTime to = LocalDateTime.of(2025, 11, 3, 17, 0);
        taskList.addEvent("meeting", new DateTimeArg(from.toLocalDate(), from.toLocalTime()),
                          new DateTimeArg(to.toLocalDate(), to.toLocalTime()));

        Exception exception = assertThrows(Exception.class, () -> {
            LocalDateTime deadline = LocalDateTime.of(2025, 11, 15, 10, 0);
            taskList.editDeadline(0, new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime()));
        });

        assertTrue(exception.getMessage().contains("not a deadline"));
    }

    // Test editing from date of an Event task
    @Test
    void testEditFromDateOfEvent() throws StudyMateException {
        LocalDateTime originalFrom = LocalDateTime.of(2025, 11, 1, 9, 0);
        LocalDateTime originalTo = LocalDateTime.of(2025, 11, 3, 17, 0);
        LocalDateTime newFrom = LocalDateTime.of(2025, 11, 5, 10, 0);
        taskList.addEvent("conference", new DateTimeArg(originalFrom.toLocalDate(), originalFrom.toLocalTime()),
                          new DateTimeArg(originalTo.toLocalDate(), originalTo.toLocalTime()));

        taskList.editFrom(0, new DateTimeArg(newFrom.toLocalDate(), newFrom.toLocalTime()));

        Event event = (Event) taskList.getTask(0);
        assertEquals(newFrom.toLocalDate(), event.getFrom().getDate());
        assertEquals(originalTo.toLocalDate(), event.getTo().getDate()); // To date should remain unchanged
        assertEquals("conference", event.getName());
    }

    // Test editing from date of a non-Event task throws exception
    @Test
    void testEditFromOfToDoThrowsException() throws StudyMateException {
        taskList.addToDo("task");

        Exception exception = assertThrows(Exception.class, () -> {
            LocalDateTime from = LocalDateTime.of(2025, 11, 15, 10, 0);
            taskList.editFrom(0, new DateTimeArg(from.toLocalDate(), from.toLocalTime()));
        });

        assertTrue(exception.getMessage().contains("not an event"));
    }

    // Test editing from date of a Deadline throws exception
    @Test
    void testEditFromOfDeadlineThrowsException() throws StudyMateException {
        LocalDateTime deadline = LocalDateTime.of(2025, 11, 15, 14, 30);
        taskList.addDeadline("report", new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime()));

        Exception exception = assertThrows(Exception.class, () -> {
            LocalDateTime from = LocalDateTime.of(2025, 11, 1, 9, 0);
            taskList.editFrom(0, new DateTimeArg(from.toLocalDate(), from.toLocalTime()));
        });

        assertTrue(exception.getMessage().contains("not an event"));
    }

    // Test editing to date of an Event task
    @Test
    void testEditToDateOfEvent() throws StudyMateException {
        LocalDateTime originalFrom = LocalDateTime.of(2025, 11, 1, 9, 0);
        LocalDateTime originalTo = LocalDateTime.of(2025, 11, 3, 17, 0);
        LocalDateTime newTo = LocalDateTime.of(2025, 11, 10, 18, 0);
        taskList.addEvent("workshop", new DateTimeArg(originalFrom.toLocalDate(), originalFrom.toLocalTime()),
                          new DateTimeArg(originalTo.toLocalDate(), originalTo.toLocalTime()));

        taskList.editTo(0, new DateTimeArg(newTo.toLocalDate(), newTo.toLocalTime()));

        Event event = (Event) taskList.getTask(0);
        assertEquals(originalFrom.toLocalDate(), event.getFrom().getDate()); // From date should remain unchanged
        assertEquals(newTo.toLocalDate(), event.getTo().getDate());
        assertEquals("workshop", event.getName());
    }

    // Test editing to date of a non-Event task throws exception
    @Test
    void testEditToOfToDoThrowsException() throws StudyMateException {
        taskList.addToDo("task");

        Exception exception = assertThrows(Exception.class, () -> {
            LocalDateTime to = LocalDateTime.of(2025, 11, 15, 17, 0);
            taskList.editTo(0, new DateTimeArg(to.toLocalDate(), to.toLocalTime()));
        });

        assertTrue(exception.getMessage().contains("not an event"));
    }

    // Test editing to date of a Deadline throws exception
    @Test
    void testEditToOfDeadlineThrowsException() throws StudyMateException {
        LocalDateTime deadline = LocalDateTime.of(2025, 11, 15, 14, 30);
        taskList.addDeadline("assignment", new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime()));

        Exception exception = assertThrows(Exception.class, () -> {
            LocalDateTime to = LocalDateTime.of(2025, 11, 20, 17, 0);
            taskList.editTo(0, new DateTimeArg(to.toLocalDate(), to.toLocalTime()));
        });

        assertTrue(exception.getMessage().contains("not an event"));
    }

    // Test editing multiple tasks in sequence
    @Test
    void testEditMultipleTasksInSequence() throws StudyMateException {
        // Add various tasks
        taskList.addToDo("task 1");
        LocalDateTime deadline = LocalDateTime.of(2025, 11, 15, 14, 30);
        taskList.addDeadline("task 2", new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime()));
        LocalDateTime from = LocalDateTime.of(2025, 11, 20, 9, 0);
        LocalDateTime to = LocalDateTime.of(2025, 11, 22, 17, 0);
        taskList.addEvent("task 3", new DateTimeArg(from.toLocalDate(), from.toLocalTime()),
                          new DateTimeArg(to.toLocalDate(), to.toLocalTime()));

        // Edit all tasks
        taskList.editDesc(0, "updated task 1");
        taskList.editDesc(1, "updated task 2");
        LocalDateTime newDeadline = LocalDateTime.of(2025, 12, 1, 10, 0);
        taskList.editDeadline(1, new DateTimeArg(newDeadline.toLocalDate(), newDeadline.toLocalTime()));
        taskList.editDesc(2, "updated task 3");
        LocalDateTime newFrom = LocalDateTime.of(2025, 11, 25, 8, 30);
        taskList.editFrom(2, new DateTimeArg(newFrom.toLocalDate(), newFrom.toLocalTime()));

        // Verify changes
        assertEquals("updated task 1", taskList.getTask(0).getName());
        assertEquals("updated task 2", taskList.getTask(1).getName());
        assertEquals(newDeadline.toLocalDate(), ((Deadline) taskList.getTask(1)).getDeadline().getDate());
        assertEquals("updated task 3", taskList.getTask(2).getName());
        assertEquals(newFrom.toLocalDate(), ((Event) taskList.getTask(2)).getFrom().getDate());
        assertEquals(to.toLocalDate(), ((Event) taskList.getTask(2)).getTo().getDate());
    }

    // Test editing preserves task completion status
    @Test
    void testEditPreservesCompletionStatus() throws StudyMateException {
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
    void testEditWithSameValue() throws StudyMateException {
        LocalDateTime deadline = LocalDateTime.of(2025, 11, 15, 14, 30);
        taskList.addDeadline("report", new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime()));

        taskList.editDeadline(0, new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime()));

        assertEquals(deadline.toLocalDate(), ((Deadline) taskList.getTask(0)).getDeadline().getDate());
        assertEquals("report", taskList.getTask(0).getName());
    }

    // Test editing description to empty string (if allowed)
    @Test
    void testEditDescriptionToEmptyString() throws StudyMateException {
        taskList.addToDo("task");
        taskList.editDesc(0, "");

        assertEquals("", taskList.getTask(0).getName());
    }

    // Test editing last task in list
    @Test
    void testEditLastTask() throws StudyMateException {
        taskList.addToDo("task 1");
        taskList.addToDo("task 2");
        taskList.addToDo("task 3");

        taskList.editDesc(2, "updated last task");

        assertEquals("updated last task", taskList.getTask(2).getName());
        assertEquals(3, taskList.getCount());
    }

    // Test that adding tasks beyond 10000 limit throws exception
    @Test
    void testAddToDo_exceedsCapacity_throwsException() throws StudyMateException {
        // Add 10000 tasks (the maximum allowed)
        for (int i = 0; i < 10000; i++) {
            taskList.addToDo("Task " + i);
        }
        assertEquals(10000, taskList.getCount());

        // Attempting to add the 10001st task should throw exception
        assertThrows(StudyMateException.class, () -> taskList.addToDo("Task 10001"));
        assertEquals(10000, taskList.getCount()); // Count should remain at 10000
    }
}
