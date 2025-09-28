package seedu.studymate;

import seedu.studymate.parser.Command;
import seedu.studymate.parser.CommandType;
import seedu.studymate.parser.Parser;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.ui.MessageHandler;

import java.util.Scanner;

public class StudyMate {
    /**
     * Main entry-point for the StudyMate application.
     */

    private final static TaskList taskList = new TaskList();

    public static void main(String[] args) {
        Parser parser = new Parser();
        Scanner sc = new Scanner(System.in);

        sendWelcomeMessage();

        while (true) {
            String input = readInput(sc);
            Command cmd = parser.parse(input);
            if (cmd.type == CommandType.BYE) {
                break;
            }
            executeCommand(cmd);
        }
        sc.close();
        sendExitMessage();
    }

    /**
     * Sends a welcome message to user
     */
    private static void sendWelcomeMessage() {
        MessageHandler.sendMessage("Hello from StudyMate");
    }

    /**
     * Sends an exit message to user
     */
    private static void sendExitMessage() {
        MessageHandler.sendMessage("Bye. Hope to see you again soon!");
    }

    /**
     * Reads a single line of input from the scanner
     *
     * @param scanner The Scanner object to read from
     * @return The trimmed input string
     */
    private static String readInput(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    /**
     * Executes the appropriate command based on the parsed input
     *
     * @param cmd Command class holding the command to be executed and the description
     */
    private static void executeCommand(Command cmd) {
        switch (cmd.type) {
        case TODO -> taskList.addToDo(cmd.desc);
        case DEADLINE -> taskList.addDeadline(cmd.desc, cmd.message);
        case LIST -> MessageHandler.sendTaskList(taskList);
        }
    }
}
