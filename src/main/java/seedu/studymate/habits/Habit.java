package seedu.studymate.habits;

import seedu.studymate.database.DataFormatting;
import seedu.studymate.ui.MessageFormatting;
import seedu.studymate.parser.DateTimeArg;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a habit with a recurring deadline and streak tracking.
 * Habits allow users to track their consistency in completing recurring tasks.
 */
public class Habit {
    private static final int gracePeriod = 4;
    private final String name;
    private DateTimeArg deadline;
    private final Duration interval;
    private int streak;
    private final Clock clock;
    // the grace period is defined as deadline.getDateTime().plus(interval.dividedBy(n)), n is the denominator for the
    // extra period, defined as (1/n) * interval

    /**
     * Constructs a new Habit with an initial streak of 1.
     *
     * @param name The name of the habit
     * @param interval The time interval between habit deadlines
     * @param clock The clock to use for time operations
     */
    public Habit(String name, Duration interval, Clock clock) {
        LocalDateTime deadline = LocalDateTime.now(clock).plus(interval);
        this.name = name;
        this.deadline = new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime());
        this.interval = interval;
        this.clock = clock;
        streak = 1;
    }

    /**
     * Constructs a Habit with existing data (used when loading from file).
     *
     * @param name The name of the habit
     * @param deadline The current deadline for the habit
     * @param interval The time interval between habit deadlines
     * @param streak The current streak count
     * @param clock The clock to use for time operations
     */
    public Habit(String name, DateTimeArg deadline, Duration interval, int streak, Clock clock) {
        this.name = name;
        this.deadline = deadline;
        this.interval = interval;
        this.streak = streak;
        this.clock = clock;
    }

    /**
     * Attempts to increment the habit streak.
     * Checks if the current time is within the valid window (after deadline but within grace period).
     * If too early, returns TOO_EARLY. If too late, resets streak to 1 and returns TOO_LATE.
     * If on time, increments streak and returns ON_TIME.
     *
     * @return The result of the increment attempt
     */
    public StreakResult incStreak() {
        LocalDateTime now = LocalDateTime.now(clock);
        if (now.isBefore(deadline.getDateTime().truncatedTo(ChronoUnit.MINUTES))) {
            return StreakResult.TOO_EARLY;
        } else if (now.isAfter(deadline.getDateTime().plus(interval.dividedBy(gracePeriod)).plusMinutes(1))) {
            streak = 1;
            LocalDateTime deadline = LocalDateTime.now(clock).plus(interval);
            this.deadline = new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime());
            return StreakResult.TOO_LATE;
        } else {
            streak += 1;
            LocalDateTime deadline = LocalDateTime.now(clock).plus(interval);
            this.deadline = new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime());
            return StreakResult.ON_TIME;
        }
    }

    /**
     * Gets the current streak count.
     *
     * @return The streak count
     */
    public int getStreak() {
        return streak;
    }

    /**
     * Returns a human-readable string representation of the habit.
     *
     * @return The formatted string for display
     */
    @Override
    public String toString() {
        return MessageFormatting.habitString(name, deadline, streak);
    }

    /**
     * Returns a string representation suitable for saving to a file.
     *
     * @return The formatted string for file storage
     */
    public String toSaveString() {
        return DataFormatting.habitString(name, deadline, interval, streak);
    }
}
