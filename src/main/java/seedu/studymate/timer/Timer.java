package seedu.studymate.timer;

import java.time.Duration;
import java.time.Instant;

import seedu.studymate.tasks.TaskList;

/**
 * Represents a timer to be used for focus sessions
 */
public class Timer {
    private TimerState state;               // State of timer
    private long remainingSec;              // Time remaining
    private Instant startedAt;              // Timer start time
    private final String label;             // Timer label

    /**
     * Constructs a timer with a link to task
     *
     * @param durationSec Duration of the timer
     * @param taskIndex   Index of task in taskList
     */
    public Timer(TaskList taskList, long durationSec, Integer taskIndex) {
        this.state = TimerState.IDLE;
        this.remainingSec = durationSec;
        this.label = "Task #" + (taskIndex + 1) + " " +
                taskList.getTask(taskIndex).getName();
    }

    /**
     * Constructs a timer with a label string
     *
     * @param label       Label of the timer
     * @param durationSec Duration of the timer
     */
    public Timer(String label, long durationSec) {
        this.state = TimerState.IDLE;
        this.remainingSec = durationSec;
        this.label = label;
    }

    /**
     * Starts the timer
     */
    public void start() {
        state = TimerState.RUNNING;
        startedAt = Instant.now();
    }

    /**
     * Pauses the timer
     */
    public void pause() {
        // Calculate time elapsed during RUNNING state
        Duration elapsed = Duration.between(startedAt, Instant.now());

        // Subtract elapsed time from remainingSec
        remainingSec -= elapsed.getSeconds();

        if (remainingSec <= 0) {
            remainingSec = 0;
            state = TimerState.IDLE;
        } else {
            state = TimerState.PAUSED;
        }
    }

    /**
     * Resumes the timer
     */
    public void resume() {
        startedAt = Instant.now();
        state = TimerState.RUNNING;
    }

    /**
     * Resets the timer
     */
    public void reset() {
        state = TimerState.IDLE;
        remainingSec = 0;
    }

    public long getRemainingTime() {
        if (state == TimerState.IDLE || state == TimerState.PAUSED) {
            return remainingSec;
        }

        Duration elapsed = Duration.between(startedAt, Instant.now());
        long currentRemaining = remainingSec - elapsed.getSeconds();

        if (currentRemaining <= 0) {
            remainingSec = 0;
            state = TimerState.IDLE;
            return 0;
        }
        return currentRemaining;
    }

    public TimerState getState() {
        return state;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        int[] formattedTime = formatDuration(remainingSec);
        return "Timer Status\n"
                + "  State: " + state.toString() + "\n"
                + "  Time Left: " + formattedTime[0] + ":" + formattedTime[1] + "\n"
                + "  Label: " + label;
    }

    private static int[] formatDuration(long totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        return new int[]{minutes, seconds};
    }
}
