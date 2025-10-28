package seedu.studymate.parser;

import java.time.Duration;
import java.util.LinkedHashSet;

/**
 * Represents a parsed command containing all relevant information for command execution.
 * This class encapsulates the command type and associated parameters such as descriptions,
 * dates, indexes, and durations.
 */
public class Command {
    public CommandType type;
    // for descriptions
    public String desc;
    public String substring;
    public boolean isSorted = false;
    // for reminders
    public String message;
    public DateTimeArg datetime0;
    public DateTimeArg datetime1;
    // for operations done on a range of indices
    public LinkedHashSet<Integer> indexes;
    public int index;

    // Timer
    public long duration;

    public Duration interval;

    public Duration snoozeDuration;

    /**
     * Constructs a Command with only a command type.
     *
     * @param type The type of command
     */
    public Command(CommandType type) {
        this.type = type;
    }

    /**
     * Constructs a Command with a type and description.
     *
     * @param type The type of command
     * @param desc The description or name associated with the command
     */
    public Command(CommandType type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * Constructs a Command with a type and index.
     *
     * @param type The type of command
     * @param index The index of the item to operate on
     */
    public Command(CommandType type, int index) {
        this.type = type;
        this.index = index;
    }

    /**
     * Constructs a Command with a type, index, and description.
     *
     * @param type The type of command
     * @param index The index of the item
     * @param desc The description
     */
    public Command(CommandType type, int index, String desc) {
        this.type = type;
        this.index = index;
        this.desc = desc;
    }

    /**
     * Constructs a Command with a type, index, and date-time argument.
     *
     * @param type The type of command
     * @param index The index of the item
     * @param dateTimeArg The date-time information
     */
    public Command(CommandType type, int index, DateTimeArg dateTimeArg) {
        this.type = type;
        this.index = index;
        this.datetime0 = dateTimeArg;
    }

    /**
     * Constructs a Command with a substring and type (used for find command).
     *
     * @param substring The substring to search for
     * @param type The type of command
     */
    public Command(String substring, CommandType type) {
        this.type = type;
        this.substring = substring;
    }

    /**
     * Constructs a Command with a type and sorted flag.
     *
     * @param type The type of command
     * @param isSorted Whether the output should be sorted
     */
    public Command(CommandType type, boolean isSorted) {
        this.type = type;
        this.isSorted = isSorted;
    }

    /**
     * Constructs a Command with a type, description, and date-time argument.
     *
     * @param type The type of command
     * @param desc The description
     * @param dateTimeArg The date-time information
     */
    public Command(CommandType type, String desc, DateTimeArg dateTimeArg) {
        this.type = type;
        this.desc = desc;
        this.datetime0 = dateTimeArg;
    }

    /**
     * Constructs a Command with a type, description, and two date-time arguments.
     *
     * @param type The type of command
     * @param desc The description
     * @param datetimeArg0 The first date-time argument (from)
     * @param dateTimeArg1 The second date-time argument (to)
     */
    public Command(CommandType type, String desc, DateTimeArg datetimeArg0, DateTimeArg dateTimeArg1) {
        this.type = type;
        this.desc = desc;
        this.datetime0 = datetimeArg0;
        this.datetime1 = dateTimeArg1;
    }

    /**
     * Constructs a Command with a type and set of indexes.
     *
     * @param type The type of command
     * @param indexes The set of indexes to operate on
     */
    public Command(CommandType type, LinkedHashSet<Integer> indexes) {
        this.type = type;
        this.indexes = indexes;
    }

    /**
     * Constructs a Timer Command with index, label, and duration.
     *
     * @param type The type of command
     * @param index The task index (can be null)
     * @param label The timer label
     * @param duration The duration in minutes
     */
    public Command(CommandType type, Integer index, String label, long duration) {
        this.type = type;
        if (index != null) {
            this.indexes = new LinkedHashSet<>();
            this.indexes.add(index);
        }
        this.desc = label;
        this.duration = duration;
    }

    /**
     * Constructs a Timer Command with index and duration.
     *
     * @param type The type of command
     * @param index The task index (can be null)
     * @param duration The duration in minutes
     */
    public Command(CommandType type, Integer index, Integer duration) {
        this.type = type;
        if (index != null) {
            this.indexes = new LinkedHashSet<>();
            this.indexes.add(index);
        }
        this.duration = duration;
    }

    /**
     * Constructs a Timer Command with label and duration.
     *
     * @param type The type of command
     * @param label The timer label
     * @param duration The duration in minutes
     */
    public Command(CommandType type, String label, Integer duration) {
        this.type = type;
        this.desc = label;
        this.duration = duration;
    }

    /**
     * Constructs a Command for snoozing a reminder.
     *
     * @param type The type of command
     * @param index The reminder index
     * @param snoozeDuration The duration to snooze
     */
    public Command(CommandType type, int index, Duration snoozeDuration) {
        this.type = type;
        this.index = index;
        this.snoozeDuration = snoozeDuration;
    }

    /**
     * Constructs a Command for a recurring reminder.
     *
     * @param type The type of command
     * @param name The reminder name
     * @param remindAt The time to remind at
     * @param interval The recurring interval
     */
    public Command(CommandType type, String name, DateTimeArg remindAt, Duration interval) {
        this.type = type;
        this.message = name;
        this.datetime0 = remindAt;
        this.interval = interval;
    }

    /**
     * Constructs a Command for a habit with name and interval.
     *
     * @param type The type of command
     * @param name The habit name
     * @param interval The habit interval
     */
    public Command(CommandType type, String name, Duration interval) {
        this.type = type;
        this.desc = name;
        this.interval = interval;
    }
}
