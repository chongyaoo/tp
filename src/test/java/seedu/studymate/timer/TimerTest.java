package seedu.studymate.timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.studymate.tasks.TaskList;
import seedu.studymate.tasks.Task;
import seedu.studymate.tasks.ToDo;

/**
 * Extensive unit tests for the Timer class, covering state transitions,
 * time calculation accuracy, and edge cases using mocked time passage (Thread.sleep).
 */
public class TimerTest {

    private static final long DEFAULT_DURATION_SEC = 5; // 5 seconds for easy testing
    private static final String DEFAULT_LABEL = "Focus Session";
    private static final int DELTA = 1; // Margin of error for time calculations
    private static final String STUB_TASK_NAME = "Review Git Workflow";

    private static class StubTaskList extends TaskList {
        private final ArrayList<Task> tasks = new ArrayList<>();

        public StubTaskList() {
            // Initializing with a task so getTask(0) works
            this.tasks.add(new ToDo(STUB_TASK_NAME));
        }

        @Override
        public Task getTask(int index) {
            if (index == 0) {
                return tasks.get(0);
            }
            throw new IndexOutOfBoundsException("StubTaskList only has index 0.");
        }
    }

    private TaskList mockTaskList;

    @BeforeEach
    void setup() {
        mockTaskList = new StubTaskList();
    }

    // Helper method for time assertions
    private void assertTimeWithinDelta(long expected, long actual) {
        assertTrue(actual >= expected - DELTA,
                String.format("Time check failed: Expected >= %d, but was %d", expected - DELTA, actual));
        assertTrue(actual <= expected + DELTA,
                String.format("Time check failed: Expected <= %d, but was %d", expected + DELTA, actual));
    }

    @Test
    void testConstructor_defaultLabel() {
        Timer timer = new Timer(DEFAULT_LABEL, DEFAULT_DURATION_SEC);
        assertEquals(TimerState.IDLE, timer.getState());
        assertEquals(DEFAULT_DURATION_SEC, timer.getRemainingTime());
        assertEquals(DEFAULT_LABEL, timer.getLabel());
    }

    @Test
    void testConstructor_linkedTaskLabel() {
        Timer timer = new Timer(mockTaskList, DEFAULT_DURATION_SEC, 0);
        assertEquals(TimerState.IDLE, timer.getState());
        // Label should be "Task #1 Review Git Workflow" (0-based index + 1)
        assertEquals("Task #1 Review Git Workflow", timer.getLabel());
    }

    @Test
    void testStart_transitionAndInitialTime() {
        Timer timer = new Timer(DEFAULT_LABEL, DEFAULT_DURATION_SEC);
        timer.start();
        assertEquals(TimerState.RUNNING, timer.getState());
        assertTrue(timer.getRemainingTime() <= DEFAULT_DURATION_SEC);
        assertTrue(timer.getRemainingTime() > 0);
    }

    @Test
    void testPause_transitionAndTimeUpdate() throws InterruptedException {
        Timer timer = new Timer(DEFAULT_LABEL, DEFAULT_DURATION_SEC);
        timer.start();

        // Wait for 2 seconds
        Thread.sleep(2000);

        timer.pause();
        assertEquals(TimerState.PAUSED, timer.getState());

        long expectedRemaining = DEFAULT_DURATION_SEC - 2;
        long actualRemaining = timer.getRemainingTime();

        // Check calculation with delta
        assertTimeWithinDelta(expectedRemaining, actualRemaining);
    }

    @Test
    void testResume_transitionAndFixedTime() throws InterruptedException {
        Timer timer = new Timer(DEFAULT_LABEL, DEFAULT_DURATION_SEC);
        timer.start();
        Thread.sleep(1000);
        timer.pause();

        long remainingBeforeResume = timer.getRemainingTime();

        timer.resume();
        assertEquals(TimerState.RUNNING, timer.getState());

        // Wait 2 seconds while running
        Thread.sleep(2000);

        // Pause again to update remainingSec
        timer.pause();
        long remainingAfterResume = timer.getRemainingTime();

        // Time elapsed should be 2 seconds less than before resume
        long expectedElapsed = 2;
        assertTimeWithinDelta(remainingBeforeResume - expectedElapsed, remainingAfterResume);
    }

