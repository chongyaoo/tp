package seedu.studymate.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.tasks.ReminderList;
import seedu.studymate.tasks.TaskList;

import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandHandlerTest {
    private TaskList taskList;
    private ReminderList reminderList;
    private Parser parser;

    @BeforeEach
    void setup() {
        taskList = new TaskList();
        reminderList = new ReminderList();
        parser = new Parser();

        // Add some test tasks
        taskList.addToDo("Review Git Workflow");
        taskList.addToDo("Study Java OOP");
        taskList.addToDo("Complete Assignment");

        // Cleanup any existing timer state before each test
        CommandHandler.cleanup();
    }

    // Timer Start Command Tests
    @Test
    void testHandleTimerStart_defaultTimer() {
        Command cmd = new Command(CommandType.START, null,  "Focus session", 25);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testHandleTimerStart_withTaskIndex() {
        Command cmd = new Command(CommandType.START, 0, 30);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testHandleTimerStart_withCustomLabel(){
        Command cmd = new Command(CommandType.START, "Study Math", 45);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testHandleTimerStart_invalidTaskIndex() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(5); // Out of bounds index
        Command cmd = new Command(CommandType.START, indexes);
        cmd.duration = 25;

        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testHandleTimerStart_negativeTaskIndex() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(-1); // Negative index
        Command cmd = new Command(CommandType.START, indexes);
        cmd.duration = 25;

        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testHandleTimerStart_whenTimerAlreadyActive() throws StudyMateException {
        // Start first timer
        Command cmd1 = new Command(CommandType.START, "First Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, cmd1);

        // Try to start second timer
        Command cmd2 = new Command(CommandType.START, "Second Timer", 30);

        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, cmd2));
    }

    @Test
    void testHandleTimerStart_withEmptyTaskList() {
        TaskList emptyTaskList = new TaskList();
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        Command cmd = new Command(CommandType.START, indexes);
        cmd.duration = 25;

        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(emptyTaskList, reminderList, cmd));
    }

    // Timer Control Command Tests
    @Test
    void testHandleTimerPause_whenRunning() throws StudyMateException {
        // Start a timer first
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, startCmd);

        // Pause the timer
        Command pauseCmd = new Command(CommandType.PAUSE);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, pauseCmd));
    }

    @Test
    void testHandleTimerPause_whenNoTimer() {
        Command pauseCmd = new Command(CommandType.PAUSE);
        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, pauseCmd));
    }

    @Test
    void testHandleTimerResume_whenPaused() throws StudyMateException {
        // Start and pause a timer
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, startCmd);

        Command pauseCmd = new Command(CommandType.PAUSE);
        CommandHandler.executeCommand(taskList, reminderList, pauseCmd);

        // Resume the timer
        Command resumeCmd = new Command(CommandType.RESUME);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, resumeCmd));
    }

    @Test
    void testHandleTimerResume_whenNoTimer() {
        Command resumeCmd = new Command(CommandType.RESUME);
        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, resumeCmd));
    }

    @Test
    void testHandleTimerReset_whenTimerExists() throws StudyMateException {
        // Start a timer first
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, startCmd);

        // Reset the timer
        Command resetCmd = new Command(CommandType.RESET);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, resetCmd));
    }

    @Test
    void testHandleTimerReset_whenNoTimer() {
        Command resetCmd = new Command(CommandType.RESET);
        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, resetCmd));
    }

    @Test
    void testHandleTimerStat_whenTimerExists() throws StudyMateException {
        // Start a timer first
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, startCmd);

        // Get timer stats
        Command statCmd = new Command(CommandType.STAT);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, statCmd));
    }

    @Test
    void testHandleTimerStat_whenNoTimer() {
        Command statCmd = new Command(CommandType.STAT);
        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, statCmd));
    }

    // Integration Tests with Parser
    @Test
    void testParseAndExecuteTimerStart_defaultDuration() throws StudyMateException {
        Command cmd = parser.parse("start");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Focus session", cmd.desc);
        assertEquals(Integer.valueOf(25), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withDuration() throws StudyMateException {
        Command cmd = parser.parse("start @45");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Integer.valueOf(45), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withLabel() throws StudyMateException {
        Command cmd = parser.parse("start Study Physics");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Study Physics", cmd.desc);
        assertEquals(Integer.valueOf(25), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withLabelAndDuration() throws StudyMateException {
        Command cmd = parser.parse("start Study Chemistry @60");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Study Chemistry", cmd.desc);
        assertEquals(Integer.valueOf(60), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withTaskIndex() throws StudyMateException {
        Command cmd = parser.parse("start 1");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Integer.valueOf(0), cmd.indexes.iterator().next()); // 1-based to 0-based
        assertEquals(Integer.valueOf(25), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withTaskIndexAndDuration() throws StudyMateException {
        Command cmd = parser.parse("start 2 @90");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Integer.valueOf(1), cmd.indexes.iterator().next()); // 1-based to 0-based
        assertEquals(Integer.valueOf(90), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    // Error Handling Tests
    @Test
    void testParseTimerStart_invalidDuration() {
        assertThrows(StudyMateException.class, () -> parser.parse("start @0"));
        assertThrows(StudyMateException.class, () -> parser.parse("start @-5"));
        assertThrows(StudyMateException.class, () -> parser.parse("start @abc"));
    }

    // Timer State Sequence Tests
    @Test
    void testTimerSequence_startPauseResumeReset() throws StudyMateException {
        // Start timer
        Command startCmd = parser.parse("start Test Session @30");
        CommandHandler.executeCommand(taskList, reminderList, startCmd);

        // Pause timer
        Command pauseCmd = parser.parse("pause");
        CommandHandler.executeCommand(taskList, reminderList, pauseCmd);

        // Resume timer
        Command resumeCmd = parser.parse("resume");
        CommandHandler.executeCommand(taskList, reminderList, resumeCmd);

        // Reset timer
        Command resetCmd = parser.parse("reset");
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, resetCmd));
    }

    @Test
    void testTimerDefaultValues() throws StudyMateException {
        Command cmd = parser.parse("start");

        // Verify default values
        assertEquals("Focus session", cmd.desc);
        assertEquals(Integer.valueOf(25), cmd.duration);

        // Should execute without error
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testCleanup_clearsActiveTimer() throws StudyMateException {
        // Start a timer
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, startCmd);

        // Cleanup should clear the active timer
        CommandHandler.cleanup();

        // Starting another timer should now work
        Command newStartCmd = new Command(CommandType.START, "New Timer", 30);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, newStartCmd));
    }

    // Edge Cases
    @Test
    void testTimerWithBoundaryValues() throws StudyMateException {
        // Test with minimum valid duration (1 minute)
        Command cmd1 = parser.parse("start @1");
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd1));

        CommandHandler.cleanup();

        // Test with large duration
        Command cmd2 = parser.parse("start @999");
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd2));
    }

    @Test
    void testTimerWithMaxTaskIndex() throws StudyMateException {
        // Test with the last valid task index
        Command cmd = parser.parse("start 3"); // Should be index 2 (0-based)
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, cmd));
    }

    @Test
    void testTimerCommandsAfterReset() throws StudyMateException {
        // Start and reset timer
        Command startCmd = parser.parse("start");
        CommandHandler.executeCommand(taskList, reminderList, startCmd);

        Command resetCmd = parser.parse("reset");
        CommandHandler.executeCommand(taskList, reminderList, resetCmd);

        // Should be able to start a new timer after reset
        Command newStartCmd = parser.parse("start New Session @45");
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, newStartCmd));
    }
}
