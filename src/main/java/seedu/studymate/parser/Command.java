package seedu.studymate.parser;

public class Command {
    public CommandType type;
    // for descriptions
    public String desc;
    // for reminders
    public String message;
    // might refactor these to datetime
    DateTimeArg datetime;
    // for operations done on a range of indices
    int indexStart;
    int indexEnd;

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

    public Command(CommandType type, int startInclusive, int endInclusive) {
        this.type = type;
        this.indexStart = startInclusive;
        this.indexEnd = endInclusive;
    }
}
