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
    private static final Logger logger = Logger.getLogger("Timer Logger"); // Logger
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
    public synchronized void start() {
        if (state != TimerState.IDLE) {
            return;
        }
        state = TimerState.RUNNING;
        startedAt = Instant.now();
        assert (state == TimerState.RUNNING);
        logger.log(Level.INFO, "Started Timer");
    }

    /**
     * Pauses the timer
     */
    public synchronized void pause() {
        if (state != TimerState.RUNNING) {
            return;
        }
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
    public synchronized void resume() {
        if (state != TimerState.PAUSED) {
            return;
        }
        startedAt = Instant.now();
        state = TimerState.RUNNING;
        logger.log(Level.INFO, "Timer resumed");
    }

    /**
     * Resets the timer
     */
    public synchronized void reset() {
        state = TimerState.IDLE;
        remainingSec = 0;
        logger.log(Level.INFO, "Timer reset");
    }

    public synchronized long getRemainingTime() {
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

    public synchronized TimerState getState() {
        return state;
    }

    public synchronized String getLabel() {
        logger.log(Level.INFO, "Retrieve timer label: " + label);
        return label;
    }

    @Override
    public synchronized String toString() {
        Duration elapsed = Duration.between(startedAt, Instant.now());
        long currentRemaining;
        if (state == TimerState.RUNNING) {
            currentRemaining = remainingSec - elapsed.getSeconds();
        } else if (state == TimerState.PAUSED) {
            currentRemaining = remainingSec;
        } else {
            currentRemaining = 0;
        }

        String[] formattedTime = formatDuration(currentRemaining);
        String output = "Timer Status\n"
                + "  State: " + state.toString() + "\n"
                + "  Time Left: " + formattedTime[0] + ":" + formattedTime[1] + "\n"
                + "  Label: " + label;
        logger.log(Level.INFO, "Retrieve timer string:\n" + output);
        return output;
    }

    private static String[] formatDuration(long totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        String formattedMinutes = minutes + "";
        String formattedSeconds = String.format("%02d", seconds);
        logger.log(Level.INFO, "Format duration: " + minutes + ":" + formattedSeconds);
        return new String[]{formattedMinutes, formattedSeconds};
    }
}
