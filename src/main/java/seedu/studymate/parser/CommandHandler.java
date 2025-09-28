package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.ui.MessageHandler;

public class CommandHandler {

    /**
     * Executes the appropriate command based on the parsed input
     *
     * @param cmd Command class holding the command to be executed and the description
     */
    public static void executeCommand(TaskList taskList, Command cmd) throws StudyMateException {
        switch (cmd.type) {
        case TODO -> handleToDo(taskList, cmd);
        case DEADLINE -> handleDeadline(taskList, cmd);
        case LIST -> handleList(taskList);
        case MARK -> handleMark(taskList, cmd);
        case UNMARK -> handleUnmark(taskList, cmd);
        case DELETE -> handleDelete(taskList, cmd);
        default -> throw new StudyMateException("Invalid Command");
        }
    }

    private static void handleToDo(TaskList taskList, Command cmd) {
        taskList.addToDo(cmd.desc);
    }

    private static void handleDeadline(TaskList taskList, Command cmd) {
        taskList.addDeadline(cmd.desc, cmd.message);
    }

    private static void handleList(TaskList taskList) {
        MessageHandler.sendTaskList(taskList);
    }

    private static void handleMark(TaskList taskList, Command cmd) throws StudyMateException {
        int taskNumber = parseAndValidateTaskNumber(cmd, taskList.getCount());
        taskList.mark(taskNumber);
    }

    private static void handleUnmark(TaskList taskList, Command cmd) throws StudyMateException {
        int taskNumber = parseAndValidateTaskNumber(cmd, taskList.getCount());
        taskList.unmark(taskNumber);
    }

    private static void handleDelete(TaskList taskList, Command cmd) throws StudyMateException {
        int taskNumber = parseAndValidateTaskNumber(cmd, taskList.getCount());
        taskList.delete(taskNumber);
    }

    private static int parseAndValidateTaskNumber(Command cmd, int max) throws StudyMateException {
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(cmd.desc);
        } catch (NumberFormatException e) {
            throw new StudyMateException("Invalid task number!");
        }

        if (taskNumber < 1 || taskNumber > max) {
            throw new StudyMateException("There is no such task number!");
        }

        return taskNumber;
    }
}
