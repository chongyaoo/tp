package seedu.studymate.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import seedu.studymate.habits.Habit;
import seedu.studymate.habits.HabitList;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.tasks.TaskList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageTest {

    private static final String TEST_FILE_PATH = "test_data/test_storage.txt";
    private static final char DELIM = 0x1F; // for serialisation
    private Storage storage;
    private TaskList tasks;
    private ReminderList reminders;
    private HabitList habits;

    /**
          * Creates a temporary test file to test the storage class
     */
    @BeforeEach
    public void setUp() throws IOException {
        Files.createDirectories(Paths.get("test_data"));
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        storage = new Storage(TEST_FILE_PATH);
        tasks = new TaskList();
        reminders = new ReminderList();
        habits = new HabitList();
    }

    /**
     * Deletes the test file for a clean ui
     */
    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
    }

    /**
          * Tests that a Todo task is saved correctly
     */
    @Test
    public void test_todo_save() throws Exception {
        tasks.addToDo("Read book");
        tasks.getTask(0).setDone(true);

        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        assertTrue(content.contains("T" + DELIM + "1" + DELIM + "Read book"));
    }

    /**
     * Test that a saved Todo task is read correctly
     */
    @Test
    public void test_todo_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH), List.of("T" + DELIM + "1" + DELIM + "Read book"),
                StandardOpenOption.CREATE);
        storage.load(tasks, reminders, habits);

        assertEquals(1, tasks.getCount());
        assertEquals("Read book", tasks.getTask(0).getName());
        assertTrue(tasks.getTask(0).getDone());
    }

    /**
     * Tests that a Deadline task is saved correctly
     */
    @Test
    public void test_deadline_save() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addDeadline("Submit report", new DateTimeArg(LocalDate.parse("2025-10-15")));

        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        assertTrue(content.contains("D" + DELIM + "0" + DELIM + "Submit report" + DELIM + "2025-10-15"));
    }

    /**
          * Tests that a saved Deadline task is read correctly
     */
    @Test
    public void test_deadline_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH),
                List.of("D" + DELIM + "0" + DELIM + "Submit report" + DELIM + "2025-10-15"),
                StandardOpenOption.CREATE);
        storage.load(tasks, reminders, habits);

        assertEquals(1, tasks.getCount());
        assertEquals("Submit report", tasks.getTask(0).getName());
        assertFalse(tasks.getTask(0).getDone());
    }

    /**
     * Tests that an Event task is saved correctly
     */
    @Test
    public void test_event_save() throws Exception {
        tasks.addEvent("Team meeting",
                new DateTimeArg(LocalDate.parse("2025-10-20")),
                new DateTimeArg(LocalDate.parse("2025-10-22")));

        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        assertTrue(content.contains("E" + DELIM + "0" + DELIM + "Team meeting" + DELIM + "2025-10-20" + DELIM +
                "2025-10-22"));
    }

    /**
     * Tests that a saved Event task is read correctly
     */
    @Test
    public void test_event_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH),
                List.of("E" + DELIM + "0" + DELIM + "Team meeting" + DELIM + "2025-10-20" + DELIM + "2025-10-22"),
                StandardOpenOption.CREATE);
        storage.load(tasks, reminders, habits);

        assertEquals(1, tasks.getCount());
        assertEquals("Team meeting", tasks.getTask(0).getName());
        assertFalse(tasks.getTask(0).getDone());
    }

    /**
     * Tests that a one-time reminder is saved correctly
     */
    @Test
    public void test_oneTimeReminder_save() throws Exception {
        reminders.addReminderOneTime("Doctor appointment",
                new DateTimeArg(LocalDate.parse("2025-11-25")));

        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        //Should be On when reminder is created
        assertTrue(content.contains("R" + DELIM + "0" + DELIM + "1" + DELIM + "Doctor appointment" + DELIM +
                "2025-11-25"));
    }

    /**
     * Tests that a saved one-time reminder is read correctly
     */
    @Test
    public void test_oneTimeReminder_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH),
                List.of("R" + DELIM + "0" + DELIM + "0" + DELIM + "Doctor appointment" + DELIM + "2025-10-25"),
                StandardOpenOption.CREATE);
        storage.load(tasks, reminders, habits);

        assertEquals(1, reminders.getCount());
        assertEquals("Doctor appointment", reminders.getReminder(0).getName());
        assertTrue(reminders.getReminder(0).getOnReminder());
    }

    /**
     * Tests that a recurring reminder is saved correctly
     */
    @Test
    public void test_recurringReminder_save() throws Exception {
        reminders.addReminderRec("Weekly meeting",
                new DateTimeArg(LocalDate.parse("2025-11-25")),
                Duration.ofDays(7));

        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        //Should be On when reminder is created
        assertTrue(content.contains("R" + DELIM + "1" + DELIM + "1" + DELIM + "Weekly meeting" + DELIM + "2025-11-25" +
                DELIM + "PT168H"));
    }

    /**
     * Tests that a saved recurring reminder is read correctly
     */
    @Test
    public void test_recurringReminder_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH),
                List.of("R" + DELIM + "1" + DELIM + "0" + DELIM + "Weekly meeting" + DELIM + "2025-10-25" + DELIM +
                        "P7D"),
                StandardOpenOption.CREATE);
        storage.load(tasks, reminders, habits);

        assertEquals(1, reminders.getCount());
        assertEquals("Weekly meeting", reminders.getReminder(0).getName());
        assertTrue(reminders.getReminder(0).getOnReminder());
    }

    /**
     * Tests that a habit is saved correctly
     */
    @Test
    public void test_habit_save() throws Exception {
        habits.addHabit("Morning routine",
                new DateTimeArg(LocalDate.parse("2025-10-27")),
                Duration.ofDays(1),
                5);

        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        assertTrue(content.contains("H" + DELIM + "Morning routine" + DELIM + "2025-10-27" + DELIM + "PT24H" +
                DELIM + "5"));
    }

    /**
     * Tests that a saved habit is read correctly
     */
    @Test
    public void test_habit_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH),
                List.of("H" + DELIM + "Morning routine" + DELIM + "2025-10-27" + DELIM + "PT24H" + DELIM + "5"),
                StandardOpenOption.CREATE);
        storage.load(tasks, reminders, habits);

        assertEquals(1, habits.getCount());
        Habit habit = habits.getHabit(0);
        assertTrue(habit.toString().contains("Morning routine"));
        assertEquals(5, habit.getStreak());
    }

    /**
     * Tests that invalid lines are skipped during loading and valid entries are still loaded
     */
    @Test
    public void test_invalidLine_skipped() throws Exception {
        // Create a file with mixed valid and invalid lines
        Files.write(Paths.get(TEST_FILE_PATH),
                List.of(
                        "T" + DELIM + "1" + DELIM + "Valid todo",
                        "INVALID_LINE_WITH_NO_PROPER_FORMAT",
                        "D" + DELIM + "0" + DELIM + "Valid deadline" + DELIM + "2025-10-30",
                        "X" + DELIM + "unknown" + DELIM + "type",
                        "T" + DELIM,  // incomplete task
                        "E" + DELIM + "0" + DELIM + "Valid event" + DELIM + "2025-11-01" + DELIM + "2025-11-02"
                ),
                StandardOpenOption.CREATE);

        storage.load(tasks, reminders, habits);

        // Only the 3 valid entries should be loaded
        assertEquals(3, tasks.getCount());
        assertEquals("Valid todo", tasks.getTask(0).getName());
        assertEquals("Valid deadline", tasks.getTask(1).getName());
        assertEquals("Valid event", tasks.getTask(2).getName());
    }
}
