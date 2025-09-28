package seedu.studymate;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.Command;
import seedu.studymate.parser.CommandHandler;
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
        sendWelcomeMessage();

        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                String input = readInput(sc);
                Command cmd = Parser.parse(input);
                if (cmd.type == CommandType.BYE) {
                    break;
                }
                CommandHandler.executeCommand(taskList, cmd);
            } catch (StudyMateException e) {
                MessageHandler.sendMessage(e.getMessage());
            }
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
}
