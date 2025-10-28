package seedu.studymate.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.studymate.exceptions.StudyMateException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {
    private Parser parser;

    @BeforeEach
    void setup() {
        parser = new Parser();
    }

    @Test
    void testListCommand() throws StudyMateException {
        Command cmd = parser.parse("list");
        assertEquals(CommandType.LIST, cmd.type);
    }

    @Test
    void testToDoCommand() throws StudyMateException {
        Command cmd = parser.parse("todo read book");
        assertEquals(CommandType.TODO, cmd.type);
        assertEquals("read book", cmd.desc);
    }

    @Test
    void testDeadlineCommand() throws StudyMateException {
        Command cmd = parser.parse("deadline submit assignment /by 2024-12-15 23:59");
        assertEquals(CommandType.DEADLINE, cmd.type);
        assertEquals("submit assignment", cmd.desc);
    }

    @Test
    void testMarkCommand() throws StudyMateException {
        Command cmd = parser.parse("mark 1");
        assertEquals(CommandType.MARK, cmd.type);
    }

    @Test
    void testMarkMultipleCommand() throws StudyMateException {
        Command cmd = parser.parse("mark 1,2,3");
        assertEquals(CommandType.MARK, cmd.type);
    }

    @Test
    void testMarkRangeCommand() throws StudyMateException {
        Command cmd = parser.parse("mark 1...5");
        assertEquals(CommandType.MARK, cmd.type);
    }

    @Test
    void testUnmarkCommand() throws StudyMateException {
        Command cmd = parser.parse("unmark 1");
        assertEquals(CommandType.UNMARK, cmd.type);
    }

    @Test
    void testUnmarkMultipleCommand() throws StudyMateException {
        Command cmd = parser.parse("unmark 1,2,3");
        assertEquals(CommandType.UNMARK, cmd.type);
    }

    @Test
    void testUnmarkRangeCommand() throws StudyMateException {
        Command cmd = parser.parse("unmark 2...4");
        assertEquals(CommandType.UNMARK, cmd.type);
    }

    @Test
    void testDeleteCommand() throws StudyMateException {
        Command cmd = parser.parse("delete 1");
        assertEquals(CommandType.DELETE, cmd.type);
    }

    @Test
    void testDeleteMultipleCommand() throws StudyMateException {
        Command cmd = parser.parse("delete 1,3,5");
        assertEquals(CommandType.DELETE, cmd.type);
    }

    @Test
    void testDeleteRangeCommand() throws StudyMateException {
        Command cmd = parser.parse("delete 1...3");
        assertEquals(CommandType.DELETE, cmd.type);
    }

    @Test
    void testByeCommand() throws StudyMateException {
        Command cmd = parser.parse("bye");
        assertEquals(CommandType.BYE, cmd.type);
    }

    @Test
    void testComplexRangeCommands() throws StudyMateException {
        Command cmd = parser.parse("mark 1...3,5");
        // note: parser automatically handles one to zero indexing
        LinkedHashSet<Integer> result = new LinkedHashSet<>(List.of(0, 1, 2, 4));
        assertEquals(result, cmd.indexes);
    }

    @Test
    void testCaseInsensitiveCommands() throws StudyMateException {
        Command listCmd = parser.parse("LIST");
        assertEquals(CommandType.LIST, listCmd.type);

        Command todoCmd = parser.parse("TODO read book");
        assertEquals(CommandType.TODO, todoCmd.type);

        Command byeCmd = parser.parse("BYE");
        assertEquals(CommandType.BYE, byeCmd.type);
    }

    @Test
    void testEmptyLineThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse(""));
    }

    @Test
    void testUnknownCommandThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("unknown"));
    }

    @Test
    void testEmptyToDoDescriptionThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("todo "));
    }

    @Test
    void testEmptyDeadlineDescriptionThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("deadline "));
    }

    @Test
    void testDeadlineWithoutByDelimiterThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("deadline submit assignment"));
    }

    @Test
    void testDeadlineWithInvalidDateThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("deadline submit assignment /by invalid-date"));
    }

    @Test
    void testDeadlineWithoutTimeThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("deadline submit assignment /by 2024-12-15"));
    }

    @Test
    void testDeadlineWithInvalidTimeFormatThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("deadline submit assignment /by 2024-12-15 25:00"));
    }

    // Event command tests
    @Test
    void testEventCommand() throws StudyMateException {
        Command cmd = parser.parse("event Team meeting /from 2025-10-20 09:00 /to 2025-10-22 17:00");
        assertEquals(CommandType.EVENT, cmd.type);
        assertEquals("Team meeting", cmd.desc);
    }

    @Test
    void testEventWithLongDescription() throws StudyMateException {
        Command cmd = parser.parse("event Project presentation and demo /from 2025-11-01 14:00 /to 2025-11-05 16:00");
        assertEquals(CommandType.EVENT, cmd.type);
        assertEquals("Project presentation and demo", cmd.desc);
    }

    @Test
    void testEmptyEventDescriptionThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("event "));
    }

    @Test
    void testEventWithoutFromDelimiterThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("event Team meeting /to 2025-10-22 17:00"));
    }

    @Test
    void testEventWithoutToDelimiterThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("event Team meeting /from 2025-10-20 09:00"));
    }

    @Test
    void testEventWithoutBothDelimitersThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("event Team meeting"));
    }

    @Test
    void testEventWithReversedDelimitersThrowsException() {
        assertThrows(StudyMateException.class, () ->
                parser.parse("event Team meeting /to 2025-10-22 17:00 /from 2025-10-20 09:00"));
    }

    @Test
    void testEventWithInvalidFromDateThrowsException() {
        assertThrows(StudyMateException.class, () ->
                parser.parse("event Team meeting /from invalid-date /to 2025-10-22 17:00"));
    }

    @Test
    void testEventWithInvalidToDateThrowsException() {
        assertThrows(StudyMateException.class, () ->
                parser.parse("event Team meeting /from 2025-10-20 09:00 /to invalid-date"));
    }

    @Test
    void testEventWithEmptyFromDateThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("event Team meeting /from /to 2025-10-22 17:00"));
    }

    @Test
    void testEventWithEmptyToDateThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("event Team meeting /from 2025-10-20 09:00 /to"));
    }

    @Test
    void testEventWithEmptyDescriptionThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("event /from 2025-10-20 09:00 /to 2025-10-22 17:00"));
    }

    @Test
    void testEventWithoutTimeInFromThrowsException() {
        assertThrows(StudyMateException.class,
                () -> parser.parse("event Team meeting /from 2025-10-20 /to 2025-10-22 17:00"));
    }

    @Test
    void testEventWithoutTimeInToThrowsException() {
        assertThrows(StudyMateException.class,
                () -> parser.parse("event Team meeting /from 2025-10-20 09:00 /to 2025-10-22"));
    }

    @Test
    void testEventWithInvalidTimeFormatThrowsException() {
        assertThrows(StudyMateException.class,
                () -> parser.parse("event Team meeting /from 2025-10-20 25:00 /to 2025-10-22 17:00"));
    }

    @Test
    void testMarkWithoutIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("mark "));
    }

    @Test
    void testDeleteWithoutIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("delete "));
    }

    @Test
    void testUnmarkWithoutIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("unmark "));
    }

    // Reminder command tests
    @Test
    void testRemAddCommand() throws StudyMateException {
        Command cmd = parser.parse("rem meeting @ 2024-12-15 18:00");
        assertEquals(CommandType.REM_ADD_ONETIME, cmd.type);
        assertEquals("meeting", cmd.desc);
    }

    @Test
    void testRemLsCommand() throws StudyMateException {
        Command cmd = parser.parse("rem ls");
        assertEquals(CommandType.REM_LS, cmd.type);
    }

    @Test
    void testRemRmCommand() throws StudyMateException {
        Command cmd = parser.parse("rem rm 1");
        assertEquals(CommandType.REM_RM, cmd.type);
    }

    @Test
    void testRemRmMultipleCommand() throws StudyMateException {
        Command cmd = parser.parse("rem rm 1,2,3");
        assertEquals(CommandType.REM_RM, cmd.type);
    }

    @Test
    void testRemRmRangeCommand() throws StudyMateException {
        Command cmd = parser.parse("rem rm 1...3");
        assertEquals(CommandType.REM_RM, cmd.type);
    }

    @Test
    void testRemWithoutSubcommandThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("rem"));
    }

    @Test
    void testRemAddWithoutAtDelimiterThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("rem meeting 2024-12-15"));
    }

    @Test
    void testRemAddWithInvalidDateThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("rem meeting @ invalid-date"));
    }

    // Find command tests
    @Test
    void testFindCommand() throws StudyMateException {
        Command cmd = parser.parse("find book");
        assertEquals(CommandType.FIND, cmd.type);
        assertEquals("book", cmd.substring);
    }

    @Test
    void testFindWithMultipleWords() throws StudyMateException {
        Command cmd = parser.parse("find submit assignment");
        assertEquals(CommandType.FIND, cmd.type);
        assertEquals("submit assignment", cmd.substring);
    }

    @Test
    void testFindWithSpecialCharacters() throws StudyMateException {
        Command cmd = parser.parse("find CS2103T");
        assertEquals(CommandType.FIND, cmd.type);
        assertEquals("CS2103T", cmd.substring);
    }

    @Test
    void testFindCaseInsensitive() throws StudyMateException {
        Command cmd = parser.parse("FIND test");
        assertEquals(CommandType.FIND, cmd.type);
        assertEquals("test", cmd.substring);
    }

    @Test
    void testFindWithExtraSpaces() throws StudyMateException {
        Command cmd = parser.parse("find   read   book");
        assertEquals(CommandType.FIND, cmd.type);
        assertEquals("read book", cmd.substring);
    }

    @Test
    void testFindWithoutKeywordThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("find "));
    }

    @Test
    void testListSortedCommand() throws StudyMateException {
        Command cmd = parser.parse("list -s");
        assertEquals(CommandType.LIST, cmd.type);
        assertEquals(true, cmd.isSorted);
    }

    @Test
    void testListWithInvalidFlagThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("list -x"));
    }

    @Test
    void testListWithExtraArgumentsThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("list extra arguments"));
    }

    // Timer command tests
    @Test
    void testTimerStartDefaultCommand() throws StudyMateException {
        Command cmd = parser.parse("start");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Focus session", cmd.desc);
        assertEquals(Long.valueOf(25), cmd.duration);
    }

    @Test
    void testTimerStartWithDurationCommand() throws StudyMateException {
        Command cmd = parser.parse("start @45");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Focus session", cmd.desc);
        assertEquals(Long.valueOf(45), cmd.duration);
    }

    @Test
    void testTimerStartWithLabelCommand() throws StudyMateException {
        Command cmd = parser.parse("start Study Math");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Study Math", cmd.desc);
        assertEquals(Long.valueOf(25), cmd.duration);
    }

    @Test
    void testTimerStartWithLabelAndDurationCommand() throws StudyMateException {
        Command cmd = parser.parse("start Study Physics @30");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Study Physics", cmd.desc);
        assertEquals(Long.valueOf(30), cmd.duration);
    }

    @Test
    void testTimerStartWithIndexCommand() throws StudyMateException {
        Command cmd = parser.parse("start 1");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Integer.valueOf(0), cmd.indexes.iterator().next()); // 1-based to 0-based conversion
        assertEquals(Long.valueOf(25), cmd.duration);
    }

    @Test
    void testTimerStartWithIndexAndDurationCommand() throws StudyMateException {
        Command cmd = parser.parse("start 2 @60");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Integer.valueOf(1), cmd.indexes.iterator().next()); // 1-based to 0-based conversion
        assertEquals(Long.valueOf(60), cmd.duration);
    }

    @Test
    void testTimerPauseCommand() throws StudyMateException {
        Command cmd = parser.parse("pause");
        assertEquals(CommandType.PAUSE, cmd.type);
    }

    @Test
    void testTimerResumeCommand() throws StudyMateException {
        Command cmd = parser.parse("resume");
        assertEquals(CommandType.RESUME, cmd.type);
    }

    @Test
    void testTimerResetCommand() throws StudyMateException {
        Command cmd = parser.parse("reset");
        assertEquals(CommandType.RESET, cmd.type);
    }

    @Test
    void testTimerStatCommand() throws StudyMateException {
        Command cmd = parser.parse("stat");
        assertEquals(CommandType.STAT, cmd.type);
    }

    @Test
    void testTimerStartWithZeroMinutesThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("start @0"));
    }

    @Test
    void testTimerStartWithNegativeMinutesThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("start @-5"));
    }

    @Test
    void testTimerStartWithInvalidMinutesThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("start @abc"));
    }

    @Test
    void testTimerStartWithZeroIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("start 0"));
    }

    // Edit command tests
    @Test
    void testEditDescriptionCommand() throws StudyMateException {
        Command cmd = parser.parse("edit 1 -n new description");
        assertEquals(CommandType.EDIT_DESC, cmd.type);
        assertEquals(0, cmd.index); // 1-based to 0-based conversion
        assertEquals("new description", cmd.desc);
    }

    @Test
    void testEditDeadlineCommand() throws StudyMateException {
        Command cmd = parser.parse("edit 2 -d 2025-12-31 23:59");
        assertEquals(CommandType.EDIT_DEADLINE, cmd.type);
        assertEquals(1, cmd.index); // 1-based to 0-based conversion
        assertEquals(LocalDate.of(2025, 12, 31), cmd.datetime0.getDate());
        assertEquals(LocalTime.of(23, 59), cmd.datetime0.getTime());
    }

    @Test
    void testEditFromCommand() throws StudyMateException {
        Command cmd = parser.parse("edit 3 -f 2025-11-15 10:00");
        assertEquals(CommandType.EDIT_FROM, cmd.type);
        assertEquals(2, cmd.index); // 1-based to 0-based conversion
        assertEquals(LocalDate.of(2025, 11, 15), cmd.datetime0.getDate());
        assertEquals(LocalTime.of(10, 0), cmd.datetime0.getTime());
    }

    @Test
    void testEditToCommand() throws StudyMateException {
        Command cmd = parser.parse("edit 4 -t 2025-11-20 17:30");
        assertEquals(CommandType.EDIT_TO, cmd.type);
        assertEquals(3, cmd.index); // 1-based to 0-based conversion
        assertEquals(LocalDate.of(2025, 11, 20), cmd.datetime0.getDate());
        assertEquals(LocalTime.of(17, 30), cmd.datetime0.getTime());
    }

    @Test
    void testEditWithLongDescription() throws StudyMateException {
        Command cmd = parser.parse("edit 1 -n This is a very long task description with multiple words");
        assertEquals(CommandType.EDIT_DESC, cmd.type);
        assertEquals("This is a very long task description with multiple words", cmd.desc);
    }

    @Test
    void testEditWithoutIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit -n new description"));
    }

    @Test
    void testEditWithoutFlagThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 new description"));
    }

    @Test
    void testEditWithoutValueThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 -n"));
    }

    @Test
    void testEditWithInvalidFlagThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 -x value"));
    }

    @Test
    void testEditWithInvalidIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit abc -n description"));
    }

    @Test
    void testEditWithInvalidDateThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 -d invalid-date"));
    }

    @Test
    void testEditDeadlineWithInvalidDateFormatThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 -d 2025/12/31"));
    }

    @Test
    void testEditDeadlineWithoutTimeThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 -d 2025-12-31"));
    }

    @Test
    void testEditFromWithInvalidDateThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 -f not-a-date"));
    }

    @Test
    void testEditFromWithoutTimeThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 -f 2025-11-15"));
    }

    @Test
    void testEditToWithInvalidDateThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 -t 31-12-2025"));
    }

    @Test
    void testEditToWithoutTimeThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit 1 -t 2025-11-20"));
    }

    @Test
    void testEditEmptyCommandThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("edit"));
    }

    // Habit command tests
    @Test
    void testHabitAddCommand() throws StudyMateException {
        Command cmd = parser.parse("habit Exercise -t 1d");
        assertEquals(CommandType.HABIT_ADD, cmd.type);
        assertEquals("Exercise", cmd.desc);
        assertEquals("PT24H", cmd.interval.toString());
    }

    @Test
    void testHabitAddWithMultipleWordsCommand() throws StudyMateException {
        Command cmd = parser.parse("habit Morning meditation practice -t 12h");
        assertEquals(CommandType.HABIT_ADD, cmd.type);
        assertEquals("Morning meditation practice", cmd.desc);
        assertEquals("PT12H", cmd.interval.toString());
    }

    @Test
    void testHabitAddWithWeekIntervalCommand() throws StudyMateException {
        Command cmd = parser.parse("habit Weekly review -t 1w");
        assertEquals(CommandType.HABIT_ADD, cmd.type);
        assertEquals("Weekly review", cmd.desc);
        assertEquals("PT168H", cmd.interval.toString()); // 1 week = 168 hours
    }

    @Test
    void testHabitAddWithMinuteIntervalCommand() throws StudyMateException {
        Command cmd = parser.parse("habit Quick stretch -t 30m");
        assertEquals(CommandType.HABIT_ADD, cmd.type);
        assertEquals("Quick stretch", cmd.desc);
        assertEquals("PT30M", cmd.interval.toString());
    }

    @Test
    void testHabitListCommand() throws StudyMateException {
        Command cmd = parser.parse("habit ls");
        assertEquals(CommandType.HABIT_LIST, cmd.type);
    }

    @Test
    void testHabitStreakCommand() throws StudyMateException {
        Command cmd = parser.parse("habit streak 1");
        assertEquals(CommandType.HABIT_STREAK, cmd.type);
        assertEquals(0, cmd.index); // 1-based to 0-based conversion
    }

    @Test
    void testHabitRmCommand() throws StudyMateException {
        Command cmd = parser.parse("habit rm 2");
        assertEquals(CommandType.HABIT_DELETE, cmd.type);
        assertEquals(1, cmd.index); // 1-based to 0-based conversion
    }

    @Test
    void testHabitWithoutSubcommandThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit"));
    }

    @Test
    void testHabitAddWithoutIntervalFlagThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit Exercise"));
    }

    @Test
    void testHabitAddWithoutIntervalValueThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit Exercise -t"));
    }

    @Test
    void testHabitAddWithBlankNameThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit    -t 1d"));
    }

    @Test
    void testHabitAddWithInvalidIntervalFormatThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit Exercise -t invalid"));
    }

    @Test
    void testHabitAddWithInvalidIntervalUnitThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit Exercise -t 1x"));
    }

    @Test
    void testHabitStreakWithoutIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit streak"));
    }

    @Test
    void testHabitStreakWithBlankIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit streak "));
    }

    @Test
    void testHabitStreakWithInvalidIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit streak abc"));
    }

    @Test
    void testHabitRmWithoutIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit rm"));
    }

    @Test
    void testHabitRmWithBlankIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit rm "));
    }

    @Test
    void testHabitRmWithInvalidIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit rm xyz"));
    }

    @Test
    void testHabitWithInvalidSubcommandThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit invalid"));
    }

    @Test
    void testHabitAddWithExtraSpacesCommand() throws StudyMateException {
        Command cmd = parser.parse("habit   Exercise   -t   1d");
        assertEquals(CommandType.HABIT_ADD, cmd.type);
        assertEquals("Exercise", cmd.desc);
    }

    @Test
    void testHabitStreakWithExtraSpacesCommand() throws StudyMateException {
        Command cmd = parser.parse("habit   streak   5");
        assertEquals(CommandType.HABIT_STREAK, cmd.type);
        assertEquals(4, cmd.index);
    }

    @Test
    void testHabitRmWithExtraSpacesCommand() throws StudyMateException {
        Command cmd = parser.parse("habit   rm   3");
        assertEquals(CommandType.HABIT_DELETE, cmd.type);
        assertEquals(2, cmd.index);
    }

    // Tests for exceeding maximum value cap (10000)
    @Test
    void testMarkWithExcessiveIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("mark 10001"));
    }

    @Test
    void testMarkWithRangeExceedingCapThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("mark 1...10001"));
    }

    @Test
    void testTimerStartWithExcessiveMinutesThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("start @10001"));
    }

    @Test
    void testTimerStartWithLabelAndExcessiveMinutesThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("start Study Session @10001"));
    }

    @Test
    void testHabitAddWithExcessiveIntervalThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("habit Exercise -t 10001d"));
    }

    @Test
    void testRemAddWithExcessiveIntervalThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("rem Study @ 2025-12-31 12:00 -r 10001h"));
    }

    // Tests for boundary values at exactly 10000 (should succeed)
    @Test
    void testMarkWithMaximumAllowedIndex() throws StudyMateException {
        Command cmd = parser.parse("mark 10000");
        assertEquals(CommandType.MARK, cmd.type);
        assertEquals(Integer.valueOf(9999), cmd.indexes.iterator().next()); // 1-based to 0-based
    }

    @Test
    void testTimerStartWithMaximumAllowedMinutes() throws StudyMateException {
        Command cmd = parser.parse("start @10000");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Long.valueOf(10000), cmd.duration);
    }

    @Test
    void testHabitAddWithMaximumAllowedInterval() throws StudyMateException {
        Command cmd = parser.parse("habit Exercise -t 10000d");
        assertEquals(CommandType.HABIT_ADD, cmd.type);
        assertEquals("Exercise", cmd.desc);
    }
}
