package seedu.studymate.database;

import seedu.studymate.tasks.*;
import seedu.studymate.exceptions.StudyMateException;

import java.io.*;
import java.util.ArrayList;
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
    public List<Task> load() throws StudyMateException {
        List<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // ensure directory exists
                file.createNewFile();
            } catch (IOException e) {
                throw new StudyMateException("Error creating save file: " + e.getMessage());
            }
            return tasks; // empty list
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new StudyMateException("Error reading save file: " + e.getMessage());
        }

        return tasks;
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
     * Converts a saved text line into a Task object.
     */
    private Task parseTask(String line) {
        String[] parts = line.split("\\|");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");

        switch (type) {
        case "T":
            Task todo = new ToDo(parts[2]);
            todo.setDone(isDone);
            return todo;
        case "D":
            Task deadline = new Deadline(parts[2], parts[3]);
            deadline.setDone(isDone);
            return deadline;
        default:
            return null;
        }
    }
}
