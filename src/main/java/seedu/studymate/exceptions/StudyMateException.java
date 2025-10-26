package seedu.studymate.exceptions;

/**
 * Represents an exception specific to the StudyMate application.
 * This exception is thrown when errors occur during operations.
 */
public class StudyMateException extends Exception {

    /**
     * Constructs a new StudyMateException with the specified error message.
     *
     * @param message The detail message explaining the cause of the exception
     */
    public StudyMateException(String message) {
        super(message);
    }
}
