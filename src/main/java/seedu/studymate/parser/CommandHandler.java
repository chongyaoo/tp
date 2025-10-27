package seedu.studymate.parser;

import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.habits.HabitList;
import seedu.studymate.reminders.Reminder;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.tasks.Task;
import seedu.studymate.tasks.TaskList;
import seedu.studymate.timer.Timer;
import seedu.studymate.timer.TimerState;
import seedu.studymate.ui.MessageHandler;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the execution of parsed commands by delegating to appropriate handler methods.
 * This class manages all command operations including tasks, reminders, timers, and habits.
 */
public class CommandHandler {

    private static Timer activeTimer = null;
    private static ScheduledExecutorService scheduler = null;

    private static final Logger logger = Logger.getLogger("Command Handler Logger");

    /**
     * Executes the appropriate command based on the parsed input
     *
     * @param taskList The task list to operate on
     * @param reminderList The reminder list to operate on
     * @param habitList The habit list to operate on
     * @param cmd Command object holding the command to be executed and associated data
     * @throws StudyMateException If the command execution fails
     */
    public static void executeCommand(TaskList taskList, ReminderList reminderList, HabitList habitList, Command cmd)
            throws StudyMateException {
        switch (cmd.type) {

        // Task Commands
        case TODO -> handleToDo(taskList, cmd);
        case DEADLINE -> handleDeadline(taskList, cmd);
        case EVENT -> handleEvent(taskList, cmd);
        case LIST -> handleList(taskList, cmd);
        case FIND -> handleFind(taskList, cmd);
        case EDIT_DESC -> handleEdit(taskList, cmd);
        case EDIT_DEADLINE -> handleEdit(taskList, cmd);
        case EDIT_FROM -> handleEdit(taskList, cmd);
        case EDIT_TO -> handleEdit(taskList, cmd);
        case MARK -> handleMark(taskList, cmd);
        case UNMARK -> handleUnmark(taskList, cmd);
        case DELETE -> handleDelete(taskList, cmd);

        // Reminder Commands
        case REM_ADD_REC -> handleRemAddRec(reminderList, cmd);
        case REM_ADD_ONETIME -> handleRemAddOneTime(reminderList, cmd);
        case REM_LS -> handleRemList(reminderList);
        case REM_RM -> handleRemRm(reminderList, cmd);
        case REM_ON -> handleRemOn(reminderList, cmd);
        case REM_OFF -> handleRemOff(reminderList, cmd);
        case REM_SNOOZE -> handleRemSnooze(reminderList, cmd);

        // Timer Commands
        case START -> handleTimerStart(taskList, cmd);
        case PAUSE -> handleTimerPause();
        case RESUME -> handleTimerResume();
        case RESET -> handleTimerReset();
        case STAT -> handleTimerStat();

        // Habit Commands
        case HABIT_ADD -> handleHabitAdd(habitList, cmd);
        case HABIT_STREAK -> handleHabitStreak(habitList, cmd);
        case HABIT_LIST -> handleHabitList(habitList);
        case HABIT_DELETE -> handleHabitDelete(habitList, cmd);

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

        ScheduledExecutorService currentScheduler = scheduler;
        if (currentScheduler != null) {
            scheduler = null;  //
            if (!currentScheduler.isShutdown()) {
                currentScheduler.shutdownNow();
            }
        }
    }

    /**
     * Handles adding a to-do task to the task list.
     *
     * @param taskList The task list to add to
     * @param cmd The command containing the task description
     */
    private static void handleToDo(TaskList taskList, Command cmd) {
        taskList.addToDo(cmd.desc);
        int listCount = taskList.getCount();
        Task newTask = taskList.getTask(listCount - 1);
        MessageHandler.sendAddTaskMessage(newTask, listCount);
    }

    /**
     * Handles adding a deadline task to the task list.
     *
     * @param taskList The task list to add to
     * @param cmd The command containing the task description and deadline
     */
    private static void handleDeadline(TaskList taskList, Command cmd) {
        taskList.addDeadline(cmd.desc, cmd.datetime0);
        int listCount = taskList.getCount();
        Task newTask = taskList.getTask(listCount - 1);
        MessageHandler.sendAddTaskMessage(newTask, listCount);
    }

    /**
     * Handles adding an event task to the task list.
     * Validates that the end time is not before the start time.
     *
     * @param taskList The task list to add to
     * @param cmd The command containing the event description, from date, and to date
     * @throws StudyMateException If the end time is before the start time
     */
    private static void handleEvent(TaskList taskList, Command cmd) throws StudyMateException {
        // ensures end time (cmd.datetime1) is not before start time (cmd.datetime0) of an event
        if (cmd.datetime1.compareTo(cmd.datetime0) < 0) {
            throw new StudyMateException("End time cannot be earlier than start time");
        }
        taskList.addEvent(cmd.desc, cmd.datetime0, cmd.datetime1);
        int listCount = taskList.getCount();
        Task newTask = taskList.getTask(listCount - 1);
        MessageHandler.sendAddTaskMessage(newTask, listCount);
    }

    /**
     * Handles listing tasks, either in normal or sorted order.
     *
     * @param taskList The task list to display
     * @param cmd The command containing the sort flag
     */
    private static void handleList(TaskList taskList, Command cmd) {
        if (cmd.isSorted) {
            ArrayList<Task> result = taskList.getSorted();
            MessageHandler.sendSortedTaskList(result);
        } else {
            MessageHandler.sendTaskList(taskList);
        }
    }

    private static void handleFind(TaskList taskList, Command cmd) {
        ArrayList<Task> result = taskList.findTasks(cmd.substring);
        MessageHandler.sendFindResults(result);
    }

    private static void handleMark(TaskList taskList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndexes(cmd.indexes, taskList.getCount());
        taskList.mark(cmd.indexes);
    }

    private static void handleUnmark(TaskList taskList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndexes(cmd.indexes, taskList.getCount());
        taskList.unmark(cmd.indexes);
    }

    /**
     * Handles editing a task's properties (description, deadline, from date, or to date).
     *
     * @param taskList The task list to modify
     * @param cmd The command containing the task index and new value
     * @throws StudyMateException If the index is invalid or operation fails
     */
    private static void handleEdit(TaskList taskList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndex(cmd.index, taskList.getCount());
        switch (cmd.type) {
        case EDIT_DESC -> taskList.editDesc(cmd.index, cmd.desc);
        case EDIT_DEADLINE -> taskList.editDeadline(cmd.index, cmd.datetime0);
        case EDIT_FROM -> taskList.editFrom(cmd.index, cmd.datetime0);
        case EDIT_TO -> taskList.editTo(cmd.index, cmd.datetime0);
        default -> throw new StudyMateException("Something wrong happened");
        }
    }

    private static void handleDelete(TaskList taskList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndexes(cmd.indexes, taskList.getCount());
        taskList.delete(cmd.indexes);
    }

    /**
     * Handles adding a recurring reminder to the reminder list.
     *
     * @param reminderList The reminder list to add to
     * @param cmd The command containing reminder name, datetime, and interval
     */
    private static void handleRemAddRec(ReminderList reminderList, Command cmd) {
        reminderList.addReminderRec(cmd.message, cmd.datetime0, cmd.interval);
        int reminderCount = reminderList.getCount();
        Reminder newReminder = reminderList.getReminder(reminderCount - 1);
        MessageHandler.sendAddReminderRecMessage(newReminder, reminderCount);
    }

    /**
     * Handles adding a one-time reminder to the reminder list.
     *
     * @param reminderList The reminder list to add to
     * @param cmd The command containing reminder name and datetime
     */
    private static void handleRemAddOneTime(ReminderList reminderList, Command cmd) {
        reminderList.addReminderOneTime(cmd.desc, cmd.datetime0);
        int reminderCount = reminderList.getCount();
        Reminder newReminder = reminderList.getReminder(reminderCount - 1);
        MessageHandler.sendAddReminderOneTimeMessage(newReminder, reminderCount);
    }

    private static void handleRemList(ReminderList reminderList) {
        MessageHandler.sendReminderList(reminderList);
    }

    private static void handleRemRm(ReminderList reminderList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndexes(cmd.indexes, reminderList.getCount());
        reminderList.delete(cmd.indexes);
    }

    private static void handleRemOn(ReminderList reminderList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndexes(cmd.indexes, reminderList.getCount());
        reminderList.turnOnReminders(cmd.indexes);
    }

    private static void handleRemOff(ReminderList reminderList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndexes(cmd.indexes, reminderList.getCount());
        reminderList.turnOffReminders(cmd.indexes);
    }

    private static void handleRemSnooze(ReminderList reminderList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndex(cmd.index, reminderList.getCount());
        reminderList.handleSnooze(cmd.index, cmd.snoozeDuration);
    }

    /**
     * Handles starting a new timer.
     * Validates that no other timer is running and creates a timer with the specified duration.
     *
     * @param taskList The task list (used if timer is linked to a task)
     * @param cmd The command containing timer duration and optional task index or label
     * @throws StudyMateException If another timer is running or index is invalid
     */
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

    /**
     * Handles pausing the active timer.
     *
     * @throws StudyMateException If no timer is active or timer is already paused
     */
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

    /**
     * Handles resuming a paused timer.
     *
     * @throws StudyMateException If no timer is active or timer is already running
     */
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

    /**
     * Handles resetting and stopping the active timer.
     *
     * @throws StudyMateException If no timer is active
     */
    private static void handleTimerReset() throws StudyMateException {
        if (activeTimer == null) {
            throw new StudyMateException("No timer is currently active");
        }

        ScheduledExecutorService currentScheduler = scheduler;
        if (currentScheduler != null) {
            scheduler = null;  // Clear reference first
            currentScheduler.shutdownNow();
            logger.log(Level.INFO, "Scheduler shut down with reset command");
        }

        assert(scheduler == null);
        activeTimer.reset();
        activeTimer = null;

        MessageHandler.sendTimerResetMessage();
    }

    /**
     * Handles displaying the current timer status.
     *
     * @throws StudyMateException If no timer is active
     */
    private static void handleTimerStat() throws StudyMateException {
        if (activeTimer == null) {
            throw new StudyMateException("No timer is currently active");
        }

        MessageHandler.sendTimerStatMessage(activeTimer.toString()); //
    }

    /**
     * Starts periodic monitoring of the active timer.
     * Checks every second if the timer has completed and handles cleanup.
     */
    private static void startTimerMonitoring() {
        assert(scheduler == null);
        // Initialise a scheduler to check if timer is done
        scheduler = Executors.newSingleThreadScheduledExecutor();
        logger.log(Level.INFO, "Starting timer monitoring");

        Runnable timerCheckTask = () -> {
            if (activeTimer == null) {
                // does scheduler cleanup when timer isn't running
                if (!scheduler.isShutdown()) {
                    scheduler.shutdown();
                    logger.log(Level.INFO, "Scheduler shutdown (Active timer is null)");
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
                    logger.log(Level.INFO, "Scheduler shutdown (Timer ended)");
                    scheduler = null;
                }
                activeTimer = null;
            }
        };

        // Schedule check every second
        scheduler.scheduleAtFixedRate(timerCheckTask, 0, 1, TimeUnit.SECONDS);
        logger.log(Level.INFO, "Scheduler initialised and checking every second");
    }

    private static void handleHabitAdd(HabitList habitList, Command cmd) {
        habitList.addHabit(cmd.desc, cmd.interval);
    }

    private static void handleHabitDelete(HabitList habitList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndex(cmd.index, habitList.getCount());
        habitList.deleteHabit(cmd.index);
    }

    private static void handleHabitList(HabitList habitList) {
        MessageHandler.sendHabitList(habitList);
    }

    private static void handleHabitStreak(HabitList habitList, Command cmd) throws StudyMateException {
        IndexValidator.validateIndex(cmd.index, habitList.getCount());
        habitList.incStreak(cmd.index);
    }
}
