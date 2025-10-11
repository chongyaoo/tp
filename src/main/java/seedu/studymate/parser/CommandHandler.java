package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.tasks.ReminderList;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.ui.MessageHandler;

import java.util.LinkedHashSet;

public class CommandHandler {

    /**
     * Executes the appropriate command based on the parsed input
     *
     * @param cmd Command class holding the command to be executed and the description
     */
    public static void executeCommand(TaskList taskList, ReminderList reminderList, Command cmd) throws StudyMateException {
        switch (cmd.type) {
            case TODO -> handleToDo(taskList, cmd);
            case DEADLINE -> handleDeadline(taskList, cmd);
            case LIST -> handleList(taskList);
            case MARK -> handleMark(taskList, cmd);
            case UNMARK -> handleUnmark(taskList, cmd);
            case DELETE -> handleDelete(taskList, cmd);
            case REM_ADD -> handleRemAdd(reminderList, cmd);
            case REM_LS -> handleRemList(reminderList);
            case REM_RM -> handleRemRm(reminderList, cmd);
            default -> throw new StudyMateException("Invalid Command");
        }
    }

    private static void handleToDo(TaskList taskList, Command cmd) {
        taskList.addToDo(cmd.desc);
    }

    private static void handleDeadline(TaskList taskList, Command cmd) {
        taskList.addDeadline(cmd.desc, cmd.datetime);
    }

    private static void handleList(TaskList taskList) {
        MessageHandler.sendTaskList(taskList);
    }

    private static void handleMark(TaskList taskList, Command cmd) throws StudyMateException {
        validateTaskNumber(cmd.indexes, taskList.getCount());
        taskList.mark(cmd.indexes);
    }

    private static void handleUnmark(TaskList taskList, Command cmd) throws StudyMateException {
        validateTaskNumber(cmd.indexes, taskList.getCount());
        taskList.unmark(cmd.indexes);
    }

    private static void handleDelete(TaskList taskList, Command cmd) throws StudyMateException {
        validateTaskNumber(cmd.indexes, taskList.getCount());
        taskList.delete(cmd.indexes);
    }

    private static void validateTaskNumber(LinkedHashSet<Integer> indexes, int max) throws StudyMateException {
        for (Integer index : indexes) {
            if (index < 0 || index >= max) {
                throw new StudyMateException("Invalid index ranges given!");
            }
        }
    }

    private static void handleRemAdd(ReminderList reminderList, Command cmd) {
        reminderList.addReminder(cmd.desc, cmd.datetime);
    }

    private static void handleRemList(ReminderList reminderList) throws StudyMateException {
        MessageHandler.sendReminderList(reminderList);
    }

    private static void handleRemRm(ReminderList reminderList, Command cmd) throws StudyMateException {
        validateTaskNumber(cmd.indexes, reminderList.getCount());
        reminderList.delete(cmd.indexes);
    }

}

