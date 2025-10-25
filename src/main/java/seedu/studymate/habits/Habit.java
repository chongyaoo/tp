package seedu.studymate.habits;

import seedu.studymate.database.DataFormatting;
import seedu.studymate.ui.MessageFormatting;
import seedu.studymate.parser.DateTimeArg;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Habit {
    private static final int gracePeriod = 4;
    private final String name;
    private DateTimeArg deadline;
    private final Duration interval;
    private int streak;
    private final Clock clock;
    // the grace period is defined as deadline.getDateTime().plus(interval.dividedBy(n)), n is the denominator for the
    // extra period, defined as (1/n) * interval

    public Habit(String name, Duration interval, Clock clock) {
        LocalDateTime deadline = LocalDateTime.now(clock).plus(interval);
        this.name = name;
        this.deadline = new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime());
        this.interval = interval;
        this.clock = clock;
        streak = 1;
    }

    public Habit(String name, DateTimeArg deadline, Duration interval, int streak, Clock clock) {
        this.name = name;
        this.deadline = deadline;
        this.interval = interval;
        this.streak = streak;
        this.clock = clock;
    }

    // allow grace period of 1/4 later than the interval, plus 1 minute
    // also accept same minute even if deadline is 59th second and user does 0th second
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

    public int getStreak() {
        return streak;
    }

    @Override
    public String toString() {
        return MessageFormatting.habitString(name, deadline, streak);
    }

    public String toSaveString() {
        return DataFormatting.habitString(name, deadline, interval, streak);
    }
}
