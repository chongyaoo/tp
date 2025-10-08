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
}
