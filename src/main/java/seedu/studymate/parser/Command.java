package seedu.studymate.parser;

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

    // possibly hacky, may need to change this but I cant think of better workarounds for reminder constuctor
    public Command(CommandType type, DateTimeArg datetimeArg, String message) {
        this.type = type;
        this.message = message;
        this.datetime = datetimeArg;
    }

    public Command(CommandType type, LinkedHashSet<Integer> indexes) {
        this.type = type;
        this.indexes = indexes;
    }
}
