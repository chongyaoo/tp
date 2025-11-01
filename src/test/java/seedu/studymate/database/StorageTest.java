package seedu.studymate.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import seedu.studymate.habits.Habit;
import seedu.studymate.habits.HabitList;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.reminders.Reminder;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.tasks.TaskList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
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

        //Should be On when reminder is created, and isFired should be 0 (false)
        assertTrue(content.contains("R" + DELIM + "0" + DELIM + "1" + DELIM + "Doctor appointment" + DELIM +
                "2025-11-25" + DELIM + "0"));
    }

    /**
     * Tests that a saved one-time reminder is read correctly when turned OFF
     */
    @Test
    public void test_oneTimeReminder_load_turnedOff() throws Exception {
        // Format: R|0|0|name|date|0
        // parts[2] = "0" means reminder was turned OFF
        // parts[5] = "0" means hasn't fired yet
        Files.write(Paths.get(TEST_FILE_PATH),
                List.of("R" + DELIM + "0" + DELIM + "0" + DELIM + "Doctor appointment" + DELIM + "2025-10-25" + DELIM + "0"),
                StandardOpenOption.CREATE);
        storage.load(tasks, reminders, habits);

        assertEquals(1, reminders.getCount());
        assertEquals("Doctor appointment", reminders.getReminder(0).getName());
        assertFalse(reminders.getReminder(0).getOnReminder()); // Should be OFF
    }

    /**
     * Tests that a saved one-time reminder is read correctly when turned ON
     */
    @Test
    public void test_oneTimeReminder_load_turnedOn() throws Exception {
        // Format: R|0|1|name|date|0
        // parts[2] = "1" means reminder was turned ON
        // parts[5] = "0" means hasn't fired yet
        Files.write(Paths.get(TEST_FILE_PATH),
                List.of("R" + DELIM + "0" + DELIM + "1" + DELIM + "Doctor appointment" + DELIM + "2025-11-25" + DELIM + "0"),
                StandardOpenOption.CREATE);
        storage.load(tasks, reminders, habits);

        assertEquals(1, reminders.getCount());
        assertEquals("Doctor appointment", reminders.getReminder(0).getName());
        assertTrue(reminders.getReminder(0).getOnReminder()); // Should be ON
    }

    /**
     * Tests that a one-time reminder that has already fired is saved with isFired flag
     */
    @Test
    public void test_oneTimeReminder_alreadyFired_save() throws Exception {
        // Create reminder with isFired = true
        reminders.addReminderOneTime("Past reminder",
                new DateTimeArg(LocalDate.parse("2025-10-20")), true);

        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        // Format: R|0|1|name|date|1
        // parts[5] = "1" (isFired = true)
        assertTrue(content.contains("R" + DELIM + "0" + DELIM + "1" + DELIM + "Past reminder" + DELIM +
                "2025-10-20" + DELIM + "1"));
    }

    /**
     * Tests that a one-time reminder that has already fired is loaded correctly
     * and doesn't fire again (bug fix test)
     */
    @Test
    public void test_oneTimeReminder_alreadyFired_load_preventsReFiring() throws Exception {
        // Format: R|0|0|name|date|1
        // parts[2] = "0" (onReminder = false - was turned off after firing)
        // parts[5] = "1" (isFired = true - already fired)
        Files.write(Paths.get(TEST_FILE_PATH),
                List.of("R" + DELIM + "0" + DELIM + "0" + DELIM + "Past reminder" + DELIM + "2025-10-20" + DELIM + "1"),
                StandardOpenOption.CREATE);
        storage.load(tasks, reminders, habits);

        assertEquals(1, reminders.getCount());
        Reminder reminder = reminders.getReminder(0);
        assertEquals("Past reminder", reminder.getName());
        assertFalse(reminder.getOnReminder()); // Should be OFF
        assertFalse(reminder.isDue()); // Should NOT be due (isFired = true prevents it)
    }

    /**
     * Tests that turning off a reminder is preserved across save/load
     */
    @Test
    public void test_oneTimeReminder_turnedOff_preservedOnLoad() throws Exception {
        // Create and turn off a reminder
        reminders.addReminderOneTime("Future reminder",
                new DateTimeArg(LocalDate.parse("2025-11-25")));
        reminders.getReminder(0).setOnReminder(false);

        // Save
        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());

        // Clear and reload
        TaskList newTasks = new TaskList();
        ReminderList newReminders = new ReminderList();
        HabitList newHabits = new HabitList();
        storage.load(newTasks, newReminders, newHabits);

        assertEquals(1, newReminders.getCount());
        Reminder reminder = newReminders.getReminder(0);
        assertFalse(reminder.getOnReminder()); // Should remain OFF after reload
    }

    /**
     * Tests that turning on a reminder is preserved across save/load
     */
    @Test
    public void test_oneTimeReminder_turnedOn_preservedOnLoad() throws Exception {
        // Create reminder (defaults to ON)
        reminders.addReminderOneTime("Future reminder",
                new DateTimeArg(LocalDate.parse("2025-11-25")));
        assertTrue(reminders.getReminder(0).getOnReminder()); // Verify it's ON

        // Save
        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());

        // Clear and reload
        TaskList newTasks = new TaskList();
        ReminderList newReminders = new ReminderList();
        HabitList newHabits = new HabitList();
        storage.load(newTasks, newReminders, newHabits);

        assertEquals(1, newReminders.getCount());
        Reminder reminder = newReminders.getReminder(0);
        assertTrue(reminder.getOnReminder()); // Should remain ON after reload
    }

    /**
     * Tests the complete lifecycle: create -> fire -> save -> load -> verify doesn't fire again
     */
    @Test
    public void test_oneTimeReminder_completeFiringLifecycle() throws Exception {
        // Create a past reminder that would be due
        reminders.addReminderOneTime("Past reminder",
                new DateTimeArg(LocalDate.parse("2025-10-20"), LocalTime.parse("12:00")));

        Reminder reminder = reminders.getReminder(0);
        assertTrue(reminder.isDue()); // Should be due (past date)

        // Fire the reminder
        reminder.isFired();
        assertFalse(reminder.getOnReminder()); // Should be turned off after firing
        assertFalse(reminder.isDue()); // Should no longer be due

        // Save
        storage.save(tasks.getTasks(), reminders.getReminders(), habits.getHabits());

        // Load into fresh lists
        TaskList newTasks = new TaskList();
        ReminderList newReminders = new ReminderList();
        HabitList newHabits = new HabitList();
        storage.load(newTasks, newReminders, newHabits);

        // Verify state is preserved
        assertEquals(1, newReminders.getCount());
        Reminder loadedReminder = newReminders.getReminder(0);
        assertFalse(loadedReminder.getOnReminder()); // Still OFF
        assertFalse(loadedReminder.isDue()); // Still not due (won't fire again!)
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
        assertFalse(reminders.getReminder(0).getOnReminder()); // parts[2]="0" means OFF
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
