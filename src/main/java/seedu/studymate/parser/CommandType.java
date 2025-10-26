package seedu.studymate.parser;

/**
 * Represents all possible command types in the StudyMate application.
 * This enum is used to categorize parsed user commands for task management,
 * reminders, timers, habits, and other operations.
 */
public enum CommandType {
    TODO,
    DEADLINE,
    EVENT,
    LIST,
    FIND,
    MARK,
    UNMARK,
    EDIT_DESC,
    EDIT_DEADLINE,
    EDIT_FROM,
    EDIT_TO,
    DELETE,
    REM_ADD_REC,
    REM_ADD_ONETIME,
    REM_LS,
    REM_RM,
    REM_ON,
    REM_OFF,
    REM_SNOOZE,
    START,
    PAUSE,
    RESUME,
    RESET,
    STAT,
    HABIT_ADD,
    HABIT_STREAK,
    HABIT_LIST,
    HABIT_DELETE,
    BYE
}
