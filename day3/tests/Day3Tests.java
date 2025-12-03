package day3.tests;

import day3.Day3Part1;
import day3.Day3Part2;
import day3.Day3Part1Optimized;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Day3Tests {

    @Test
    public void testFindMaxJoltage() {
        assertEquals(987654321111L, Day3Part2.findMaxJoltage("987654321111111"));
        assertEquals(811111111119L, Day3Part2.findMaxJoltage("811111111111119"));
        assertEquals(434234234278L, Day3Part2.findMaxJoltage("234234234234278"));
        assertEquals(888911112111L, Day3Part2.findMaxJoltage("818181911112111"));
    }

    @Test
    public void testPart1Example() throws IOException {
        // Create a temporary file with example content
        Path tempFile = Files.createTempFile("day3part1_test", ".txt");
        // Based on the logic in Day3Part1, it seems to look for max 2-digit number
        // formed by a digit and its neighbor?
        // Let's just put some dummy data that would produce a predictable result.
        // Logic: max( (leftDigit * 10) + rightDigit )
        // "123" -> 12, 23 -> max 23.
        String content = "123\n456";
        Files.write(tempFile, content.getBytes());

        try {
            long result = Day3Part1.solve(tempFile.toString());
            // Line 1: 123 -> max 23
            // Line 2: 456 -> max 56
            // Sum = 23 + 56 = 79
            // Wait, let's trace the code in Day3Part1.java
            // values = ['1', '2', '3']
            // numIdx=0: values[0]('1') > values[left=0]('1') -> False. right=1. max =
            // max(0, 12) = 12.
            // numIdx=1: values[1]('2') > values[left=0]('1') -> True. left=1. right=2. max
            // = max(12, 23) = 23.
            // Line 1 result: 23.

            // values = ['4', '5', '6']
            // numIdx=0: '4' > '4' -> False. right=1. max=45.
            // numIdx=1: '5' > '4' -> True. left=1. right=2. max=max(45, 56) = 56.
            // Line 2 result: 56.

            // Total = 79.
            assertEquals(79, result);
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void testPart1Optimized() throws IOException {
        Path tempFile = Files.createTempFile("day3part1_opt_test", ".txt");
        String content = "123\n456";
        Files.write(tempFile, content.getBytes());

        try {
            long result = Day3Part1Optimized.solve(tempFile.toString());
            assertEquals(79, result);
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void testPart2Optimized() throws IOException {
        Path tempFile = Files.createTempFile("day3part2_test", ".txt");
        String content = "987654321111111\n811111111111119";
        Files.write(tempFile, content.getBytes());

        try {
            // Line 1: 987654321111111 -> 987654321111
            // Line 2: 811111111111119 -> 811111111119
            // Sum: 1798765432230
            long result = day3.Day3Part2Optimized.solve(tempFile.toString()).get();
            assertEquals(1798765432230L, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Files.delete(tempFile);
        }
    }
}
