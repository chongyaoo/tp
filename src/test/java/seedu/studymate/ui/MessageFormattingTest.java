package seedu.studymate.ui;

import org.junit.jupiter.api.Test;
import seedu.studymate.parser.DateTimeArg;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Mock implementation of DateTimeArg used for predictable string output
 * in MessageFormatting tests. It ensures the 'T' delimiter for time is present
 * for the .replace("T", " ") logic to be tested.
 */
class MockDateTimeArg extends DateTimeArg {
    private final String testString;

    public MockDateTimeArg(String testString) {
        // Super constructor requires date, but we only rely on toString() for formatting tests.
        // We use dummy values for LocalDate and LocalTime.
        super(java.time.LocalDate.now(), java.time.LocalTime.now());
        this.testString = testString;
    }

    @Override
    public String toString() {
        return testString;
    }
}

public class MessageFormattingTest {

    // --- 1. toDoString Tests (Covers both branches: isDone=true/false) ---
    @Test
    void toDoString_done_success() {
        String expected = "[T][X] Study Java";
        String actual = MessageFormatting.toDoString(true, "Study Java");
        assertEquals(expected, actual);
    }

    @Test
    void toDoString_notDone_success() {
        String expected = "[T][ ] Study Java";
        String actual = MessageFormatting.toDoString(false, "Study Java");
        assertEquals(expected, actual);
    }

    // --- 2. deadlineString Tests (Covers both branches: isDone=true/false) ---
    @Test
    void deadlineString_done_success() {
        DateTimeArg deadline = new MockDateTimeArg("2025-10-29T10:00");
        String expected = "[D][X] Submit Report (by: 2025-10-29 10:00)";
        String actual = MessageFormatting.deadlineString(true, "Submit Report", deadline);
        assertEquals(expected, actual);
    }

    @Test
    void deadlineString_notDone_success() {
        DateTimeArg deadline = new MockDateTimeArg("2025-10-30T23:59");
        String expected = "[D][ ] Submit Report (by: 2025-10-30 23:59)";
        String actual = MessageFormatting.deadlineString(false, "Submit Report", deadline);
        assertEquals(expected, actual);
    }

    // --- 3. eventString Tests (Covers both branches: isDone=true/false) ---
    @Test
    void eventString_done_success() {
        DateTimeArg from = new MockDateTimeArg("2025-11-01T09:00");
        DateTimeArg to = new MockDateTimeArg("2025-11-01T11:00");
        String expected = "[E][X] Meeting (from: 2025-11-01 09:00, to: 2025-11-01 11:00)";
        String actual = MessageFormatting.eventString(true, "Meeting", from, to);
        assertEquals(expected, actual);
    }

    @Test
    void eventString_notDone_success() {
        DateTimeArg from = new MockDateTimeArg("2025-12-25T12:00");
        DateTimeArg to = new MockDateTimeArg("2025-12-25T18:00");
        String expected = "[E][ ] Christmas Party (from: 2025-12-25 12:00, to: 2025-12-25 18:00)";
        String actual = MessageFormatting.eventString(false, "Christmas Party", from, to);
        assertEquals(expected, actual);
    }

    // --- 4. oneTimeReminderString Tests (Covers both branches: onReminder=true/false) ---
    @Test
    void oneTimeReminderString_onReminder_success() {
        DateTimeArg dateTime = new MockDateTimeArg("2025-11-05T14:30");
        String expected = "[RO][O] Pay Bills (2025-11-05 14:30)";
        String actual = MessageFormatting.oneTimeReminderString(true, "Pay Bills", dateTime);
        assertEquals(expected, actual);
    }

    @Test
    void oneTimeReminderString_offReminder_success() {
        DateTimeArg dateTime = new MockDateTimeArg("2025-11-06T10:00");
        String expected = "[RO][ ] Call Client (2025-11-06 10:00)";
        String actual = MessageFormatting.oneTimeReminderString(false, "Call Client", dateTime);
        assertEquals(expected, actual);
    }

    // --- 5. recReminderString Tests (Covers both branches: onReminder=true/false) ---
    @Test
    void recReminderString_onReminder_success() {
        DateTimeArg dateTime = new MockDateTimeArg("2025-11-10T08:00");
        // Duration.ofDays(7).toString() returns "PT168H" - testing the .replace("PT", "")
        Duration interval = Duration.ofDays(7);
        String expected = "[RR][O] Daily Standup (interval: 168H)\nNext reminder: 2025-11-10 08:00";
        String actual = MessageFormatting.recReminderString(true, "Daily Standup", dateTime, interval);
        assertEquals(expected, actual);
    }

    @Test
    void recReminderString_offReminder_success() {
        DateTimeArg dateTime = new MockDateTimeArg("2025-11-11T12:00");
        // Duration.ofMinutes(30).toString() returns "PT30M"
        Duration interval = Duration.ofMinutes(30);
        String expected = "[RR][ ] Check Email (interval: 30M)\nNext reminder: 2025-11-11 12:00";
        String actual = MessageFormatting.recReminderString(false, "Check Email", dateTime, interval);
        assertEquals(expected, actual);
    }

    // --- 6. habitString Tests (Covers single method execution) ---
    @Test
    void habitString_success() {
        DateTimeArg deadline = new MockDateTimeArg("2025-11-15T20:00");
        String expected = "[H] Exercise (deadline: 2025-11-15 20:00, streak: 5)";
        String actual = MessageFormatting.habitString("Exercise", deadline, 5);
        assertEquals(expected, actual);
    }
}