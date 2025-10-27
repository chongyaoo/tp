package seedu.studymate.parser;

import org.junit.jupiter.api.Test;
import seedu.studymate.exceptions.StudyMateException;

import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IndexValidatorTest {

    @Test
    public void validateIndex_validIndex_noExceptionThrown() {
        assertDoesNotThrow(() -> IndexValidator.validateIndex(0, 5));
        assertDoesNotThrow(() -> IndexValidator.validateIndex(2, 5));
        assertDoesNotThrow(() -> IndexValidator.validateIndex(4, 5));
    }

    @Test
    public void validateIndex_negativeIndex_throwsException() {
        StudyMateException exception = assertThrows(StudyMateException.class,
                () -> IndexValidator.validateIndex(-1, 5));
        assertEquals("Invalid index given!", exception.getMessage());
    }

    @Test
    public void validateIndex_indexEqualToMax_throwsException() {
        StudyMateException exception = assertThrows(StudyMateException.class,
                () -> IndexValidator.validateIndex(5, 5));
        assertEquals("Invalid index given!", exception.getMessage());
    }

    @Test
    public void validateIndex_indexGreaterThanMax_throwsException() {
        StudyMateException exception = assertThrows(StudyMateException.class,
                () -> IndexValidator.validateIndex(6, 5));
        assertEquals("Invalid index given!", exception.getMessage());
    }

    @Test
    public void validateIndex_maxZero_throwsException() {
        StudyMateException exception = assertThrows(StudyMateException.class,
                () -> IndexValidator.validateIndex(0, 0));
        assertEquals("Invalid index given!", exception.getMessage());
    }

    @Test
    public void validateIndexes_validIndexes_noExceptionThrown() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        indexes.add(2);
        indexes.add(4);

        assertDoesNotThrow(() -> IndexValidator.validateIndexes(indexes, 5));
    }

    @Test
    public void validateIndexes_emptySet_noExceptionThrown() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();

        assertDoesNotThrow(() -> IndexValidator.validateIndexes(indexes, 5));
    }

    @Test
    public void validateIndexes_containsNegativeIndex_throwsException() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        indexes.add(-1);
        indexes.add(2);

        StudyMateException exception = assertThrows(StudyMateException.class,
                () -> IndexValidator.validateIndexes(indexes, 5));
        assertEquals("Invalid index ranges given!", exception.getMessage());
    }

    @Test
    public void validateIndexes_containsIndexEqualToMax_throwsException() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        indexes.add(5);
        indexes.add(2);

        StudyMateException exception = assertThrows(StudyMateException.class,
                () -> IndexValidator.validateIndexes(indexes, 5));
        assertEquals("Invalid index ranges given!", exception.getMessage());
    }

    @Test
    public void validateIndexes_containsIndexGreaterThanMax_throwsException() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        indexes.add(6);
        indexes.add(2);

        StudyMateException exception = assertThrows(StudyMateException.class,
                () -> IndexValidator.validateIndexes(indexes, 5));
        assertEquals("Invalid index ranges given!", exception.getMessage());
    }

    @Test
    public void validateIndexes_singleValidIndex_noExceptionThrown() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(3);

        assertDoesNotThrow(() -> IndexValidator.validateIndexes(indexes, 5));
    }

    @Test
    public void validateIndexes_singleInvalidIndex_throwsException() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(10);

        StudyMateException exception = assertThrows(StudyMateException.class,
                () -> IndexValidator.validateIndexes(indexes, 5));
        assertEquals("Invalid index ranges given!", exception.getMessage());
    }

    @Test
    public void validateIndexes_mixedValidAndInvalidIndexes_throwsException() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(1);
        indexes.add(3);
        indexes.add(-2);
        indexes.add(8);

        StudyMateException exception = assertThrows(StudyMateException.class,
                () -> IndexValidator.validateIndexes(indexes, 5));
        assertEquals("Invalid index ranges given!", exception.getMessage());
    }

    @Test
    public void validateIndexes_allIndexesAtBoundary_noExceptionThrown() {
        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        indexes.add(4);

        assertDoesNotThrow(() -> IndexValidator.validateIndexes(indexes, 5));
    }
}
