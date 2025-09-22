package seedu.studymate;

import seedu.studymate.parser.Command;
import seedu.studymate.parser.Parser;

import java.util.Scanner;

public class StudyMate {
    /**
     * Main entry-point for the java.studymate.StudyMate application.
     */
    public static void main(String[] args) {
        Parser parser = new Parser();
        Scanner sc = new Scanner(System.in);
        System.out.println("Hello from StudyMate");
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Command cmd = parser.parse(line);
            switch (cmd.type) {
            case LIST -> System.out.println("listing");
            }
            System.out.println("Parser skeleton is working");
            break;
        }
    }
}
