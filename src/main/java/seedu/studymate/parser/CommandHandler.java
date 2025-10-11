package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.tasks.ReminderList;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.timer.Timer;
import seedu.studymate.timer.TimerState;
import seedu.studymate.ui.MessageHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandHandler {

    private static Timer activeTimer = null;
    private static ScheduledExecutorService scheduler = null;

    /**
     * Executes the appropriate command based on the parsed input
     *
     * @param cmd Command class holding the command to be executed and the description
     */
    public static void executeCommand(TaskList taskList, ReminderList reminderList, Command cmd)
            throws StudyMateException {
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
        // TODO: HANDLE TIMER COMMANDS
        case START -> handleTimerStart(taskList, cmd);
        case PAUSE -> handleTimerPause();
        case RESUME -> handleTimerResume();
        case RESET -> handleTimerReset();
        case STAT -> handleTimerStat();
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
        IndexValidator.validateIndexes(cmd.indexes, taskList.getCount());
        taskList.mark(cmd.indexes);
    }

    private static void handleUnmark(TaskList taskList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndexes(cmd.indexes, taskList.getCount());
        taskList.unmark(cmd.indexes);
    }

    private static void handleDelete(TaskList taskList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndexes(cmd.indexes, taskList.getCount());
        taskList.delete(cmd.indexes);
    }

    private static void handleRemAdd(ReminderList reminderList, Command cmd) {
        reminderList.addReminder(cmd.desc, cmd.datetime);
    }

    private static void handleRemList(ReminderList reminderList) throws StudyMateException {
        MessageHandler.sendReminderList(reminderList);
    }

    private static void handleRemRm(ReminderList reminderList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndexes(cmd.indexes, reminderList.getCount());
        reminderList.delete(cmd.indexes);
    }

    private static void handleTimerStart(TaskList taskList, Command cmd) throws StudyMateException {
        if (activeTimer != null && activeTimer.getState() != TimerState.IDLE) {
            throw new StudyMateException("A timer is already running or paused. Please stop it first.");
        }

        long durationSec = cmd.duration * 60;

        Timer timer;

        // Task Index provided
        if (cmd.indexes != null && !cmd.indexes.isEmpty()) {
            Integer index = cmd.indexes.iterator().next();
            IndexValidator.validateIndex(index, taskList.getCount());

            timer = new Timer(taskList, durationSec, index);
        } else {
            // Custom label or "Focus session" provided
            String label = cmd.desc;
            timer = new Timer(label, durationSec);
        }

        activeTimer = timer;
        activeTimer.start();

        startTimerMonitoring(activeTimer);
        MessageHandler.sendTimerStartMessage(); // TODO: IMPLEMENT START TIMER MESSAGE
    }

    private static void handleTimerPause() throws StudyMateException {
        if (activeTimer == null) {
            throw new StudyMateException("No timer is currently active");
        }
        if (activeTimer.getState() != TimerState.RUNNING) {
            throw new StudyMateException("Timer is not running");
        }
        if (activeTimer.getState() == TimerState.PAUSED) {
            throw new StudyMateException("Timer is already paused");
        }
        activeTimer.pause();
        MessageHandler.sendTimerPauseMessage(); // TODO: IMPLEMENT PAUSE TIMER MESSAGE
    }

    private static void handleTimerResume() throws StudyMateException {
        if (activeTimer == null) {
            throw new StudyMateException("No timer is currently active");
        }
        if (activeTimer.getState() == TimerState.IDLE) {
            throw new StudyMateException("Timer is not running");
        }
        if (activeTimer.getState() == TimerState.RUNNING) {
            throw new StudyMateException("Timer is already running");
        }
        activeTimer.resume();
        MessageHandler.sendTimerResumeMessage(); // TODO: IMPLEMENT RESUME TIMER MESSAGE
    }

    private static void handleTimerReset() throws StudyMateException {
        if (activeTimer == null) {
            throw new StudyMateException("No timer is currently active");
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        activeTimer.reset();
        activeTimer = null;

        MessageHandler.sendTimerResetMessage(); // TODO: IMPLEMENT RESET TIMER MESSAGE
    }

    private static void handleTimerStat() throws StudyMateException {
        if (activeTimer == null) {
            throw new StudyMateException("No timer is currently active");
        }
        TimerState state = activeTimer.getState();
        long remainingSec = activeTimer.getRemainingTime();
        String formattedTime = formatDuration(remainingSec);
        String label = activeTimer.getLabel();

        String message = "Timer Status:\n"
                + "  State: " + state.toString() + "\n"
                + "  Time Left: " + formattedTime + "\n"
                + "  Label: " + label;

        MessageHandler.sendTimerStatMessage(message); // TODO: IMPLEMENT STAT TIMER MESSAGE
    }

    private static String formatDuration(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private static void startTimerMonitoring(Timer timer) {
        // Initialise a scheduler to check if timer is done
        scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable timerCheckTask = () -> {
            if (activeTimer == null || activeTimer.getState() != TimerState.RUNNING) {
                if (activeTimer != null && !scheduler.isShutdown()) {
                    scheduler.shutdown();
                    scheduler = null;
                }
                return;
            }

            activeTimer.getRemainingTime();

            // Timer run out
            if (activeTimer.getState() == TimerState.IDLE) {
                MessageHandler.sendTimerEndedMessage(); // TODO: IMPLEMENT TIMER ENDED MESSAGE

                // Reset active timer when timer is done
                if (scheduler != null) {
                    scheduler.shutdown();
                    scheduler = null;
                }
                activeTimer = null;
            }
        };

        // Schedule check every second
        scheduler.scheduleAtFixedRate(timerCheckTask, 0, 1, TimeUnit.SECONDS);
    }
}
