package seedu.studymate;

import seedu.studymate.database.Storage;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.parser.Command;
import seedu.studymate.parser.CommandHandler;
import seedu.studymate.parser.CommandType;
import seedu.studymate.parser.Parser;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.reminders.Scheduler;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.ui.MessageHandler;

import java.util.Scanner;

public class StudyMate {
    /**
     * Main entry-point for the StudyMate application.
     */
    private static final String FILE_PATH = "data/tasks.txt";
    private static final TaskList taskList = new TaskList();
    private static final ReminderList reminderList = new ReminderList();
    private static final Scheduler scheduler = new Scheduler(reminderList);


    public static void main(String[] args) {
        sendWelcomeMessage();

        Storage storage = new Storage(FILE_PATH);
        Scanner sc = new Scanner(System.in);
        Parser parser = new Parser();

        // Load existing tasks from file.
        try {
            storage.load(taskList, reminderList);
            MessageHandler.sendMessage("Loaded " + taskList.getCount() + " task(s) from file.");
        } catch (StudyMateException e) {
            MessageHandler.sendMessage("Error loading tasks: " + e.getMessage());
        }

        while (true) {
            try {
                String input = readInput(sc);
                Command cmd = parser.parse(input);
                if (cmd.type == CommandType.BYE) {
                    CommandHandler.cleanup();
                    break;
                }
                CommandHandler.executeCommand(taskList, reminderList, cmd);

                storage.save(taskList.getTasks(), reminderList.getReminders());
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
