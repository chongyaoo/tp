package seedu.studymate.habits;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.DateTimeArg;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HabitListTest {
    private HabitList habitList;

    @BeforeEach
    void setUp() {
        habitList = new HabitList();
    }

    @Test
    void testAddHabit_singleHabit() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        assertEquals(1, habitList.getCount());
    }

    @Test
    void testAddHabit_multipleHabits() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        habitList.addHabit("Read", Duration.ofHours(12));
        habitList.addHabit("Meditate", Duration.ofHours(6));
        assertEquals(3, habitList.getCount());
    }

    @Test
    void testGetHabit_validIndex() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        Habit habit = habitList.getHabit(0);
        assertNotNull(habit);
        assertEquals("Exercise", habit.toString().split(" ")[1]);
    }

    @Test
    void testGetCount_emptyList() {
        assertEquals(0, habitList.getCount());
    }

    @Test
    void testGetCount_afterAdding() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        habitList.addHabit("Read", Duration.ofHours(12));
        assertEquals(2, habitList.getCount());
    }

    @Test
    void testDeleteHabit_firstHabit() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        habitList.addHabit("Read", Duration.ofHours(12));
        habitList.addHabit("Meditate", Duration.ofHours(6));

        assertEquals(3, habitList.getCount());
        habitList.deleteHabit(0);
        assertEquals(2, habitList.getCount());
    }

    @Test
    void testDeleteHabit_middleHabit() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        habitList.addHabit("Read", Duration.ofHours(12));
        habitList.addHabit("Meditate", Duration.ofHours(6));

        habitList.deleteHabit(1);
        assertEquals(2, habitList.getCount());
    }

    @Test
    void testDeleteHabit_lastHabit() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        habitList.addHabit("Read", Duration.ofHours(12));
        habitList.addHabit("Meditate", Duration.ofHours(6));

        habitList.deleteHabit(2);
        assertEquals(2, habitList.getCount());
    }

    @Test
    void testDeleteHabit_onlyHabit() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        assertEquals(1, habitList.getCount());

        habitList.deleteHabit(0);
        assertEquals(0, habitList.getCount());
    }

    @Test
    void testGetHabits_emptyList(){
        assertEquals(0, habitList.getHabits().size());
    }

    @Test
    void testGetHabits_withHabits() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        habitList.addHabit("Read", Duration.ofHours(12));
        assertEquals(2, habitList.getHabits().size());
    }

    @Test
    void testIncStreak_validIndex() throws StudyMateException {
        // Add habit with deadline in the past so streak can be incremented
        LocalDateTime pastTime = LocalDateTime.of(2025, 10, 25, 11, 0);
        DateTimeArg pastDeadline = new DateTimeArg(pastTime.toLocalDate(), pastTime.toLocalTime());
        habitList.addHabit("Exercise", pastDeadline, Duration.ofDays(1), 0);

        StreakResult result = habitList.incStreak(0);
        // Result should be ON_TIME or TOO_LATE depending on grace period
        assertNotNull(result);
    }

    @Test
    void testAddHabit_withDeadlineAndStreak() {
        LocalDateTime deadline = LocalDateTime.of(2025, 10, 26, 12, 0);
        DateTimeArg dateTimeArg = new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime());
        habitList.addHabit("Exercise", dateTimeArg, Duration.ofDays(1), 5);

        assertEquals(1, habitList.getCount());
        assertEquals(5, habitList.getHabit(0).getStreak());
    }

    @Test
    void testGetAllHabits() throws StudyMateException {
        habitList.addHabit("Exercise", Duration.ofDays(1));
        habitList.addHabit("Read", Duration.ofHours(12));

        assertEquals(2, habitList.getAllHabits().size());
    }

    @Test
    void testSequentialOperations() throws StudyMateException {
        // Add habits
        habitList.addHabit("Exercise", Duration.ofDays(1));
        habitList.addHabit("Read", Duration.ofHours(12));
        habitList.addHabit("Meditate", Duration.ofHours(6));
        assertEquals(3, habitList.getCount());

        // Delete middle habit
        habitList.deleteHabit(1);
        assertEquals(2, habitList.getCount());

        // Add another habit
        habitList.addHabit("Study", Duration.ofHours(2));
        assertEquals(3, habitList.getCount());

        // Delete first habit
        habitList.deleteHabit(0);
        assertEquals(2, habitList.getCount());
    }

    @Test
    void testAddHabit_differentIntervals() throws StudyMateException {
        habitList.addHabit("Daily", Duration.ofDays(1));
        habitList.addHabit("Weekly", Duration.ofDays(7));
        habitList.addHabit("Hourly", Duration.ofHours(1));
        habitList.addHabit("Minutes", Duration.ofMinutes(30));

        assertEquals(4, habitList.getCount());
    }

    @Test
    void testLoadHabits_preservesData() {
        LocalDateTime deadline1 = LocalDateTime.of(2025, 10, 26, 8, 0);
        LocalDateTime deadline2 = LocalDateTime.of(2025, 10, 25, 20, 0);

        DateTimeArg dt1 = new DateTimeArg(deadline1.toLocalDate(), deadline1.toLocalTime());
        DateTimeArg dt2 = new DateTimeArg(deadline2.toLocalDate(), deadline2.toLocalTime());

        habitList.addHabit("Exercise", dt1, Duration.ofDays(1), 10);
        habitList.addHabit("Read", dt2, Duration.ofHours(12), 5);

        assertEquals(2, habitList.getCount());
        assertEquals(10, habitList.getHabit(0).getStreak());
        assertEquals(5, habitList.getHabit(1).getStreak());
    }

    @Test
    void testIncStreak_tooEarly() {
        // Fix time to October 25, 2025, 12:00:00
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 25, 12, 0, 0);
        Clock fixedClock = Clock.fixed(
                fixedTime.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );

        // Create habit with deadline in the future (October 25, 2025, 14:00)
        LocalDateTime futureDeadline = LocalDateTime.of(2025, 10, 25, 14, 0);
        DateTimeArg deadlineArg = new DateTimeArg(futureDeadline.toLocalDate(), futureDeadline.toLocalTime());

        habitList.addHabit("Exercise", deadlineArg, Duration.ofDays(1), 3);
        Habit habit = new Habit("Exercise", deadlineArg, Duration.ofDays(1), 3, fixedClock);

        // Try to increment streak before deadline
        StreakResult result = habit.incStreak();

        assertEquals(StreakResult.TOO_EARLY, result);
        assertEquals(3, habit.getStreak()); // Streak should not change
    }

    @Test
    void testIncStreak_wayTooLate() {
        // Fix time to October 25, 2025, 12:00:00
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 25, 12, 0, 0);
        Clock fixedClock = Clock.fixed(
                fixedTime.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );

        // Create habit with deadline way in the past (October 24, 2025, 12:00)
        // With a 1-day (24h) interval, grace period is 6h + 1min
        // 24 hours is way beyond the grace period
        LocalDateTime pastDeadline = LocalDateTime.of(2025, 10, 24, 12, 0);
        DateTimeArg deadlineArg = new DateTimeArg(pastDeadline.toLocalDate(), pastDeadline.toLocalTime());

        Habit habit = new Habit("Exercise", deadlineArg, Duration.ofDays(1), 10, fixedClock);

        // Try to increment streak way after deadline + grace period
        StreakResult result = habit.incStreak();

        assertEquals(StreakResult.TOO_LATE, result);
        assertEquals(1, habit.getStreak()); // Streak should reset to 1
    }

    @Test
    void testIncStreak_sameMinuteDifferentSeconds() {
        // Fix time to October 25, 2025, 12:00:00 (0th second)
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 25, 12, 0, 0);
        Clock fixedClock = Clock.fixed(
                fixedTime.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );

        // Create habit with deadline at 12:00:30 (30th second of the same minute)
        // User tries to streak at 12:00:00 (before the 30th second)
        LocalDateTime deadline = LocalDateTime.of(2025, 10, 25, 12, 0, 30);
        DateTimeArg deadlineArg = new DateTimeArg(deadline.toLocalDate(), deadline.toLocalTime());

        Habit habit = new Habit("Exercise", deadlineArg, Duration.ofDays(1), 5, fixedClock);

        // Try to increment streak at same minute but different seconds
        StreakResult result = habit.incStreak();

        // Should be ON_TIME because both truncate to 12:00 (minutes are the same)
        assertEquals(StreakResult.ON_TIME, result);
        assertEquals(6, habit.getStreak()); // Streak should increment
    }

    @Test
    void testIncStreak_withinGracePeriod() {
        // Fix time to October 25, 2025, 12:00:00
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 25, 12, 0, 0);
        Clock fixedClock = Clock.fixed(
                fixedTime.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );

        // Create habit with 1-day (24h) interval, deadline 5 hours ago (October 25, 2025, 07:00)
        // Grace period = 24h / 4 + 1min = 6h 1min
        // 5 hours after deadline is within the grace period
        LocalDateTime pastDeadline = LocalDateTime.of(2025, 10, 25, 7, 0);
        DateTimeArg deadlineArg = new DateTimeArg(pastDeadline.toLocalDate(), pastDeadline.toLocalTime());

        Habit habit = new Habit("Exercise", deadlineArg, Duration.ofDays(1), 8, fixedClock);

        // Try to increment streak within grace period
        StreakResult result = habit.incStreak();

        // Should be ON_TIME because we're within the grace period (6h 1min)
        assertEquals(StreakResult.ON_TIME, result);
        assertEquals(9, habit.getStreak()); // Streak should increment from 8 to 9
    }

    // Test that adding habits beyond 10000 limit throws exception
    @Test
    void testAddHabit_exceedsCapacity_throwsException() throws StudyMateException {
        // Add 10000 habits (the maximum allowed)
        for (int i = 0; i < 10000; i++) {
            habitList.addHabit("Habit " + i, Duration.ofDays(1));
        }
        assertEquals(10000, habitList.getCount());

        // Attempting to add the 10001st habit should throw exception
        assertThrows(StudyMateException.class,
                () -> habitList.addHabit("Habit 10001", Duration.ofDays(1)));
        assertEquals(10000, habitList.getCount()); // Count should remain at 10000
    }
}
