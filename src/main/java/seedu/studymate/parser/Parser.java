package seedu.studymate.parser;

public class Parser {
    public Command parse(String line) throws IllegalArgumentException {
        if (line.isEmpty()) {
            throw new IllegalArgumentException("Invalid argument");
        }
        String clean_line = line.replaceAll("\\s+", " ");
        String verb = clean_line.split(" ")[0];
        // stub for now
        switch (verb) {
        default:
            return new Command(CommandType.LIST);
        }
    }
}
