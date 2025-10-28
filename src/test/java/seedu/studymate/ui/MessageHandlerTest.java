package seedu.studymate.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.habits.Habit;
import seedu.studymate.habits.HabitList;
import seedu.studymate.habits.StreakResult;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.reminders.IndexedReminder;
import seedu.studymate.reminders.Reminder;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.tasks.Task;
import seedu.studymate.tasks.TaskList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MessageHandlerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final String LINE = "____________________________________________________________\n";

    // --- Mocks ---
    static class MockDateTimeArg extends DateTimeArg {
        private final String str;

        public MockDateTimeArg(String s) {
            super(java.time.LocalDate.now(), java.time.LocalTime.now());
            this.str = s;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    static class MockTask extends Task {
        private final String str;

        public MockTask(String name) {
            super(name);
            this.str = "[T][ ] " + name;
        }

        @Override
        public String toString() {
            return str;
        }

        @Override
        public String toSaveString() {
            return "MockTask|SaveString";
        }
    }

    static class MockTaskList extends TaskList {
        private final List<Task> internalList;

        public MockTaskList(List<Task> tasks) {
            internalList = tasks;
        }

        @Override
        public int getCount() {
            return internalList.size();
        }

        @Override
        public Task getTask(int index) {
            return internalList.get(index);
        }
    }

    static class MockReminder extends Reminder {
        private final String str;

        public MockReminder(String name) {
            super(name, new MockDateTimeArg(""), Clock.systemDefaultZone());
            this.str = name + " (Reminder)";
        }

        @Override
        public String toString() {
            return str;
        }

        @Override
        public String toSaveString() {
            return "MockReminder|SaveString";
        }
    }

    static class MockHabit extends Habit {
        private final String str;

        public MockHabit(String name) {
            super(name, Duration.ofDays(1), Clock.systemDefaultZone());
            this.str = "[H] " + name;
        }

        @Override
        public String toString() {
            return str;
        }

        @Override
        public String toSaveString() {
            return "MockHabit|SaveString";
        }
    }

    static class MockHabitList extends HabitList {
        private final List<Habit> internalList;

        public MockHabitList(List<Habit> habits) {
            super();
            internalList = habits;
        }

        @Override
        public int getCount() {
            return internalList.size();
        }

        @Override
        public ArrayList<Habit> getAllHabits() {
            return new ArrayList<>(internalList);
        }
    }

    static class MockIndexedReminder extends IndexedReminder {
        public MockIndexedReminder(int index, Reminder reminder) {
            super(index, reminder);
        }
    }

    static class MockReminderList extends ReminderList {
        private final List<Reminder> reminders;

        public MockReminderList(List<Reminder> reminders) {
            this.reminders = reminders;
        }

        @Override
        public int getCount() {
            return reminders.size();
        }

        @Override
        public Reminder getReminder(int index) {
            return reminders.get(index);
        }
    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private String normaliseOutput(String output) {
        return output.replaceAll("\\r\\n|\\r", "\n");
    }

    // --- sendMessage (varargs logic) ---
    @Test
    void sendMessage_multipleMessages_success() {
        MessageHandler.sendMessage("Line 1", "Line 2");
        String expected = LINE + "Line 1\nLine 2\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Task List: empty/not empty branches ---
    @Test
    void sendTaskList_emptyList_message() {
        MessageHandler.sendTaskList(new MockTaskList(Collections.emptyList()));
        String expected = LINE + "Task list is empty!\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendTaskList_populated_success() {
        MessageHandler.sendTaskList(new MockTaskList(List.of(new MockTask("A"))));
        String expected = LINE + "Here are the tasks in your task list:\n1. [T][ ] A\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Sorted Task List ---
    @Test
    void sendSortedTaskList_emptyList_message() {
        MessageHandler.sendSortedTaskList(new ArrayList<>());
        String expected = LINE + "Task list has no deadlines or events!\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendSortedTaskList_nonEmpty_success() {
        ArrayList<Task> tasks = new ArrayList<>(List.of(new MockTask("Deadline")));
        MessageHandler.sendSortedTaskList(tasks);
        String expected = LINE + "Here are the deadlines and events in your task list, sorted by their deadlines and/or start times:\n1. [T][ ] Deadline\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Find Results ---
    @Test
    void sendFindResults_empty_noResults() {
        MessageHandler.sendFindResults(new ArrayList<>());
        String expected = LINE + "No results found!\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendFindResults_nonEmpty_found() {
        ArrayList<Task> tasks = new ArrayList<>(List.of(new MockTask("Found")));
        MessageHandler.sendFindResults(tasks);
        String expected = LINE + "Here are the tasks with the matching substring found!:\n1. [T][ ] Found\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- ReminderList ---
    @Test
    void sendReminderList_emptyList_message() {
        MessageHandler.sendReminderList(new MockReminderList(Collections.emptyList()));
        String expected = LINE + "Reminders list is empty!\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendReminderList_nonEmpty_success() {
        MessageHandler.sendReminderList(new MockReminderList(List.of(new MockReminder("X"))));
        String expected = LINE + "Here are your Reminders:\n1. X (Reminder)\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Add/Delete Count (pluralisation, branches) ---
    @Test
    void sendAddTaskMessage_countOne_singular() {
        MessageHandler.sendAddTaskMessage(new MockTask("Single Task"), 1);
        String expected = LINE + "Got it. I've added this task:\n[T][ ] Single Task\nNow you have 1 task in the task list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendAddTaskMessage_countTwo_plural() {
        MessageHandler.sendAddTaskMessage(new MockTask("Task"), 2);
        String expected = LINE + "Got it. I've added this task:\n[T][ ] Task\nNow you have 2 tasks in the task list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendDeleteTaskMessage_countMultiple_plural() {
        MessageHandler.sendDeleteTaskMessage(List.of(new MockTask("Task A")), 3);
        String expected = LINE + "Got it. I've deleted these tasks:\n[T][ ] Task A\nNow you have 3 tasks in the task list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendDeleteTaskMessage_countOne_singular() {
        MessageHandler.sendDeleteTaskMessage(List.of(new MockTask("Task")), 1);
        String expected = LINE + "Got it. I've deleted these tasks:\n[T][ ] Task\nNow you have 1 task in the task list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Reminder delete ---
    @Test
    void sendDeleteReminderMessage_countMultiple_plural() {
        MessageHandler.sendDeleteReminderMessage(List.of(new MockReminder("Rem A")), 3);
        String expected = LINE + "Got it. I've deleted these reminders:\nRem A (Reminder)\nNow you have 3 reminders in the Reminders list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendDeleteReminderMessage_countOne_singular() {
        MessageHandler.sendDeleteReminderMessage(List.of(new MockReminder("Rem A")), 1);
        String expected = LINE + "Got it. I've deleted these reminders:\nRem A (Reminder)\nNow you have 1 reminder in the Reminders list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Mark/Unmark ---
    @Test
    void sendMarkMessage_success() {
        MessageHandler.sendMarkMessage(List.of(new MockTask("Task")));
        String expected = LINE + "Nice! I've marked these tasks as done:\n[T][ ] Task\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendUnmarkMessage_success() {
        MessageHandler.sendUnmarkMessage(List.of(new MockTask("Task")));
        String expected = LINE + "OK, I've marked these tasks as not done yet:\n[T][ ] Task\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Edit messages (desc, deadline, from, to) ---
    @Test
    void sendEditDescMessage_success() {
        MessageHandler.sendEditDescMessage(new MockTask("Edit"));
        String expected = LINE + "OK, I've edited the description of the task to: \n[T][ ] Edit\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendEditDeadlineMessage_success() {
        MessageHandler.sendEditDeadlineMessage(new MockTask("Deadline Task"), new MockDateTimeArg("NEW_DEADLINE"));
        String expected = LINE + "OK, I've edited the deadline of the deadline Deadline Task to:\nNEW_DEADLINE\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendEditFromMessage_success() {
        MessageHandler.sendEditFromMessage(new MockTask("Event Task"), new MockDateTimeArg("DATE_TIME"));
        String expected = LINE + "OK, I've edited the from date of the event Event Task to:\nDATE_TIME\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendEditToMessage_success() {
        MessageHandler.sendEditToMessage(new MockTask("Event Task"), new MockDateTimeArg("TO_TIME"));
        String expected = LINE + "OK, I've edited the to date of the event Event Task to:\nTO_TIME\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Reminder add one-time/recurring ---
    @Test
    void sendAddReminderRecMessage_countOne_singular() {
        MessageHandler.sendAddReminderRecMessage(new MockReminder("Rem"), 1);
        String expected = LINE + "Got it. I've added this recurring reminder:\nRem (Reminder)\nNow you have 1 reminder in the reminder list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendAddReminderRecMessage_countTwo_plural() {
        MessageHandler.sendAddReminderRecMessage(new MockReminder("Rem"), 2);
        String expected = LINE + "Got it. I've added this recurring reminder:\nRem (Reminder)\nNow you have 2 reminders in the reminder list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendAddReminderOneTimeMessage_countOne_singular() {
        MessageHandler.sendAddReminderOneTimeMessage(new MockReminder("OTRem"), 1);
        String expected = LINE + "Got it. I've added this One-Time reminder:\nOTRem (Reminder)\nNow you have 1 reminder in the reminder list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendAddReminderOneTimeMessage_countTwo_plural() {
        MessageHandler.sendAddReminderOneTimeMessage(new MockReminder("OTRem"), 2);
        String expected = LINE + "Got it. I've added this One-Time reminder:\nOTRem (Reminder)\nNow you have 2 reminders in the reminder list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Reminder notify API ---
    @Test
    void sendReminder_success() {
        List<IndexedReminder> reminders = List.of(new MockIndexedReminder(1, new MockReminder("Rem1")),
                new MockIndexedReminder(2, new MockReminder("Rem2")));
        MessageHandler.sendReminder(reminders);
        String expected = LINE + "IMPORTANT: StudyMate reminds you of the following!\n1. Rem1 (Reminder)\n2. Rem2 (Reminder)\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Reminder turn on/off/AlreadyOn/AlreadyOff branches ---
    @Test
    void sendIsTurnOnReminderMessage_success() {
        MessageHandler.sendIsTurnOnReminderMessage(List.of(new MockReminder("OnRem")));
        String expected = LINE + "The following reminders have been turned on:\nOnRem (Reminder)\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendAlreadyTurnOnReminderMessage_success() {
        MessageHandler.sendAlreadyTurnOnReminderMessage(List.of(new MockReminder("AlreadyOnRem")));
        String expected = LINE + "The following reminders have already been turned on:\nAlreadyOnRem (Reminder)\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendIsTurnOffReminderMessage_success() {
        MessageHandler.sendIsTurnOffReminderMessage(List.of(new MockReminder("OffRem")));
        String expected = LINE + "The following reminders have been turned off:\nOffRem (Reminder)\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendAlreadyTurnOffReminderMessage_success() {
        MessageHandler.sendAlreadyTurnOffReminderMessage(List.of(new MockReminder("AlreadyOffRem")));
        String expected = LINE + "The following reminders have already been turned off:\nAlreadyOffRem (Reminder)\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendRecUnableToSnoozeError_success() {
        MessageHandler.sendRecUnableToSnoozeError(new MockReminder("Rec Rem"));
        String expected = LINE + "The reminder is a recurring reminder, and cannot be snoozed: \nRec Rem (Reminder)\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendSnoozeMessage_success() {
        MessageHandler.sendSnoozeMessage(new MockReminder("Snooze Rem"));
        String expected = LINE + "The following reminder has successfully been snoozed: \nSnooze Rem (Reminder)\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Timer branches ---
    @Test
    void sendTimerStartMessage_success() {
        MessageHandler.sendTimerStartMessage(2, "Label");
        String expected = LINE + "# TIMER\n# RUNNING 2:00 left - Label\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendTimerPauseMessage_success() {
        MessageHandler.sendTimerPauseMessage(125, "Break");
        String expected = LINE + "# TIMER\n# PAUSED 2:05 left - Break\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendTimerResumeMessage_success() {
        MessageHandler.sendTimerResumeMessage(90, "Label");
        String expected = LINE + "# TIMER\n# RUNNING 1:30 left - Label\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendTimerResetMessage_success() {
        MessageHandler.sendTimerResetMessage();
        String expected = LINE + "# TIMER\n# RESET TIMER\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendTimerStatMessage_success() {
        MessageHandler.sendTimerStatMessage("Custom Stat Message");
        String expected = LINE + "Custom Stat Message\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendTimerEndedMessage_success() {
        MessageHandler.sendTimerEndedMessage();
        String expected = LINE + "# TIMER\n# TIMER HAS ENDED\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Habit ---
    @Test
    void sendHabitList_emptyList_message() {
        MessageHandler.sendHabitList(new MockHabitList(Collections.emptyList()));
        String expected = LINE + "Habit list is empty!\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendHabitList_populated_success() {
        MessageHandler.sendHabitList(new MockHabitList(List.of(new MockHabit("A"))));
        String expected = LINE + "Here are the habits in your habit list:\n1. [H] A\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- Add/Delete habit messages ---
    @Test
    void sendAddHabitMessage_countOne_singular() {
        MessageHandler.sendAddHabitMessage(new MockHabit("New Habit"), 1);
        String expected = LINE + "Got it. I've added this habit:\n[H] New Habit\nNow you have 1 habit in the habit list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendAddHabitMessage_countTwo_plural() {
        MessageHandler.sendAddHabitMessage(new MockHabit("Habit"), 2);
        String expected = LINE + "Got it. I've added this habit:\n[H] Habit\nNow you have 2 habits in the habit list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendDeleteHabitMessage_countOne_singular() {
        MessageHandler.sendDeleteHabitMessage(new MockHabit("DelHabit"), 1);
        String expected = LINE + "Got it. I've deleted this habit:\n[H] DelHabit\nNow you have 1 habit in the habit list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendDeleteHabitMessage_countTwo_plural() {
        MessageHandler.sendDeleteHabitMessage(new MockHabit("DelHabit"), 2);
        String expected = LINE + "Got it. I've deleted this habit:\n[H] DelHabit\nNow you have 2 habits in the habit list.\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    // --- sendIncStreakMessage (branches) ---
    @Test
    void sendIncStreakMessage_onTime_success() throws StudyMateException {
        MessageHandler.sendIncStreakMessage(new MockHabit("OnTime"), StreakResult.ON_TIME);
        String expected = LINE + "Great! You've incremented your streak for: [H] OnTime\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendIncStreakMessage_tooEarly_success() throws StudyMateException {
        MessageHandler.sendIncStreakMessage(new MockHabit("Early Habit"), StreakResult.TOO_EARLY);
        String expected = LINE + "Too early! You can only increment the streak after the deadline.\nHabit: [H] Early Habit\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }

    @Test
    void sendIncStreakMessage_tooLate_success() throws StudyMateException {
        MessageHandler.sendIncStreakMessage(new MockHabit("Late Habit"), StreakResult.TOO_LATE);
        String expected = LINE + "Missed the deadline! Your streak has been reset to 1.\nHabit: [H] Late Habit\n" + LINE;
        assertEquals(normaliseOutput(expected), normaliseOutput(outContent.toString()));
    }
}
