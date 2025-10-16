package seedu.studymate.timer;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.studymate.tasks.TaskList;

/**
 * Represents a timer to be used for focus sessions
 */
public class Timer {
    private TimerState state;               // State of timer
    private long remainingSec;              // Time remaining
    private Instant startedAt;              // Timer start time
    private final String label;             // Timer label

    private static final Logger logger = Logger.getLogger("Timer Logger"); // Logger

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
        assert (state == TimerState.IDLE);
        state = TimerState.RUNNING;
        startedAt = Instant.now();
        assert (state == TimerState.RUNNING);
        logger.log(Level.INFO, "Started Timer");
    }

    /**
     * Pauses the timer
     */
    public void pause() {
        assert(state == TimerState.RUNNING);
        // Calculate time elapsed during RUNNING state
        Duration elapsed = Duration.between(startedAt, Instant.now());

        // Subtract elapsed time from remainingSec
        remainingSec -= elapsed.getSeconds();

        if (remainingSec <= 0) {
            remainingSec = 0;
            state = TimerState.IDLE;
            logger.log(Level.INFO, "Timer ended");
        } else {
            state = TimerState.PAUSED;
            logger.log(Level.INFO, "Timer paused");
        }
    }

    /**
     * Resumes the timer
     */
    public void resume() {
        assert(state == TimerState.PAUSED);
        startedAt = Instant.now();
        state = TimerState.RUNNING;
        logger.log(Level.INFO, "Timer resumed");
    }

    /**
     * Resets the timer
     */
    public void reset() {
        state = TimerState.IDLE;
        remainingSec = 0;
        logger.log(Level.INFO, "Timer reset");
    }

    public long getRemainingTime() {
        if (state == TimerState.IDLE || state == TimerState.PAUSED) {
            return remainingSec;
        }

        assert (state == TimerState.RUNNING);
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
        logger.log(Level.INFO, "Retrieve timer label: " + label);
        return label;
    }

    @Override
    public String toString() {
        int[] formattedTime = formatDuration(remainingSec);
        String output = "Timer Status\n"
                + "  State: " + state.toString() + "\n"
                + "  Time Left: " + formattedTime[0] + ":" + formattedTime[1] + "\n"
                + "  Label: " + label;
        logger.log(Level.INFO, "Retrieve timer string:\n" + output);
        return output;
    }

    private static int[] formatDuration(long totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        logger.log(Level.INFO, "Format duration: " + minutes + ":" + seconds);
        return new int[]{minutes, seconds};
    }
}
