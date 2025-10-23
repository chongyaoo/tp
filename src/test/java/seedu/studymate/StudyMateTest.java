package seedu.studymate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudyMateTest {
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    // Utility to normalize line endings for robust assertions
    private String normalizeLines(String text) {
        return text.replace("\r\n", "\n").replace("\r", "\n").trim();
    }

    @BeforeEach
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testSendWelcomeMessage() throws Exception {
        Method method = StudyMate.class.getDeclaredMethod("sendWelcomeMessage");
        method.setAccessible(true);
        method.invoke(null);
        String output = normalizeLines(outContent.toString());
        String expected = normalizeLines(
                "____________________________________________________________\n"
                        + "Hello from StudyMate!\n"
                        + "____________________________________________________________"
        );
        assertEquals(expected, output);
    }

    @Test
    public void testSendExitMessage() throws Exception {
        Method method = StudyMate.class.getDeclaredMethod("sendExitMessage");
        method.setAccessible(true);
        method.invoke(null);
        String output = normalizeLines(outContent.toString());
        String expected = normalizeLines(
                "____________________________________________________________\n"
                        + "Bye. Hope to see you again soon!\n"
                        + "____________________________________________________________"
        );
        assertEquals(expected, output);
    }

    @Test
    public void testReadInputTrimmed() throws Exception {
        String inputString = "   hello world   \n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(inputString.getBytes());
        Scanner scanner = new Scanner(testIn);

        Method method = StudyMate.class.getDeclaredMethod("readInput", Scanner.class);
        method.setAccessible(true);
        String result = (String) method.invoke(null, scanner);

        assertEquals("hello world", result);
        scanner.close();
    }

    @Test
    public void testMainWithByeInput() throws Exception {
        String simulatedInput = "bye\n";
        InputStream originalIn = System.in;
        PrintStream originalSysOut = System.out;
        ByteArrayInputStream inContent = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream sysOutContent = new ByteArrayOutputStream();
        System.setIn(inContent);
        System.setOut(new PrintStream(sysOutContent));
        try {
            StudyMate.main(new String[]{});
            String output = normalizeLines(sysOutContent.toString());
            assertTrue(output.contains("Bye. Hope to see you again soon!"));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalSysOut);
        }
    }
}
