package seedu.studymate.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalTime getTime() {
        return this.time;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(date, time);
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.date = localDateTime.toLocalDate();
        this.time = localDateTime.toLocalTime();
    }

    // using spaces to denote empty, might be hacky
    @Override
    public String toString() {
        return (date == null ? "" : date) +
                (time == null ? "" : "T" + time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

}
