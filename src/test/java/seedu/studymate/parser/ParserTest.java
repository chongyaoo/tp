package seedu.studymate.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.studymate.exceptions.StudyMateException;

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
        Command cmd = parser.parse("deadline submit assignment /by 2024-12-15");
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
        Command cmd = parser.parse("rem meeting @ 2024-12-15");
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

    @Test
    void testRemAddWithEmptyEventThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("rem @ 2024-12-15"));
    }

    @Test
    void testRemAddWithEmptyDateThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("rem meeting @"));
    }

    @Test
    void testRemLsWithExtraArgumentsThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("rem ls extra"));
    }

    @Test
    void testRemRmWithoutIndexThrowsException() {
        assertThrows(StudyMateException.class, () -> parser.parse("rem rm"));
    }

    // Timer command tests
    @Test
    void testTimerStartDefaultCommand() throws StudyMateException {
        Command cmd = parser.parse("start");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Focus session", cmd.desc);
        assertEquals(Integer.valueOf(25), cmd.duration);
    }

    @Test
    void testTimerStartWithDurationCommand() throws StudyMateException {
        Command cmd = parser.parse("start @45");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Focus session", cmd.desc);
        assertEquals(Integer.valueOf(45), cmd.duration);
    }

    @Test
    void testTimerStartWithLabelCommand() throws StudyMateException {
        Command cmd = parser.parse("start Study Math");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Study Math", cmd.desc);
        assertEquals(Integer.valueOf(25), cmd.duration);
    }

    @Test
    void testTimerStartWithLabelAndDurationCommand() throws StudyMateException {
        Command cmd = parser.parse("start Study Physics @30");
        assertEquals(CommandType.START, cmd.type);
        assertEquals("Study Physics", cmd.desc);
        assertEquals(Integer.valueOf(30), cmd.duration);
    }

    @Test
    void testTimerStartWithIndexCommand() throws StudyMateException {
        Command cmd = parser.parse("start 1");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Integer.valueOf(0), cmd.indexes.iterator().next()); // 1-based to 0-based conversion
        assertEquals(Integer.valueOf(25), cmd.duration);
    }

    @Test
    void testTimerStartWithIndexAndDurationCommand() throws StudyMateException {
        Command cmd = parser.parse("start 2 @60");
        assertEquals(CommandType.START, cmd.type);
        assertEquals(Integer.valueOf(1), cmd.indexes.iterator().next()); // 1-based to 0-based conversion
        assertEquals(Integer.valueOf(60), cmd.duration);
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

    // Test index parsing with spaces (for the trimming fix)
    @Test
    void testMarkWithSpacesInIndexes() throws StudyMateException {
        Command cmd = parser.parse("mark 1, 2, 3");
        assertEquals(CommandType.MARK, cmd.type);
        LinkedHashSet<Integer> expected = new LinkedHashSet<>(List.of(0, 1, 2));
        assertEquals(expected, cmd.indexes);
    }

    @Test
    void testDeleteWithSpacesInRange() throws StudyMateException {
        Command cmd = parser.parse("delete 1 ... 3 , 5");
        assertEquals(CommandType.DELETE, cmd.type);
        LinkedHashSet<Integer> expected = new LinkedHashSet<>(List.of(0, 1, 2, 4));
        assertEquals(expected, cmd.indexes);
    }

    @Test
    void testUnmarkWithSpacesInIndexes() throws StudyMateException {
        Command cmd = parser.parse("unmark 2 , 4 , 6");
        assertEquals(CommandType.UNMARK, cmd.type);
        LinkedHashSet<Integer> expected = new LinkedHashSet<>(List.of(1, 3, 5));
        assertEquals(expected, cmd.indexes);
    }
}
