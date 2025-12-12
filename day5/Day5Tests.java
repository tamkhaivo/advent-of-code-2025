package day5;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day5Tests {

    @Test
    public void testPart1VirtualThreadMatchesSequential() {
        // Capture standard output from Day5Part1.main
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            Day5Part1.main(new String[] {});
        } finally {
            System.setOut(originalOut);
        }

        long expected = Long.parseLong(outContent.toString().trim());
        long actual = Day5Part1VirtualThread.solve();

        assertEquals(expected, actual, "Virtual Thread solution for Part 1 should match sequential solution");
    }

    @Test
    public void testPart2VirtualThreadMatchesSequential() {
        // Capture standard output from Day5Part2.main
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            Day5Part2.main(new String[] {});
        } finally {
            System.setOut(originalOut);
        }

        // Output format: "Total unique ingredients: <number>"
        String output = outContent.toString().trim();
        long expected = Long.parseLong(output.substring(output.lastIndexOf(" ") + 1));

        long actual = Day5Part2VirtualThread.solve();

        assertEquals(expected, actual, "Virtual Thread solution for Part 2 should match sequential solution");
    }
}
