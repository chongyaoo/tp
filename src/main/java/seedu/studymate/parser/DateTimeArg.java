package seedu.studymate.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeArg {
    private LocalDate date;
    private LocalTime time;

    public DateTimeArg (LocalDate date) {
        this.date = date;
    }

    public DateTimeArg (LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    public LocalDate getDate () {
        return date;
    }

    public LocalDateTime getDateTime () {
        return LocalDateTime.of(date, time);
    }

    // using spaces to denote empty, might be hacky
    @Override
    public String toString() {
        return (date == null ? " " : date) + (time == null ? " " : "T" + time);
    }
}
