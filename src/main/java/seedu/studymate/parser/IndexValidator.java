package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;

import java.util.LinkedHashSet;

/**
 * Validates indexes to ensure they are within valid bounds for list operations.
 */
public class IndexValidator {
    /**
     * Validates a set of indexes against a maximum bound.
     *
     * @param indexes The set of indexes to validate
     * @param max The maximum valid index (exclusive)
     * @throws StudyMateException If any index is out of bounds
     */
    public static void validateIndexes(LinkedHashSet<Integer> indexes, int max) throws StudyMateException {
        for (Integer index : indexes) {
            if (index < 0 || index >= max) {
                throw new StudyMateException("Invalid index ranges given!");
            }
        }
    }

    /**
     * Validates a single index against a maximum bound.
     *
     * @param index The index to validate
     * @param max The maximum valid index (exclusive)
     * @throws StudyMateException If the index is out of bounds
     */
    public static void validateIndex(int index, int max) throws StudyMateException {
        if (index < 0 || index >= max) {
            throw new StudyMateException("Invalid index given!");
        }
    }
}
