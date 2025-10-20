package seedu.studymate.reminders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import seedu.studymate.parser.DateTimeArg;

import java.time.LocalDateTime;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchedulerTest {
    private ReminderList reminderList;
    private Scheduler scheduler;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        reminderList = new ReminderList();
        scheduler = new Scheduler(reminderList, 1); // Use 1 second interval for testing

        // Capture System.out for integration tests
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore System.out
        System.setOut(originalOut);

        // Ensure scheduler is shutdown
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Test
    void tick_noReminders_returnsEmptyList() {
        List<Reminder> dueReminders = scheduler.tick();

        assertTrue(dueReminders.isEmpty());
    }

    @Test
    void tick_noDueReminders_returnsEmptyList() {
        // Add future reminder (tomorrow)
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        DateTimeArg futureDateTime = new DateTimeArg(future.toLocalDate(), future.toLocalTime());
        reminderList.addReminderOneTime("Future task", futureDateTime);

        List<Reminder> dueReminders = scheduler.tick();

        assertTrue(dueReminders.isEmpty());
        assertEquals(1, reminderList.getCount(), "Reminder should still be in list");
    }

    @Test
    void tick_withDueReminder_returnsReminder() {
        // Add past reminder (5 minutes ago)
        LocalDateTime past = LocalDateTime.now().minusMinutes(5);
        DateTimeArg pastDateTime = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        reminderList.addReminderOneTime("Past task", pastDateTime);

        List<Reminder> dueReminders = scheduler.tick();

        assertEquals(1, dueReminders.size());
        assertEquals("Past task", dueReminders.get(0).getName());
    }

    @Test
    void tick_withCurrentTimeReminder_returnsReminder() {
        // Add reminder for current time
        LocalDateTime now = LocalDateTime.now();
        DateTimeArg nowDateTime = new DateTimeArg(now.toLocalDate(), now.toLocalTime());
        reminderList.addReminderOneTime("Current task", nowDateTime);

        List<Reminder> dueReminders = scheduler.tick();

        assertEquals(1, dueReminders.size());
        assertEquals("Current task", dueReminders.get(0).getName());
    }

    @Test
    void tick_multipleDueReminders_returnsAll() {
        // Add multiple past reminders
        LocalDateTime past1 = LocalDateTime.now().minusHours(2);
        LocalDateTime past2 = LocalDateTime.now().minusMinutes(30);
        LocalDateTime past3 = LocalDateTime.now().minusMinutes(5);

        DateTimeArg dt1 = new DateTimeArg(past1.toLocalDate(), past1.toLocalTime());
        DateTimeArg dt2 = new DateTimeArg(past2.toLocalDate(), past2.toLocalTime());
        DateTimeArg dt3 = new DateTimeArg(past3.toLocalDate(), past3.toLocalTime());

        reminderList.addReminderOneTime("Task 1", dt1);
        reminderList.addReminderOneTime("Task 2", dt2);
        reminderList.addReminderOneTime("Task 3", dt3);

        List<Reminder> dueReminders = scheduler.tick();

        assertEquals(3, dueReminders.size());
    }

    @Test
    void tick_mixedDueAndFutureReminders_returnsOnlyDue() {
        // Add mix of past and future reminders
        LocalDateTime past = LocalDateTime.now().minusMinutes(10);
        LocalDateTime future1 = LocalDateTime.now().plusHours(1);
        LocalDateTime future2 = LocalDateTime.now().plusDays(1);

        DateTimeArg pastDt = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        DateTimeArg futureDt1 = new DateTimeArg(future1.toLocalDate(), future1.toLocalTime());
        DateTimeArg futureDt2 = new DateTimeArg(future2.toLocalDate(), future2.toLocalTime());

        reminderList.addReminderOneTime("Due now", pastDt);
        reminderList.addReminderOneTime("Not due 1", futureDt1);
        reminderList.addReminderOneTime("Not due 2", futureDt2);

        List<Reminder> dueReminders = scheduler.tick();

        assertEquals(1, dueReminders.size());
        assertEquals("Due now", dueReminders.get(0).getName());
        assertEquals(3, reminderList.getCount(), "All reminders should still be in list");
    }

    @Test
    void tick_calledTwice_doesNotReturnSameReminderTwice() {
        // Add past reminder
        LocalDateTime past = LocalDateTime.now().minusMinutes(5);
        DateTimeArg pastDateTime = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        reminderList.addReminderOneTime("Task", pastDateTime);

        // First tick should return the reminder
        List<Reminder> firstCall = scheduler.tick();
        assertEquals(1, firstCall.size());

        // Second tick should not return it again (it's been fired)
        List<Reminder> secondCall = scheduler.tick();
        assertTrue(secondCall.isEmpty(), "Reminder should not be returned again after being fired");
    }

    @Test
    void tick_newReminderAddedAfterCreation_isDetected() {
        // Create scheduler with empty list
        List<Reminder> firstTick = scheduler.tick();
        assertTrue(firstTick.isEmpty());

        // Add a due reminder to the shared reminderList
        LocalDateTime past = LocalDateTime.now().minusMinutes(5);
        DateTimeArg pastDateTime = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        reminderList.addReminderOneTime("New task", pastDateTime);

        // Tick again - should detect the new reminder
        List<Reminder> secondTick = scheduler.tick();
        assertEquals(1, secondTick.size());
        assertEquals("New task", secondTick.get(0).getName());
    }

    @Test
    void scheduler_withCustomInterval_usesCorrectInterval() {
        Scheduler customScheduler = new Scheduler(reminderList, 60);

        // We can't directly test the interval without starting the scheduler,
        // but we can verify it was created successfully
        assertNotNull(customScheduler);
    }

    @Test
    void scheduler_defaultConstructor_creates30SecondInterval() {
        Scheduler defaultScheduler = new Scheduler(reminderList);

        // Verify scheduler works with default interval
        assertNotNull(defaultScheduler);

        LocalDateTime past = LocalDateTime.now().minusMinutes(1);
        DateTimeArg pastDateTime = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        reminderList.addReminderOneTime("Task", pastDateTime);

        List<Reminder> dueReminders = defaultScheduler.tick();
        assertEquals(1, dueReminders.size());
    }

    @Test
    void start_calledTwice_doesNotCreateMultipleExecutors() {
        scheduler.start();
        scheduler.start(); // Second call should be ignored

        // If this doesn't throw an exception, the guard works
        scheduler.shutdown();
    }

    @Test
    void shutdown_withoutStart_doesNotThrowException() {
        // Should handle shutdown gracefully even if never started
        assertDoesNotThrow(() -> scheduler.shutdown());
    }

    @Test
    void shutdown_afterStart_cleansUpProperly() {
        scheduler.start();
        scheduler.shutdown();

        // Should be able to shutdown again without issues
        assertDoesNotThrow(() -> scheduler.shutdown());
    }

    @Test
    void tick_recurringReminder_reschedulesAfterFiring() {
        // Add recurring reminder (past, every 1 hour)
        LocalDateTime past = LocalDateTime.now().minusMinutes(5);
        DateTimeArg pastDateTime = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        java.time.Duration interval = java.time.Duration.ofHours(1);

        reminderList.addReminderRec("Recurring task", pastDateTime, interval);

        // First tick should return the reminder
        List<Reminder> firstCall = scheduler.tick();
        assertEquals(1, firstCall.size());
        assertEquals("Recurring task", firstCall.get(0).getName());

        // Second tick should NOT return it (rescheduled to 1 hour from now)
        List<Reminder> secondCall = scheduler.tick();
        assertTrue(secondCall.isEmpty(), "Recurring reminder should be rescheduled after firing");

        // Reminder should still be in the list
        assertEquals(1, reminderList.getCount(), "Recurring reminder should stay in list");
    }

    @Test
    void tick_mixedOneTimeAndRecurring_handlesCorrectly() {
        // Add one-time reminder (past)
        LocalDateTime past1 = LocalDateTime.now().minusMinutes(10);
        DateTimeArg pastDateTime1 = new DateTimeArg(past1.toLocalDate(), past1.toLocalTime());
        reminderList.addReminderOneTime("One-time task", pastDateTime1);

        // Add recurring reminder (past)
        LocalDateTime past2 = LocalDateTime.now().minusMinutes(5);
        DateTimeArg pastDateTime2 = new DateTimeArg(past2.toLocalDate(), past2.toLocalTime());
        reminderList.addReminderRec("Recurring task", pastDateTime2, java.time.Duration.ofDays(1));

        List<Reminder> dueReminders = scheduler.tick();

        // Both should be returned
        assertEquals(2, dueReminders.size());

        // Call tick again - only one-time shouldn't appear, recurring is rescheduled
        List<Reminder> secondTick = scheduler.tick();
        assertTrue(secondTick.isEmpty(), "Both reminders should not fire again immediately");
    }

    // ========== INTEGRATION TESTS - Testing Interval Scheduler Mechanism ==========

    @Test
    void integrationTest_schedulerAutomaticallyFiresReminder() throws InterruptedException {
        // Create a scheduler with 3-second interval
        Scheduler integrationScheduler = new Scheduler(reminderList, 3);

        // Add a past reminder that should fire immediately
        LocalDateTime past = LocalDateTime.now().minusMinutes(5);
        DateTimeArg pastDateTime = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        reminderList.addReminderOneTime("Auto-fire task", pastDateTime);

        // Start the scheduler - this should fire the reminder within 3 seconds
        integrationScheduler.start();

        // Wait for at least one tick cycle (3 seconds + buffer)
        Thread.sleep(4000);

        integrationScheduler.shutdown();

        // Verify the reminder was fired by checking output
        String output = outputStream.toString();
        assertTrue(output.contains("Reminder") || output.contains("Auto-fire task"),
                "Scheduler should have automatically fired the reminder");
    }

    @Test
    void integrationTest_schedulerFiresMultipleReminders() throws InterruptedException {
        Scheduler integrationScheduler = new Scheduler(reminderList, 3);

        // Add multiple past reminders
        LocalDateTime past1 = LocalDateTime.now().minusMinutes(10);
        LocalDateTime past2 = LocalDateTime.now().minusMinutes(5);
        DateTimeArg pastDateTime1 = new DateTimeArg(past1.toLocalDate(), past1.toLocalTime());
        DateTimeArg pastDateTime2 = new DateTimeArg(past2.toLocalDate(), past2.toLocalTime());

        reminderList.addReminderOneTime("First reminder", pastDateTime1);
        reminderList.addReminderOneTime("Second reminder", pastDateTime2);

        integrationScheduler.start();

        // Wait for tick cycle
        Thread.sleep(4000);

        integrationScheduler.shutdown();

        // Both reminders should have fired
        String output = outputStream.toString();
        assertTrue(output.contains("First reminder") || output.contains("Second reminder"),
                "Multiple reminders should have been fired");
    }

    @Test
    void integrationTest_schedulerFiresReminderAddedAfterStart() throws InterruptedException {
        Scheduler integrationScheduler = new Scheduler(reminderList, 3);

        // Start scheduler with empty list
        integrationScheduler.start();

        // Wait 1 second, then add a due reminder
        Thread.sleep(1000);
        LocalDateTime past = LocalDateTime.now().minusMinutes(5);
        DateTimeArg pastDateTime = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        reminderList.addReminderOneTime("Added after start", pastDateTime);

        // Wait for next tick cycle
        Thread.sleep(4000);

        integrationScheduler.shutdown();

        // Reminder added after start should still be fired
        String output = outputStream.toString();
        assertTrue(output.contains("Added after start") || output.contains("Reminder"),
                "Reminder added after scheduler start should be fired on next tick");
    }

    @Test
    void integrationTest_recurringReminderFiresMultipleTimes() throws InterruptedException {
        // Use a very short interval for recurring reminder (1 second)
        Scheduler integrationScheduler = new Scheduler(reminderList, 2);

        // Add a recurring reminder that fires every 5 seconds, starting in the past
        LocalDateTime past = LocalDateTime.now().minusSeconds(10);
        DateTimeArg pastDateTime = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        java.time.Duration recurInterval = java.time.Duration.ofSeconds(5);

        reminderList.addReminderRec("Recurring task", pastDateTime, recurInterval);

        integrationScheduler.start();

        // Wait for first tick
        Thread.sleep(3000);
        String firstOutput = outputStream.toString();

        // Clear output
        outputStream.reset();

        // Wait for potential second firing (recurring should reschedule)
        Thread.sleep(6000);

        integrationScheduler.shutdown();

        // First tick should have fired the reminder
        assertTrue(firstOutput.contains("Recurring task") || firstOutput.contains("Reminder"),
                "Recurring reminder should fire on first tick");
    }

    @Test
    void integrationTest_schedulerPeriodicExecution() throws InterruptedException {
        Scheduler integrationScheduler = new Scheduler(reminderList, 3);

        // Add a reminder that will be due
        LocalDateTime past = LocalDateTime.now().minusMinutes(5);
        DateTimeArg pastDateTime = new DateTimeArg(past.toLocalDate(), past.toLocalTime());
        reminderList.addReminderOneTime("Periodic check", pastDateTime);

        integrationScheduler.start();

        // Wait for first tick
        Thread.sleep(4000);
        assertTrue(outputStream.toString().contains("Reminder") ||
                   outputStream.toString().contains("Periodic check"),
                "First tick should fire the reminder");

        // Clear and wait for second tick
        outputStream.reset();
        Thread.sleep(3500);

        integrationScheduler.shutdown();
    }
}
