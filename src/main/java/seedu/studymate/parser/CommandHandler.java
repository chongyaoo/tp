package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.tasks.Task;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.timer.Timer;
import seedu.studymate.timer.TimerState;
import seedu.studymate.ui.MessageHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandHandler {

    private static Timer activeTimer = null;
    private static ScheduledExecutorService scheduler = null;

    private static final Logger logger = Logger.getLogger("Command Handler Logger");

    /**
     * Executes the appropriate command based on the parsed input
     *
     * @param cmd Command class holding the command to be executed and the description
     */
    public static void executeCommand(TaskList taskList, ReminderList reminderList, Command cmd)
            throws StudyMateException {
        switch (cmd.type) {

        // Task Commands
        case TODO -> handleToDo(taskList, cmd);
        case DEADLINE -> handleDeadline(taskList, cmd);
        case LIST -> handleList(taskList);
        case MARK -> handleMark(taskList, cmd);
        case UNMARK -> handleUnmark(taskList, cmd);
        case DELETE -> handleDelete(taskList, cmd);

        // Reminder Commands
        case REM_ADD_REC -> handleRemAddRec(reminderList, cmd);
        case REM_ADD_ONETIME -> handleRemAddOneTime(reminderList, cmd);
        case REM_LS -> handleRemList(reminderList);
        case REM_RM -> handleRemRm(reminderList, cmd);

        // Timer Commands
        case START -> handleTimerStart(taskList, cmd);
        case PAUSE -> handleTimerPause();
        case RESUME -> handleTimerResume();
        case RESET -> handleTimerReset();
        case STAT -> handleTimerStat();

        // Exception Handling
        default -> throw new StudyMateException("Invalid Command");
        }
    }

    /**
     * Clears the timer when program is exited
     */
    public static void cleanup() {
        if (activeTimer != null) {
            activeTimer.reset();
            activeTimer = null;
        }

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    private static void handleToDo(TaskList taskList, Command cmd) {
        taskList.addToDo(cmd.desc);
        int listCount = taskList.getCount();
        Task newTask = taskList.getTask(listCount - 1);
        MessageHandler.sendAddTaskMessage(newTask, listCount);
    }

    private static void handleDeadline(TaskList taskList, Command cmd) {
        taskList.addDeadline(cmd.desc, cmd.datetime);
        int listCount = taskList.getCount();
        Task newTask = taskList.getTask(listCount - 1);
        MessageHandler.sendAddTaskMessage(newTask, listCount);
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

    private static void handleRemAddRec(ReminderList reminderList, Command cmd) {
        reminderList.addReminderRec(cmd.message, cmd.datetime, cmd.remindInterval);
    }

    private static void handleRemAddOneTime(ReminderList reminderList, Command cmd) {
        reminderList.addReminderOneTime(cmd.desc, cmd.datetime);
    }

    private static void handleRemList(ReminderList reminderList) {
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
        assert(activeTimer.getState() == TimerState.IDLE);
        activeTimer.start();
        assert(activeTimer.getState() == TimerState.RUNNING);
        startTimerMonitoring();
        MessageHandler.sendTimerStartMessage(cmd.duration, activeTimer.getLabel());
    }

    private static void handleTimerPause() throws StudyMateException {
        if (activeTimer == null) {
            throw new StudyMateException("No timer is currently active");
        }
        if (activeTimer.getState() == TimerState.IDLE) {
            throw new StudyMateException("Timer is not running");
        }
        if (activeTimer.getState() == TimerState.PAUSED) {
            throw new StudyMateException("Timer is already paused");
        }
        assert(activeTimer.getState() == TimerState.RUNNING);
        activeTimer.pause();
        MessageHandler.sendTimerPauseMessage(activeTimer.getRemainingTime(), activeTimer.getLabel());
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
        assert(activeTimer.getState() == TimerState.PAUSED);
        activeTimer.resume();
        MessageHandler.sendTimerResumeMessage(activeTimer.getRemainingTime(), activeTimer.getLabel());
    }

    private static void handleTimerReset() throws StudyMateException {
        if (activeTimer == null) {
            throw new StudyMateException("No timer is currently active");
        }
        activeTimer.reset();
        activeTimer = null;

        MessageHandler.sendTimerResetMessage();
    }

    private static void handleTimerStat() throws StudyMateException {
        if (activeTimer == null) {
            throw new StudyMateException("No timer is currently active");
        }

        MessageHandler.sendTimerStatMessage(activeTimer.toString()); //
    }

    private static void startTimerMonitoring() {
        assert(scheduler == null);
        // Initialise a scheduler to check if timer is done
        scheduler = Executors.newSingleThreadScheduledExecutor();
        logger.log(Level.INFO, "Starting timer monitoring");

        Runnable timerCheckTask = () -> {
            if (activeTimer == null || activeTimer.getState() == TimerState.IDLE) {
                // does scheduler cleanup when timer isn't running
                if (activeTimer != null && !scheduler.isShutdown()) {
                    scheduler.shutdown();
                    logger.log(Level.INFO, "Scheduler shutdown");
                    scheduler = null;
                }
                return;
            }

            // If PAUSED, the timer will not advance, and the code below will be skipped.
            if (activeTimer.getState() != TimerState.RUNNING) {
                return;
            }

            activeTimer.getRemainingTime();

            // Timer run out
            if (activeTimer.getState() == TimerState.IDLE) {
                MessageHandler.sendTimerEndedMessage();
                logger.log(Level.INFO, "Timer ended");

                // Reset active timer when timer is done
                if (scheduler != null) {
                    scheduler.shutdown();
                    logger.log(Level.INFO, "Scheduler shutdown");
                    scheduler = null;
                }
                activeTimer = null;
            }
        };

        // Schedule check every second
        scheduler.scheduleAtFixedRate(timerCheckTask, 0, 1, TimeUnit.SECONDS);
        logger.log(Level.INFO, "Scheduler initialised and checking every second");
    }
}
