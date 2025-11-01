package seedu.studymate.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.habits.HabitList;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.tasks.TaskList;

import java.time.Duration;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandHandlerTest {
    private TaskList taskList;
    private ReminderList reminderList;
    private HabitList habitList;
    private Parser parser;

    @BeforeEach
    void setup() throws StudyMateException {
        taskList = new TaskList();
        reminderList = new ReminderList();
        habitList = new HabitList();
        parser = new Parser();

        // Add some test tasks
        taskList.addToDo("Review Git Workflow");
        taskList.addToDo("Study Java OOP");
        taskList.addToDo("Complete Assignment");

        // Cleanup any existing timer state before each test
        CommandHandler.cleanup();
    }

    // Event Validation Test
    @Test
    void testHandleEvent_endTimeBeforeStartTime_throwsException() {
        // Create an event where end time (datetime1) is before start time (datetime0)
        DateTimeArg startTime = new DateTimeArg(
                java.time.LocalDate.of(2025, 10, 25),
                java.time.LocalTime.of(14, 0)
        );
        DateTimeArg endTime = new DateTimeArg(
                java.time.LocalDate.of(2025, 10, 25),
                java.time.LocalTime.of(12, 0) // 12:00 is before 14:00
        );

        Command cmd = new Command(CommandType.EVENT, "Team Meeting");
        cmd.datetime0 = startTime;
        cmd.datetime1 = endTime;

        // Should throw exception with message "End time cannot be earlier than start time"
        assertThrows(StudyMateException.class,
                () -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    // Time Validation Tests - Reject Past Times
    @Test
    void testHandleDeadline_timeInPast_throwsException() {
        // Create a deadline in the past (year 2020)
        DateTimeArg pastDeadline = new DateTimeArg(
                java.time.LocalDate.of(2020, 1, 1),
                java.time.LocalTime.of(10, 0)
        );

        Command cmd = new Command(CommandType.DEADLINE, "Old Task");
        cmd.datetime0 = pastDeadline;

        // Should throw exception because deadline is in the past
        assertThrows(StudyMateException.class,
                () -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testHandleEvent_endTimeInPast_throwsException() {
        // Create an event where end time is in the past
        DateTimeArg startTime = new DateTimeArg(
                java.time.LocalDate.of(2020, 1, 1),
                java.time.LocalTime.of(10, 0)
        );
        DateTimeArg endTime = new DateTimeArg(
                java.time.LocalDate.of(2020, 1, 1),
                java.time.LocalTime.of(12, 0)
        );

        Command cmd = new Command(CommandType.EVENT, "Past Event");
        cmd.datetime0 = startTime;
        cmd.datetime1 = endTime;

        // Should throw exception because end time is in the past
        assertThrows(StudyMateException.class,
                () -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testHandleRemAddOneTime_timeInPast_throwsException() {
        // Create a one-time reminder in the past
        DateTimeArg pastTime = new DateTimeArg(
                java.time.LocalDate.of(2020, 1, 1),
                java.time.LocalTime.of(10, 0)
        );

        Command cmd = new Command(CommandType.REM_ADD_ONETIME, "Old Reminder");
        cmd.datetime0 = pastTime;

        // Should throw exception because reminder time is in the past
        assertThrows(StudyMateException.class,
                () -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testHandleRemAddRec_timeInPast_throwsException() {
        // Create a recurring reminder starting in the past
        DateTimeArg pastTime = new DateTimeArg(
                java.time.LocalDate.of(2020, 1, 1),
                java.time.LocalTime.of(10, 0)
        );
        Duration interval = Duration.ofHours(24);

        Command cmd = new Command(CommandType.REM_ADD_REC);
        cmd.message = "Old Recurring Reminder";
        cmd.datetime0 = pastTime;
        cmd.interval = interval;

        // Should throw exception because reminder time is in the past
        assertThrows(StudyMateException.class,
                () -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    // Timer Start Command Tests
    @Test
    void testHandleTimerStart_defaultTimer() {
        Command cmd = new Command(CommandType.START, null,  "Focus session", 25);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testHandleTimerStart_withTaskIndex() {
        Command cmd = new Command(CommandType.START, 0, 30);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testHandleTimerStart_withCustomLabel(){
        Command cmd = new Command(CommandType.START, "Study Math", 45);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testHandleTimerStart_invalidTaskIndex() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(5); // Out of bounds index
        Command cmd = new Command(CommandType.START, indexes);
        cmd.duration = 25;

        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testHandleTimerStart_negativeTaskIndex() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(-1); // Negative index
        Command cmd = new Command(CommandType.START, indexes);
        cmd.duration = 25;

        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testHandleTimerStart_whenTimerAlreadyActive() throws StudyMateException {
        // Start first timer
        Command cmd1 = new Command(CommandType.START, "First Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, habitList, cmd1);

        // Try to start second timer
        Command cmd2 = new Command(CommandType.START, "Second Timer", 30);

        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd2));
    }

    @Test
    void testHandleTimerStart_withEmptyTaskList() {
        TaskList emptyTaskList = new TaskList();
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        Command cmd = new Command(CommandType.START, indexes);
        cmd.duration = 25;

        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(emptyTaskList, reminderList, habitList, cmd));
    }

    // Timer Control Command Tests
    @Test
    void testHandleTimerPause_whenRunning() throws StudyMateException {
        // Start a timer first
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, habitList, startCmd);

        // Pause the timer
        Command pauseCmd = new Command(CommandType.PAUSE);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, pauseCmd));
    }

    @Test
    void testHandleTimerPause_whenNoTimer() {
        Command pauseCmd = new Command(CommandType.PAUSE);
        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, habitList, pauseCmd));
    }

    @Test
    void testHandleTimerResume_whenPaused() throws StudyMateException {
        // Start and pause a timer
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, habitList, startCmd);

        Command pauseCmd = new Command(CommandType.PAUSE);
        CommandHandler.executeCommand(taskList, reminderList, habitList, pauseCmd);

        // Resume the timer
        Command resumeCmd = new Command(CommandType.RESUME);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, resumeCmd));
    }

    @Test
    void testHandleTimerResume_whenNoTimer() {
        Command resumeCmd = new Command(CommandType.RESUME);
        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, habitList, resumeCmd));
    }

    @Test
    void testHandleTimerReset_whenTimerExists() throws StudyMateException {
        // Start a timer first
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, habitList, startCmd);

        // Reset the timer
        Command resetCmd = new Command(CommandType.RESET);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, resetCmd));
    }

    @Test
    void testHandleTimerReset_whenNoTimer() {
        Command resetCmd = new Command(CommandType.RESET);
        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, habitList, resetCmd));
    }

    @Test
    void testHandleTimerStat_whenTimerExists() throws StudyMateException {
        // Start a timer first
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, habitList, startCmd);

        // Get timer stats
        Command statCmd = new Command(CommandType.STAT);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, statCmd));
    }

    @Test
    void testHandleTimerStat_whenNoTimer() {
        Command statCmd = new Command(CommandType.STAT);
        assertThrows(StudyMateException.class,
            () -> CommandHandler.executeCommand(taskList, reminderList, habitList, statCmd));
    }

    // Integration Tests with Parser
    @Test
    void testParseAndExecuteTimerStart_defaultDuration() throws StudyMateException {
        Command cmd = parser.parse("start");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Focus session", cmd.desc);
        assertEquals(Long.valueOf(25), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withDuration() throws StudyMateException {
        Command cmd = parser.parse("start @45");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Long.valueOf(45), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withLabel() throws StudyMateException {
        Command cmd = parser.parse("start Study Physics");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Study Physics", cmd.desc);
        assertEquals(Long.valueOf(25), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withLabelAndDuration() throws StudyMateException {
        Command cmd = parser.parse("start Study Chemistry @60");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Study Chemistry", cmd.desc);
        assertEquals(Long.valueOf(60), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withTaskIndex() throws StudyMateException {
        Command cmd = parser.parse("start 1");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Integer.valueOf(0), cmd.indexes.iterator().next()); // 1-based to 0-based
        assertEquals(Long.valueOf(25), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testParseAndExecuteTimerStart_withTaskIndexAndDuration() throws StudyMateException {
        Command cmd = parser.parse("start 2 @90");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Integer.valueOf(1), cmd.indexes.iterator().next()); // 1-based to 0-based
        assertEquals(Long.valueOf(90), cmd.duration);

        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
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
        CommandHandler.executeCommand(taskList, reminderList, habitList, startCmd);

        // Pause timer
        Command pauseCmd = parser.parse("pause");
        CommandHandler.executeCommand(taskList, reminderList, habitList, pauseCmd);

        // Resume timer
        Command resumeCmd = parser.parse("resume");
        CommandHandler.executeCommand(taskList, reminderList, habitList, resumeCmd);

        // Reset timer
        Command resetCmd = parser.parse("reset");
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, resetCmd));
    }

    @Test
    void testTimerDefaultValues() throws StudyMateException {
        Command cmd = parser.parse("start");

        // Verify default values
        assertEquals("Focus session", cmd.desc);
        assertEquals(Long.valueOf(25), cmd.duration);

        // Should execute without error
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testCleanup_clearsActiveTimer() throws StudyMateException {
        // Start a timer
        Command startCmd = new Command(CommandType.START, "Test Timer", 25);
        CommandHandler.executeCommand(taskList, reminderList, habitList, startCmd);

        // Cleanup should clear the active timer
        CommandHandler.cleanup();

        // Starting another timer should now work
        Command newStartCmd = new Command(CommandType.START, "New Timer", 30);
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, newStartCmd));
    }

    // Edge Cases
    @Test
    void testTimerWithBoundaryValues() throws StudyMateException {
        // Test with minimum valid duration (1 minute)
        Command cmd1 = parser.parse("start @1");
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd1));

        CommandHandler.cleanup();

        // Test with large duration
        Command cmd2 = parser.parse("start @999");
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd2));
    }

    @Test
    void testTimerWithMaxTaskIndex() throws StudyMateException {
        // Test with the last valid task index
        Command cmd = parser.parse("start 3"); // Should be index 2 (0-based)
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, cmd));
    }

    @Test
    void testTimerCommandsAfterReset() throws StudyMateException {
        // Start and reset timer
        Command startCmd = parser.parse("start");
        CommandHandler.executeCommand(taskList, reminderList, habitList, startCmd);

        Command resetCmd = parser.parse("reset");
        CommandHandler.executeCommand(taskList, reminderList, habitList, resetCmd);

        // Should be able to start a new timer after reset
        Command newStartCmd = parser.parse("start New Session @45");
        assertDoesNotThrow(() -> CommandHandler.executeCommand(taskList, reminderList, habitList, newStartCmd));
    }
}
