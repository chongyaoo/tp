package seedu.studymate.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Command {
    public CommandType type;
    // for descriptions
    private String desc;
    // for reminders
    private String message;
    // might refactor these to datetime
    LocalDate date;
    LocalTime time;
    ArrayList<Integer> indices;

    public Command(CommandType type) {
        this.type = type;
    }
}
