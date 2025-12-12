package day1.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import day1.Day1Part1VirtualThread;
import day1.Day1Part2VirtualThread;
import day1.Day1Part1;
import day1.Day1Part2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Day1VirtualThreadTests {

    @Test
    public void testPart1VirtualThreadExample() throws Exception {
        // L50, R100
        // Start: 50
        // 1. 50 - 50 = 0 (Count 1)
        // 2. 0 + 100 = 100 (0) (Count 2)
        Path tempFile = Files.createTempFile("day1part1vt_test", ".txt");
        String content = "L50\nR100";
        Files.write(tempFile, content.getBytes());

        try {
            long result = Day1Part1VirtualThread.solve(tempFile.toString());
            assertEquals(2, result);
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void testPart2VirtualThreadExample() throws Exception {
        // L50
        // Start 50 -> 0. Distance 50. Hits 0 at end.
        // R100
        // Start 0 -> 100 (0). Distance 100. Hits 0 at end.
        // Total so far: 2.

        // Let's use a more complex example where it crosses 0 multiple times.
        // Start: 50
        // L200 -> Goes backwards 200. -150 net. 50 -> -150 = 50.
        // Crosses 0 twice (at -50 and -150 if linear).

        // Use the simple case first:
        Path tempFile = Files.createTempFile("day1part2vt_test", ".txt");
        String content = "L50\nR100";
        Files.write(tempFile, content.getBytes());

        try {
            long result = Day1Part2VirtualThread.solve(tempFile.toString());
            assertEquals(2, result);
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void testPart1Consistency() throws Exception {
        String filePath = "day1/day1.txt";
        // Check if file exists before running
        if (!Files.exists(Path.of(filePath))) {
            return; // Skip if file not found (e.g. CI)
        }

        long legacyResult = Day1Part1.solve(filePath);
        long vtResult = Day1Part1VirtualThread.solve(filePath);

        assertEquals(legacyResult, vtResult, "Virtual Thread Part 1 solution should match legacy");
    }

    @Test
    public void testPart2Consistency() throws Exception {
        String filePath = "day1/day1.txt";
        // Check if file exists before running
        if (!Files.exists(Path.of(filePath))) {
            return; // Skip if file not found
        }

        long legacyResult = Day1Part2.solve(filePath);
        long vtResult = Day1Part2VirtualThread.solve(filePath);

        assertEquals(legacyResult, vtResult, "Virtual Thread Part 2 solution should match legacy");
    }
}
