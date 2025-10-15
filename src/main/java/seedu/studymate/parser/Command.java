package seedu.studymate.parser;

import java.time.Duration;
import java.util.LinkedHashSet;

public class Command {
    public CommandType type;
    // for descriptions
    public String desc;
    // for reminders
    public String message;
    DateTimeArg datetime;
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

    public Command(CommandType type, String desc, String message) {
        this.type = type;
        this.desc = desc;
        this.message = message;
    }

    public Command(CommandType type, String desc, DateTimeArg datetimeArg) {
        this.type = type;
        this.desc = desc;
        this.datetime = datetimeArg;
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
        this.datetime = remindAt;
        this.remindInterval = remindInterval;
    }

}
