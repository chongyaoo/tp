package seedu.studymate.reminders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.studymate.parser.DateTimeArg;

public class RemListTest {
    ReminderList reminderList;

    @BeforeEach
    public void setup() {
        reminderList = new ReminderList();
    }

    // --- Test Cases for Adding Reminders ---

    @Test
    void testAddOneTime() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);

        reminderList.addReminderOneTime("running", remDateTime);
        assertEquals(1, reminderList.getCount());
        Reminder rem = reminderList.getReminder(0);
        assertInstanceOf(Reminder.class, rem);
        assertEquals("running", rem.getName());
    }

    @Test
    void testAddRecurring() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);
        Duration interval = Duration.ofDays(7);

        reminderList.addReminderRec("Weekly meeting", remDateTime, interval);
        assertEquals(1, reminderList.getCount());
        Reminder rem = reminderList.getReminder(0);
        assertInstanceOf(Reminder.class, rem);
        assertEquals("Weekly meeting", rem.getName());
        assertTrue(rem.getOnReminder());
    }

    // --- Test Cases for Reminder Status ---

    @Test
    void testSetRemindedOneTime() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);

        reminderList.addReminderOneTime("Doctor appointment", remDateTime);
        Reminder rem = reminderList.getReminder(0);

        // Initially, reminder should be on when created
        assertTrue(rem.getOnReminder());

        // Set as reminded
        rem.setOnReminder(true);
        assertEquals(true, rem.getOnReminder());

        // Set back to not reminded
        rem.setOnReminder(false);
        assertFalse(rem.getOnReminder());
    }

    @Test
    void testSetRemindedRecurring() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);
        Duration interval = Duration.ofDays(7);

        reminderList.addReminderRec("Weekly meeting", remDateTime, interval);
        Reminder rem = reminderList.getReminder(0);

        // Initially, reminder should be on when created
        assertTrue(rem.getOnReminder());

        // Set as reminded
        rem.setOnReminder(true);
        assertEquals(true, rem.getOnReminder());

        // Set back to not reminded
        rem.setOnReminder(false);
        assertFalse(rem.getOnReminder());
    }

    // --- Test Cases for Deletion ---

    // Helper function to populate a list to test for deletion
    private void populateListForDeletion() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);

        reminderList.addReminderOneTime("running", remDateTime);
        reminderList.addReminderOneTime("running 5km", remDateTime);
        reminderList.addReminderOneTime("running 10km", remDateTime);
        reminderList.addReminderOneTime("running 15km", remDateTime);
    }

    // Helper function to populate a list with recurring reminders for deletion
    private void populateListWithRecurringForDeletion() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);
        Duration weeklyInterval = Duration.ofDays(7);
        Duration dailyInterval = Duration.ofDays(1);

        reminderList.addReminderRec("Weekly standup", remDateTime, weeklyInterval);
        reminderList.addReminderRec("Daily exercise", remDateTime, dailyInterval);
        reminderList.addReminderRec("Weekly review", remDateTime, weeklyInterval);
        reminderList.addReminderRec("Daily meditation", remDateTime, dailyInterval);
    }

    //test for deleting one reminder
    @Test
    void testDeleteSingleReminder() {
        populateListForDeletion();
        assertEquals(4, reminderList.getCount());

        // Delete Reminder index 3
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>(Collections.singletonList(2));
        reminderList.delete(indexes);

        assertEquals(3, reminderList.getCount());
        assertEquals("running", reminderList.getReminder(0).getName());
        assertEquals("running 5km", reminderList.getReminder(1).getName()); // Task B remains at index 1
        assertEquals("running 15km", reminderList.getReminder(2).getName()); // Task D shifts from 3 to 2
    }

    // Test deleting 2 tasks
    @Test
    void testDeleteMultipleReminders() {
        populateListForDeletion();

        // Delete Reminders 2 and 4
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(1);
        indexes.add(3);
        reminderList.delete(indexes);

        assertEquals(2, reminderList.getCount());
        assertEquals("running", reminderList.getReminder(0).getName());
        assertEquals("running 10km", reminderList.getReminder(1).getName()); // Task C shifts from 2 to 1
    }

    // Test deleting one recurring reminder
    @Test
    void testDeleteSingleRecurringReminder() {
        populateListWithRecurringForDeletion();
        assertEquals(4, reminderList.getCount());

        // Delete Reminder at index 2 (Weekly review)
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>(Collections.singletonList(2));
        reminderList.delete(indexes);

        assertEquals(3, reminderList.getCount());
        assertEquals("Weekly standup", reminderList.getReminder(0).getName());
        assertEquals("Daily exercise", reminderList.getReminder(1).getName());
        assertEquals("Daily meditation", reminderList.getReminder(2).getName()); // Shifted from index 3
    }

    // Test deleting multiple recurring reminders
    @Test
    void testDeleteMultipleRecurringReminders() {
        populateListWithRecurringForDeletion();

        // Delete Reminders at index 0 and 2 (Weekly standup and Weekly review)
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        indexes.add(2);
        reminderList.delete(indexes);

        assertEquals(2, reminderList.getCount());
        assertEquals("Daily exercise", reminderList.getReminder(0).getName()); // Shifted from index 1
        assertEquals("Daily meditation", reminderList.getReminder(1).getName()); // Shifted from index 3
    }

    //--TestCases for checking isDue()--

    // Helper function to populate a list to test
    private void populateListOneTimeDue() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 9, 9, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);
        reminderList.addReminderOneTime("running", remDateTime);

        dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        date = dateTime.toLocalDate();
        time = dateTime.toLocalTime();
        remDateTime = new DateTimeArg(date, time);
        reminderList.addReminderOneTime("running 15km", remDateTime);
    }

    private void populateListOneTimeNotDue() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 9, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);
        reminderList.addReminderOneTime("running", remDateTime);

        dateTime = LocalDateTime.of(2026, 10, 10, 18, 0);
        date = dateTime.toLocalDate();
        time = dateTime.toLocalTime();
        remDateTime = new DateTimeArg(date, time);
        reminderList.addReminderOneTime("running 15km", remDateTime);
    }


    @Test
    void reminderOneTimeIsDue_whenPastDateTime() {
        populateListOneTimeDue();
        for (Reminder i : reminderList.getReminders()) {
            assertTrue(i.isDue());
        }
    }

    @Test
    void reminderOneTimeIsFired() {
        populateListOneTimeDue();
        for (Reminder i : reminderList.getReminders()) {
            assertTrue(i.isDue()); //both should be due
            i.isFired(); // mark as fired
            assertFalse(i.isDue(), "After firing, reminder must not be due");
        }
    }

    @Test
    void reminderOneTimeIsNotDue() {
        populateListOneTimeNotDue();
        for (Reminder i : reminderList.getReminders()) {
            assertFalse(i.isDue());
        }
    }

    private void populateListRecDue() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 9, 9, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);
        Duration duration = Duration.ofDays(1);
        reminderList.addReminderRec("running", remDateTime, duration);

        dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        date = dateTime.toLocalDate();
        time = dateTime.toLocalTime();
        remDateTime = new DateTimeArg(date, time);
        duration = Duration.ofHours(2);
        reminderList.addReminderRec("running 15km", remDateTime, duration);
    }

    @Test
    void reminderRecIsDue_whenPastDateTime() {
        populateListRecDue();
        for (Reminder i : reminderList.getReminders()) {
            assertTrue(i.isDue());
        }
    }

    @Test
    void recurringReminder_resetsAfterFired() {
        LocalDateTime fixedNow = LocalDateTime.of(2025, 10, 23, 12, 0);
        LocalDateTime startTime = fixedNow.minusDays(2); // schedule is already overdue
        DateTimeArg remDateTime = new DateTimeArg(startTime.toLocalDate(), startTime.toLocalTime());
        Duration interval = Duration.ofDays(1);

        reminderList.addReminderRec("Daily reminder to take a shit", remDateTime, interval);

        Reminder recReminder = reminderList.getReminder(0);
        assertTrue(recReminder.isDue()); // first time due

        recReminder.isFired(); // simulate firing, moves remindAt forward
        LocalDateTime newTarget = LocalDateTime.of(
                recReminder.remindAt.getDate(),
                recReminder.remindAt.getTime());

        // Expected next time is "now plus 1 day"
        LocalDateTime expected = fixedNow.plusDays(1);

        assertEquals(expected.toLocalDate(), newTarget.toLocalDate(),
                "Next scheduled date should be exactly tomorrow");
        assertEquals(expected.toLocalTime().getHour(), newTarget.getHour(),
                "Next scheduled hour should match the original time");

        assertFalse(recReminder.isDue());
    }
}
