package seedu.studymate.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.tasks.ReminderList;
import seedu.studymate.tasks.TaskList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageTest {

    private static final String TEST_FILE_PATH = "test_data/test_storage.txt";
    private Storage storage;
    private TaskList tasks;
    private ReminderList reminders;

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

        storage.save(tasks.getTasks(), reminders.getReminders());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        assertTrue(content.contains("T|1|Read book"));
    }

    /**
     * Test that a saved Todo task is read correctly
     */
    @Test
    public void test_todo_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH), List.of("T|1|Read book"), StandardOpenOption.CREATE);
        storage.load(tasks, reminders);

        assertEquals(1, tasks.getCount());
        assertEquals("Read book", tasks.getTask(0).getName());
        assertTrue(tasks.getTask(0).isDone());
    }

    /**
     * Tests that a Deadline task is saved correctly
     */
    @Test
    public void test_deadline_save() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addDeadline("Submit report", new DateTimeArg(LocalDate.parse("2025-10-15")));

        storage.save(tasks.getTasks(), reminders.getReminders());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        assertTrue(content.contains("D|0|Submit report|2025-10-15"));
    }

    /**
          * Tests that a saved Deadline task is read correctly
     */
    @Test
    public void test_deadline_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH), List.of("D|0|Submit report|2025-10-15"), StandardOpenOption.CREATE);
        storage.load(tasks, reminders);

        assertEquals(1, tasks.getCount());
        assertEquals("Submit report", tasks.getTask(0).getName());
        assertFalse(tasks.getTask(0).isDone());
    }
}
