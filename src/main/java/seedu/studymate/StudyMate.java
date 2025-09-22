package seedu.studymate;

import java.util.Scanner;

public class StudyMate {
    /**
     * Main entry-point for the java.studymate.StudyMate application.
     */
    public static void main(String[] args) {
        System.out.println("Hello from StudyMate");
        System.out.println("What is your name?");

        Scanner in = new Scanner(System.in);
        System.out.println("Hello " + in.nextLine());
    }
}
