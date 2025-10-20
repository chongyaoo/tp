package seedu.studymate.tasks;

import seedu.studymate.database.DataFormatting;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageFormatting;

public class Event extends Task {
    private final DateTimeArg from;
    private final DateTimeArg to;

    public Event(String task, DateTimeArg from, DateTimeArg to) {
        super(task);
        this.from = from;
        this.to = to;
    }

    public DateTimeArg getFrom() {
        return from;
    }

    public DateTimeArg getTo() {
        return to;
    }

    @Override
    public String toSaveString() {
        return DataFormatting.eventSaveString(isDone, name, from, to);
    }

    @Override
    public String toString() {
        return MessageFormatting.eventString(isDone, name, from, to);
    }
}
