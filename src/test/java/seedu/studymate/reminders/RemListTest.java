package seedu.studymate.reminders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    // --- Test Cases for Adding Tasks ---

    @Test
    void testAdd() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);

        reminderList.addReminder("running", remDateTime);
        assertEquals(1, reminderList.getCount());
        Reminder rem = reminderList.getReminder(0);
        assertInstanceOf(Reminder.class, rem);
        assertEquals("running", rem.getName());
        assertFalse(rem.getOnReminder());
    }

    // --- Test Cases for Deletion ---

    // Helper function to populate a list to test for deletion
    private void populateListForDeletion() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 10, 18, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DateTimeArg remDateTime = new DateTimeArg(date, time);

        reminderList.addReminder("running", remDateTime);
        reminderList.addReminder("running 5km", remDateTime);
        reminderList.addReminder("running 10km", remDateTime);
        reminderList.addReminder("running 15km", remDateTime);
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
}
