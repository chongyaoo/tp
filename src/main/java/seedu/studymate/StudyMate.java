package seedu.studymate;

import seedu.studymate.database.Storage;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.habits.HabitList;
import seedu.studymate.parser.Command;
import seedu.studymate.parser.CommandHandler;
import seedu.studymate.parser.CommandType;
import seedu.studymate.parser.Parser;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.reminders.Scheduler;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.ui.MessageHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.logging.LogManager;

public class StudyMate {
    /**
     * Main entry-point for the StudyMate application.
     */
    private static final String FILE_PATH = "data/StudyMate.txt";
    private static ReminderList reminderList;
    private static HabitList habitList;
    private static TaskList taskList;

    public static void main(String[] args) {
        if (isRunningFromJar()) {
            try (InputStream is = StudyMate.class.getResourceAsStream("/logging.properties")) {
                if (is != null) {
                    LogManager.getLogManager().readConfiguration(is);
                }
            } catch (IOException e) {
                // Silent fail - logging will just not be configured
            }
        }
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        sendWelcomeMessage();

        String testTime = System.getenv("TEST_TIME");
        if (testTime != null) {
            try {
                LocalDateTime fixedTime = LocalDateTime.parse(testTime);
                Clock clock = Clock.fixed(fixedTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
                reminderList = new ReminderList(clock);
                habitList = new HabitList(clock);
            } catch (DateTimeParseException e) {
                System.err.println("Invalid TEST_TIME format: " + testTime);
            }
        } else {
            reminderList = new ReminderList();
            habitList = new HabitList();
        }
        taskList = new TaskList();
        Storage storage = new Storage(FILE_PATH);
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        Parser parser = new Parser();
        Scheduler scheduler = new Scheduler(reminderList);

        // Load existing tasks from file.
        try {
            storage.load(taskList, reminderList, habitList);
            MessageHandler.sendMessage("Loaded " + taskList.getCount() + " task(s) from file.");
            MessageHandler.sendMessage("Loaded " + reminderList.getCount() + " reminder(s) from file.");
            MessageHandler.sendMessage("Loaded " + habitList.getCount() + " habit(s) from file.");
        } catch (StudyMateException e) {
            MessageHandler.sendMessage("Error loading!");
        }
        scheduler.start();
        while (true) {
            try {
                String input = readInput(sc);
                Command cmd = parser.parse(input);
                if (cmd.type == CommandType.BYE) {
                    CommandHandler.cleanup();
                    break;
                }
                CommandHandler.executeCommand(taskList, reminderList, habitList, cmd);

                storage.save(taskList.getTasks(), reminderList.getReminders(), habitList.getAllHabits());
            } catch (StudyMateException e) {
                MessageHandler.sendMessage(e.getMessage());
            }
        }
        scheduler.shutdown();
        sc.close();
        sendExitMessage();
    }

    /**
     * Sends a welcome message to user
     */
    private static void sendWelcomeMessage() {
        MessageHandler.sendMessage("Hello from StudyMate!");
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

    private static boolean isRunningFromJar() {
        try {
            String protocol = StudyMate.class.getResource("StudyMate.class").getProtocol();
            return "jar".equals(protocol);
        } catch (NullPointerException e) {
            // might force a true if we rather disable logs at all if we can't tell if running from jar
            return false;
        }
    }
}
