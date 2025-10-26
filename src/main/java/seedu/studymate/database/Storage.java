package seedu.studymate.database;

import seedu.studymate.habits.Habit;
import seedu.studymate.habits.HabitList;
import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.reminders.Reminder;
import seedu.studymate.reminders.ReminderList;
import seedu.studymate.tasks.Task;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.tasks.TaskList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Handles reading and writing tasks to the save file.
 */
public class Storage {
    private final String filePath;
    private final char delim = 0x1F; // for serialisation

    public Storage(String filePath) {
        assert filePath != null && !filePath.isEmpty() : "File path should not be null or empty";
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the save file into a TaskList.
     * If file not found, creates a new empty one.
     */
    public void load(TaskList taskList, ReminderList reminderList, HabitList habitList) throws StudyMateException {
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // ensure directory exists
                file.createNewFile();
                assert file.exists() : "File should exist after creation attempt";
            } catch (IOException e) {
                throw new StudyMateException("Error creating save file: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    parseAndAddLine(line, taskList, reminderList, habitList);
                    // skip line if invalid
                } catch (StudyMateException e) {
                    System.err.println("Skipping invalid line: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new StudyMateException("Error reading save file: " + e.getMessage());
        }
    }

    /**
     * Saves task or reminder to the save file.
     * 
     * @param tasks The list of tasks to save.
     * @param reminders The list of reminders to save.
     * @throws StudyMateException If an error occurs while writing to the file.
     */
    public void save(List<Task> tasks, List<Reminder> reminders, List<Habit> habits) throws StudyMateException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                bw.write(task.toSaveString());
                bw.newLine();
            }
            for (Reminder reminder : reminders) {
                bw.write(reminder.toSaveString());
                bw.newLine();
            }
            for (Habit habit : habits) {
                bw.write(habit.toSaveString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new StudyMateException("Error writing to save file: " + e.getMessage());
        }
    }

    /**
     * Parses a line from the save file and adds the corresponding Task to the given TaskList.
     * Supports ToDo and Deadline tasks. Sets the task's done status if indicated.
     *
     * @param line     The line from the save file representing a task.
     * @param taskList The TaskList to add the parsed task to.
     * @param reminderList The ReminderList to add parsed reminders to.
     * @throws StudyMateException If the line format is invalid.
     */
    private void parseAndAddLine(String line, TaskList taskList, ReminderList reminderList, HabitList habitList)
            throws StudyMateException {
        String[] parts = line.split(Character.toString(delim));

        /* Skip all the lines that have less than 3 parts. */
        if (parts.length < 3) {
            return;
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");

        switch (type) {
        case "T":
            
            taskList.addToDo(parts[2]);
            if (isDone) {
                taskList.getTask(taskList.getCount() - 1).setDone(true);
            }
            break;

        case "D":
            String[] rawdateTime = parts[3].split("T");
            DateTimeArg dateTimeArg;
            try {
                if (rawdateTime.length == 2 && !rawdateTime[1].trim().isEmpty()) {
                    dateTimeArg = new DateTimeArg(LocalDate.parse(rawdateTime[0].trim()),
                            LocalTime.parse(rawdateTime[1].trim()));
                } else if (!rawdateTime[0].trim().isEmpty()) {
                    dateTimeArg = new DateTimeArg(LocalDate.parse(rawdateTime[0].trim()));
                } else {
                    throw new StudyMateException("Error parsing deadline data!");
                }
            } catch (Exception e) {
                throw new StudyMateException("Error parsing deadline date/time: " + e.getMessage());
            }
            taskList.addDeadline(parts[2], dateTimeArg);
            if (isDone) {
                taskList.getTask(taskList.getCount() - 1).setDone(true);
            }
            break;

        case "E":
            if (parts.length < 5) {
                return;
            }
            String eventName = parts[2];
            String[] rawFromDateTime = parts[3].split("T");
            String[] rawToDateTime = parts[4].split("T");
            DateTimeArg fromDateTimeArg;
            DateTimeArg toDateTimeArg;

            try {
                // Parse from date
                if (rawFromDateTime.length == 2 && !rawFromDateTime[1].trim().isEmpty()) {
                    fromDateTimeArg = new DateTimeArg(LocalDate.parse(rawFromDateTime[0].trim()),
                            LocalTime.parse(rawFromDateTime[1].trim()));
                } else if (!rawFromDateTime[0].trim().isEmpty()) {
                    fromDateTimeArg = new DateTimeArg(LocalDate.parse(rawFromDateTime[0].trim()));
                } else {
                    throw new StudyMateException("Error parsing event from date!");
                }

                // Parse to date
                if (rawToDateTime.length == 2 && !rawToDateTime[1].trim().isEmpty()) {
                    toDateTimeArg = new DateTimeArg(LocalDate.parse(rawToDateTime[0].trim()),
                            LocalTime.parse(rawToDateTime[1].trim()));
                } else if (!rawToDateTime[0].trim().isEmpty()) {
                    toDateTimeArg = new DateTimeArg(LocalDate.parse(rawToDateTime[0].trim()));
                } else {
                    throw new StudyMateException("Error parsing event to date!");
                }
            } catch (Exception e) {
                throw new StudyMateException("Error parsing event date/time: " + e.getMessage());
            }

            taskList.addEvent(eventName, fromDateTimeArg, toDateTimeArg);
            if (isDone) {
                taskList.getTask(taskList.getCount() - 1).setDone(true);
            }
            break;

        case "R":
            if (parts.length < 5) {
                return;
            }
            boolean isRecurring = parts[1].equals("1");
            boolean isReminderDone = parts[2].equals("1");
            String reminderName = parts[3];
            String[] rawDateTime = parts[4].split("T");
            DateTimeArg reminderTime;

            try {
                if (rawDateTime.length == 2 && !rawDateTime[1].trim().isEmpty()) {
                    reminderTime = new DateTimeArg(LocalDate.parse(rawDateTime[0].trim()),
                            LocalTime.parse(rawDateTime[1].trim()));
                } else if (!rawDateTime[0].trim().isEmpty()) {
                    reminderTime = new DateTimeArg(LocalDate.parse(rawDateTime[0].trim()));
                } else {
                    throw new StudyMateException("Error parsing reminder data!");
                }
            } catch (Exception e) {
                throw new StudyMateException("Error parsing reminder date/time: " + e.getMessage());
            }

            if (isRecurring) {
                // Recurring reminder: parse interval from parts[5]
                if (parts.length < 6) {
                    throw new StudyMateException("Recurring reminder missing interval!");
                }
                Duration interval = Duration.parse(parts[5]);
                reminderList.addReminderRec(reminderName, reminderTime, interval);
            } else {
                // One-time reminder
                reminderList.addReminderOneTime(reminderName, reminderTime);
            }

            if (isReminderDone) {
                reminderList.getReminder(reminderList.getCount() - 1).setOnReminder(true);
            }
            break;

        case "H":
            if (parts.length < 5) {
                return;
            }
            String habitName = parts[1];
            String[] rawHabitDateTime = parts[2].split("T");
            DateTimeArg habitDeadline;

            try {
                if (rawHabitDateTime.length == 2 && !rawHabitDateTime[1].trim().isEmpty()) {
                    habitDeadline = new DateTimeArg(LocalDate.parse(rawHabitDateTime[0].trim()),
                            LocalTime.parse(rawHabitDateTime[1].trim()));
                } else if (!rawHabitDateTime[0].trim().isEmpty()) {
                    habitDeadline = new DateTimeArg(LocalDate.parse(rawHabitDateTime[0].trim()));
                } else {
                    throw new StudyMateException("Error parsing habit deadline!");
                }
            } catch (Exception e) {
                throw new StudyMateException("Error parsing habit date/time: " + e.getMessage());
            }

            Duration habitInterval = Duration.parse(parts[3]);
            int habitStreak = Integer.parseInt(parts[4]);

            habitList.addHabit(habitName, habitDeadline, habitInterval, habitStreak);
            break;

        default:
        // ignore invalid lines
        }
    }
}
