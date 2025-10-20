package seedu.studymate.tasks;

import seedu.studymate.database.DataFormatting;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.ui.MessageFormatting;

public class Event extends Task {
    private DateTimeArg from;
    private DateTimeArg to;

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

    /**
     * Sets the from date of the event
     * @param from The new from date
     */
    public void setFrom(DateTimeArg from) {
        this.from = from;
    }

    /**
     * Sets the to date of the event
     * @param to The new to date
     */
    public void setTo(DateTimeArg to) {
        this.to = to;
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
