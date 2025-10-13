package seedu.studymate.database;

import seedu.studymate.parser.DateTimeArg;
import seedu.studymate.tasks.Reminder;
import seedu.studymate.tasks.ReminderList;
import seedu.studymate.tasks.Task;
import seedu.studymate.exceptions.StudyMateException;
import seedu.studymate.tasks.TaskList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Handles reading and writing tasks to the save file.
 */
public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the save file into a TaskList.
     * If file not found, creates a new empty one.
     */
    public void load(TaskList taskList, ReminderList reminderList) throws StudyMateException {
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // ensure directory exists
                file.createNewFile();
            } catch (IOException e) {
                throw new StudyMateException("Error creating save file: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                parseAndAddLine(line, taskList, reminderList);
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
    public void save(List<Task> tasks, List<Reminder> reminders) throws StudyMateException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                bw.write(task.toSaveString());
                bw.newLine();
            }
            for (Reminder reminder : reminders) {
                bw.write(reminder.toSaveString());
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
    private void parseAndAddLine(String line, TaskList taskList, ReminderList reminderList) throws StudyMateException {
        String[] parts = line.split("\\|");

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
            if (rawdateTime[0].length() == 2 && !rawdateTime[1].equals(" ")) {
                dateTimeArg = new DateTimeArg(LocalDate.parse(rawdateTime[0]), LocalTime.parse(rawdateTime[1]));
            } else if (!rawdateTime[0].equals(" ")) {
                dateTimeArg = new DateTimeArg(LocalDate.parse(rawdateTime[0]));
            } else {
                throw new StudyMateException("Error parsing data!");
            }
            taskList.addDeadline(parts[2], dateTimeArg);
            if (isDone) {
                taskList.getTask(taskList.getCount() - 1).setDone(true);
            }
            break;
        case "R":
            if (parts.length < 4) {
                return;
            }
            String name = parts[2];
            String[] rawDateTime = parts[3].split("T");
            DateTimeArg reminderTime;
            if (rawDateTime.length == 2 && !rawDateTime[1].isBlank()) {
                reminderTime = new DateTimeArg(LocalDate.parse(rawDateTime[0]), LocalTime.parse(rawDateTime[1]));
            } else {
                reminderTime = new DateTimeArg(LocalDate.parse(rawDateTime[0]));
            }

            reminderList.addReminder(name, reminderTime);

            if (isDone) {
                reminderList.getReminder(reminderList.getCount() - 1).setReminded(true);
            }
            break;


        default:
        // ignore invalid lines
        }
    }

    /**
     * Saves all reminders to the file.
     * 
     * @param reminders The list of reminders that needs to be saved.
     * @throws StudyMateException If an error occurs while writing to the file.
     */
    public void saveReminders(List<Reminder> reminders) throws StudyMateException {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            for (Reminder reminder : reminders) {
                fw.write(reminder.toSaveString() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new StudyMateException("Error saving reminders: " + e.getMessage());
        }
    }

}

