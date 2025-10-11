package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;

import java.util.LinkedHashSet;

public class IndexValidator {
    public static void validateIndexes(LinkedHashSet<Integer> indexes, int max) throws StudyMateException {
        for (Integer index : indexes) {
            if (index < 0 || index >= max) {
                throw new StudyMateException("Invalid index ranges given!");
            }
        }
    }

    public static void validateIndex(int index, int max) throws StudyMateException {
        if (index < 0 || index >= max) {
            throw new StudyMateException("Invalid index ranges given!");
        }
    }
}
