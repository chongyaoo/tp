package seedu.studymate.database;

import org.junit.jupiter.api.*;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.tasks.TaskList;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    private static final String TEST_FILE_PATH = "test_data/test_storage.txt";
    private Storage storage;

    /**
     * Creates a temporary test file to see test the storage class
     */
    @BeforeEach
    public void setUp() throws IOException {
        Files.createDirectories(Paths.get("test_data"));
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        storage = new Storage(TEST_FILE_PATH);
    }

    /**
     * Deletes the test file for a clean ui
     */
    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
    }

    /**
     * Tests that a Todo ask is saved correctly
     */
    @Test
    public void test_todo_save() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addToDo("Read book");
        tasks.getTask(0).setDone(true);

        storage.save(tasks.getTasks());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        assertTrue(content.contains("T|1|Read book"));
    }

    /**
     * Test that a saved Todo task is read correctly
     */
    @Test
    public void test_todo_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH), List.of("T|1|Read book"), StandardOpenOption.CREATE);

        TaskList loaded = new TaskList();
        storage.load(loaded);

        assertEquals(1, loaded.getCount());
        assertEquals("Read book", loaded.getTask(0).getDescription());
        assertTrue(loaded.getTask(0).isDone());
    }

    /**
     * Tests that a Deadline task is saved correctly
     */
    @Test
    public void test_deadline_save() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addDeadline("Submit report", "2025-10-15");

        storage.save(tasks.getTasks());
        String content = Files.readString(Paths.get(TEST_FILE_PATH));

        assertTrue(content.contains("D|0|Submit report|2025-10-15"));
    }

    /**
     * Tests that a saved Deadline task is read correctedly
     */
    @Test
    public void test_deadline_load() throws Exception {
        Files.write(Paths.get(TEST_FILE_PATH), List.of("D|0|Submit report|2025-10-15"), StandardOpenOption.CREATE);

        TaskList loaded = new TaskList();
        storage.load(loaded);

        assertEquals(1, loaded.getCount());
        assertEquals("Submit report", loaded.getTask(0).getDescription());
        assertFalse(loaded.getTask(0).isDone());
    }
}