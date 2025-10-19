package seedu.studymate.parser;

import java.time.Duration;
import java.util.LinkedHashSet;

public class Command {
    public CommandType type;
    // for descriptions
    public String desc;
    // for reminders
    public String message;
    DateTimeArg datetime0;
    DateTimeArg datetime1;
    // for operations done on a range of indices
    LinkedHashSet<Integer> indexes;
    boolean isRecurring;

    // Timer
    Integer duration;

    // Rem trigger interval
    Duration remindInterval;

    public Command(CommandType type) {
        this.type = type;
    }

    public Command(CommandType type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Command(CommandType type, String desc, DateTimeArg dateTimeArg) {
        this.type = type;
        this.desc = desc;
        this.datetime0 = dateTimeArg;
    }

    public Command(CommandType type, String desc, DateTimeArg datetimeArg0, DateTimeArg dateTimeArg1) {
        this.type = type;
        this.desc = desc;
        this.datetime0 = datetimeArg0;
        this.datetime1 = dateTimeArg1;
    }

    public Command(CommandType type, LinkedHashSet<Integer> indexes) {
        this.type = type;
        this.indexes = indexes;
    }

    // Timer Command
    public Command(CommandType type, Integer index, String label, Integer duration) {
        this.type = type;
        if (index != null) {
            this.indexes = new LinkedHashSet<>();
            this.indexes.add(index);
        }
        this.desc = label;
        this.duration = duration;
    }

    public Command(CommandType type, Integer index, Integer duration) {
        this.type = type;
        if (index != null) {
            this.indexes = new LinkedHashSet<>();
            this.indexes.add(index);
        }
        this.duration = duration;
    }

    public Command(CommandType type, String label, Integer duration) {
        this.type = type;
        this.desc = label;
        this.duration = duration;
    }

    // recurring reminder
    public Command(CommandType type, String name, DateTimeArg remindAt, Duration remindInterval) {
        this.type = type;
        this.message = name;
        this.datetime0 = remindAt;
        this.remindInterval = remindInterval;
    }

}
