package seedu.studymate.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Command {
    public CommandType type;
    // for descriptions
    public String desc;
    // for reminders
    public String message;
    // might refactor these to datetime
    LocalDate date;
    LocalTime time;
    ArrayList<Integer> indices;

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
}
