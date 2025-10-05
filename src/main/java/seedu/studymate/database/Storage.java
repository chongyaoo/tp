package seedu.studymate.database;

import seedu.studymate.parser.DateTimeArg;
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
    public void load(TaskList taskList) throws StudyMateException {
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
                parseAndAddTask(line, taskList);
            }
        } catch (IOException e) {
            throw new StudyMateException("Error reading save file: " + e.getMessage());
        }
    }

    /**
     * Saves all tasks to the save file.
     */
    public void save(List<Task> tasks) throws StudyMateException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                bw.write(task.toSaveString());
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
     */
    private void parseAndAddTask(String line, TaskList taskList) throws StudyMateException {
        String[] parts = line.split("\\|");
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
            if (!rawdateTime[0].equals(" ") && !rawdateTime[1].equals(" ")) {
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

        default:
        // ignore invalid lines
        }
    }
}

