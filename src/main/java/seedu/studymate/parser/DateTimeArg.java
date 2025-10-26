package seedu.studymate.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a date and time argument that can be used for tasks, reminders, and habits.
 * Implements Comparable to allow sorting by date and time.
 */
public class DateTimeArg implements Comparable<DateTimeArg> {
    private LocalDate date;
    private LocalTime time;

    /**
     * Constructs a DateTimeArg with only a date (no time component).
     *
     * @param date The date component
     */
    public DateTimeArg (LocalDate date) {
        this.date = date;
    }

    /**
     * Constructs a DateTimeArg with both date and time components.
     *
     * @param date The date component
     * @param time The time component
     */
    public DateTimeArg (LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Sets the date component.
     *
     * @param date The new date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Sets the time component.
     *
     * @param time The new time
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Gets the time component.
     *
     * @return The time, or null if not set
     */
    public LocalTime getTime() {
        return this.time;
    }

    /**
     * Gets the date component.
     *
     * @return The date
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Combines date and time into a LocalDateTime.
     *
     * @return A LocalDateTime combining both date and time
     */
    public LocalDateTime getDateTime() {
        return LocalDateTime.of(date, time);
    }

    /**
     * Sets both date and time from a LocalDateTime.
     *
     * @param localDateTime The LocalDateTime to extract date and time from
     */
    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.date = localDateTime.toLocalDate();
        this.time = localDateTime.toLocalTime();
    }

    /**
     * Returns a string representation of the date-time.
     * Format: yyyy-MM-ddTHH:mm (if time is present), otherwise just yyyy-MM-dd.
     *
     * @return The formatted string
     */
    @Override
    public String toString() {
        return (date == null ? "" : date) +
                (time == null ? "" : "T" + time.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    /**
     * Compares this DateTimeArg with another for ordering.
     * Comparison is first by date, then by time. Null values are considered smaller.
     *
     * @param other The DateTimeArg to compare with
     * @return A negative integer, zero, or a positive integer as this object is less than,
     *         equal to, or greater than the specified object
     */
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

    /**
     * Compares the time component with another DateTimeArg.
     *
     * @param other The DateTimeArg to compare time with
     * @return A negative integer, zero, or a positive integer based on time comparison
     */
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
