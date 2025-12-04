package day4.tests;

import day4.Day4Part1;
import day4.Day4Part1Optimized;
import day4.Day4Part1AIOptimized;
import day4.Day4Part2;
import day4.Day4Part2Optimized;
import day4.Day4Part2AIOptimized;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day4Tests {

    @Test
    public void testExamplePart1() {
        String[] input = {
                "..@@.@@@@.",
                "@@@.@.@.@@",
                "@@@@@.@.@@",
                "@.@@@@..@.",
                "@@.@@@@.@@",
                ".@@@@@@@.@",
                ".@.@.@.@@@",
                "@.@@@.@@@@",
                ".@@@@@@@@.",
                "@.@.@@@.@."
        };

        List<char[]> grid = new ArrayList<>();
        for (String line : input) {
            grid.add(line.toCharArray());
        }

        int result = Day4Part1.solve(grid);
        assertEquals(13, result);
    }

    @Test
    public void testExamplePart2() {
        String[] input = {
                "..@@.@@@@.",
                "@@@.@.@.@@",
                "@@@@@.@.@@",
                "@.@@@@..@.",
                "@@.@@@@.@@",
                ".@@@@@@@.@",
                ".@.@.@.@@@",
                "@.@@@.@@@@",
                ".@@@@@@@@.",
                "@.@.@@@.@."
        };

        List<char[]> grid = new ArrayList<>();
        for (String line : input) {
            grid.add(line.toCharArray());
        }

        List<char[]> gridOptimized = new ArrayList<>();
        for (String line : input) {
            gridOptimized.add(line.toCharArray());
        }

        int result = Day4Part2.solve(grid);
        int resultOptimized = Day4Part2Optimized.solve(gridOptimized);

        assertEquals(result, resultOptimized);
    }

    @Test
    public void testOptimizedExample() {
        String input = "..@@.@@@@.\n" +
                "@@@.@.@.@@\n" +
                "@@@@@.@.@@\n" +
                "@.@@@@..@.\n" +
                "@@.@@@@.@@\n" +
                ".@@@@@@@.@\n" +
                ".@.@.@.@@@\n" +
                "@.@@@.@@@@\n" +
                ".@@@@@@@@.\n" +
                "@.@.@@@.@.";

        byte[] grid = input.getBytes();

        int result = Day4Part1Optimized.solve(grid);
        assertEquals(13, result);
    }

    @Test
    public void testAIOptimizedExample() throws Exception {
        String input = "..@@.@@@@.\n" +
                "@@@.@.@.@@\n" +
                "@@@@@.@.@@\n" +
                "@.@@@@..@.\n" +
                "@@.@@@@.@@\n" +
                ".@@@@@@@.@\n" +
                ".@.@.@.@@@\n" +
                "@.@@@.@@@@\n" +
                ".@@@@@@@@.\n" +
                "@.@.@@@.@.";

        Path tempFile = Files.createTempFile("day4test", ".txt");
        Files.write(tempFile, input.getBytes());

        try {
            int result = Day4Part2AIOptimized.solve(tempFile.toAbsolutePath().toString());

            List<char[]> grid = new ArrayList<>();
            for (String line : input.split("\n")) {
                grid.add(line.toCharArray());
            }
            int expected = Day4Part2.solve(grid);

            assertEquals(expected, result);
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void testAIOptimizedPart1Example() {
        String input = "..@@.@@@@.\n" +
                "@@@.@.@.@@\n" +
                "@@@@@.@.@@\n" +
                "@.@@@@..@.\n" +
                "@@.@@@@.@@\n" +
                ".@@@@@@@.@\n" +
                ".@.@.@.@@@\n" +
                "@.@@@.@@@@\n" +
                ".@@@@@@@@.\n" +
                "@.@.@@@.@.";

        byte[] grid = input.getBytes();

        long result = Day4Part1AIOptimized.solve(grid);
        assertEquals(13, result);
    }
}
