package seedu.studymate.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeArg implements Comparable<DateTimeArg> {
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
                (time == null ? "" : "T" + time.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    @Override
    public int compareTo(DateTimeArg other) {
        // Handle null dates
        if (this.date == null && other.date == null) {
            return compareTime(other);
        }
        if (this.date == null) {
            return -1; // null dates come first
        }
        if (other.date == null) {
            return 1; // null dates come first
        }

        int dateComparison = this.date.compareTo(other.date);
        if (dateComparison != 0) {
            return dateComparison;
        }

        return compareTime(other);
    }

    private int compareTime(DateTimeArg other) {
        if (this.time == null && other.time == null) {
            return 0;
        }

        if (this.time == null) {
            return -1; // null time come first
        }
        if (other.time == null) {
            return 1; // null time come first
        }

        return this.time.compareTo(other.time);
    }
}

