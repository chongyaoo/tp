package seedu.studymate.habits;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageHandler;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a list of habits.
 * It provides methods for adding, deleting, listing, and incrementing streaks.
 */
public class HabitList {
    private static final Logger logger = Logger.getLogger("HabitList Logger");
    private static final int cap = 10000;
    private final ArrayList<Habit> habits;
    private final Clock clock;

    /**
     * Constructs an empty HabitList.
     */
    public HabitList(Clock clock) {
        habits = new ArrayList<>();
        this.clock = clock;
        logger.log(Level.INFO, "Created habitlist");
    }

    public HabitList() {
        this(Clock.systemDefaultZone());
        logger.log(Level.INFO, "Created habitlist");
    }

    /**
     * Retrieves a habit at a specific index.
     *
     * @param index The index of the habit to retrieve
     * @return The habit at the specified index
     */
    public Habit getHabit(int index) {
        logger.log(Level.INFO, "Habit retrieved: " + habits.get(index).toString());
        return habits.get(index);
    }

    /**
     * Retrieves all habits.
     *
     * @return The ArrayList of habits
     */
    public ArrayList<Habit> getAllHabits() {
        logger.log(Level.INFO, "Retrieved habits");
        return habits;
    }

    /**
     * Returns the total number of habits in the list.
     *
     * @return The number of habits
     */
    public int getCount() {
        logger.log(Level.INFO, "Habit list count retrieved: " + habits.size());
        return habits.size();
    }

    /**
     * Adds a habit to the list.
     *
     * @param name The name of the habit
     * @param interval The interval for the habit
     */
    public void addHabit(String name, Duration interval) throws StudyMateException {
        Habit newHabit = new Habit(name, interval, clock);
        if (habits.size() >= cap) {
            throw new StudyMateException("Too many habits! Please delete some to add in more.");
        }
        habits.add(newHabit);
        assert (habits.contains(newHabit));
        logger.log(Level.INFO, "Added Habit: " + newHabit);
        MessageHandler.sendAddHabitMessage(newHabit, habits.size());
    }

    /**
     * Adds a habit to the list with existing deadline and streak (for loading from file).
     * Does not send a message to the user.
     *
     * @param name The name of the habit
     * @param deadline The existing deadline for the habit
     * @param interval The interval for the habit
     * @param streak The existing streak count
     */
    public void addHabit(String name, DateTimeArg deadline, Duration interval, int streak) {
        Habit newHabit = new Habit(name, deadline, interval, streak, clock);
        habits.add(newHabit);
        assert (habits.contains(newHabit));
        logger.log(Level.INFO, "Loaded Habit: " + newHabit);
    }

    /**
     * Deletes a habit from the list at a specific index.
     *
     * @param index The index of the habit to delete
     */
    public void deleteHabit(int index) {
        Habit habit = habits.get(index);
        habits.remove(index);
        assert (!habits.contains(habit));
        logger.log(Level.INFO, "Deleted Habit: " + habit);
        MessageHandler.sendDeleteHabitMessage(habit, habits.size());
    }

    /**
     * Returns the list of all habits in this HabitList.
     *
     * @return An ArrayList containing all Habit objects in the list
     */
    public ArrayList<Habit> getHabits() {
        logger.log(Level.INFO, "Habit List Retrieved");
        return habits;
    }

    /**
     * Increments the streak of a habit at the specified index.
     *
     * @param index The index of the habit to increment
     * @return The StreakResult indicating the result of the increment operation
     */
    public StreakResult incStreak(int index) throws StudyMateException {
        Habit habit = habits.get(index);
        StreakResult result = habit.incStreak();
        logger.log(Level.INFO, "Attempted to increment streak for habit at index " + index + ": " + result);
        MessageHandler.sendIncStreakMessage(habit, result);
        return result;
    }
}