    @Test
    void testComplexCycle_cumulativeAccuracy() throws InterruptedException {
        Timer timer = new Timer(DEFAULT_LABEL, 10); // 10s total

        timer.start();
        Thread.sleep(2000); // Elapsed: ~2s
        timer.pause();

        Thread.sleep(3000); // Paused: 3s (No change)

        timer.resume();
        Thread.sleep(1000); // Elapsed: ~1s
        timer.pause();

        // Total elapsed time should be approximately 3 seconds (2s + 1s)
        long expectedFinalRemaining = 10 - 3;
        long actualFinalRemaining = timer.getRemainingTime();

        assertEquals(TimerState.PAUSED, timer.getState());
        assertTimeWithinDelta(expectedFinalRemaining, actualFinalRemaining);
    }

    @Test
    void testGetRemainingTime_forcesTimeout() throws InterruptedException {
        // Set duration to 1 second
        Timer timer = new Timer(DEFAULT_LABEL, 1);
        timer.start();

        // Wait 2 seconds to ensure timer runs out
        Thread.sleep(2000);

        // Calling getRemainingTime() forces the calculation and state correction
        long remaining = timer.getRemainingTime();

        assertEquals(0, remaining);
        assertEquals(TimerState.IDLE, timer.getState());
    }

    @Test
    void testPause_forcesTimeout() throws InterruptedException {
        Timer timer = new Timer(DEFAULT_LABEL, 1);
        timer.start();
        Thread.sleep(2000);
        timer.pause();

        assertEquals(0, timer.getRemainingTime());
        assertEquals(TimerState.IDLE, timer.getState());
    }

    @Test
    void testReset_fromPaused_resetsToZero() {
        Timer timer = new Timer(DEFAULT_LABEL, DEFAULT_DURATION_SEC);
        timer.start();
        timer.pause();
        timer.reset();

        assertEquals(0, timer.getRemainingTime());
        assertEquals(TimerState.IDLE, timer.getState());
    }

    /**
     * Tests attempts at invalid state transitions (relying on CommandHandler to handle errors).
     * Since Timer methods are void, we ensure the state is unchanged.
     */
    @Test
    void testInvalidTransitions_stateUnchanged() {
        Timer timer = new Timer(DEFAULT_LABEL, DEFAULT_DURATION_SEC);

        // Test Pause from IDLE
        timer.pause();
        assertEquals(TimerState.IDLE, timer.getState(), "Pause from IDLE should not change state.");

        // Test Resume from IDLE
        timer.resume();
        assertEquals(TimerState.IDLE, timer.getState(), "Resume from IDLE should not change state.");

        // Start the timer
        timer.start();

        // Test Start from RUNNING
        timer.start();
        assertEquals(TimerState.RUNNING, timer.getState(), "Start from RUNNING should not change state.");

        // Test Resume from RUNNING
        timer.resume();
        assertEquals(TimerState.RUNNING, timer.getState(), "Resume from RUNNING should not change state.");
    }

    /**
     * Tests the accuracy of the remaining time after many small runs, checking for cumulative float error.
     */
    @Test
    void testAccuracy_manySmallCycles() throws InterruptedException {
        long duration = 10;
        Timer timer = new Timer(DEFAULT_LABEL, duration);
        long sleepTimeMs = 1100; // Increased sleep time to guarantee >= 1s of elapsed time per cycle
        timer.start();
        timer.pause();
        // Run and pause 5 times (total ~5.5s elapsed)
        for (int i = 0; i < 4; i++) {
            timer.resume();
            Thread.sleep(sleepTimeMs);
            timer.pause();
        }

        // Calculation: 10s - 5s = 5s
        long actualRemaining = timer.getRemainingTime();
        // Since we guarantee 1 second subtraction per cycle (5 cycles * 1s = 5s)
        long expectedRemaining = duration - 5;

        assertEquals(TimerState.PAUSED, timer.getState());
        // We expect the remaining time to be 5 seconds, +/- the DELTA of 1 second.
        assertTimeWithinDelta(expectedRemaining, actualRemaining);
    }

    /**
     * Tests the scenario where time runs out precisely during a pause call.
     */
    @Test
    void testPause_timeExpiresExactly() throws InterruptedException {
        Timer timer = new Timer(DEFAULT_LABEL, 2); // 2 seconds duration
        timer.start();

        // Wait slightly more than 2 seconds
        Thread.sleep(2100);

        timer.pause();

        assertEquals(0, timer.getRemainingTime());
        assertEquals(TimerState.IDLE, timer.getState());
    }
}
