package day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class Day4Part1Optimized {
    final static String INPUT_FILE = "day4/day4.txt";

    public static void main(String[] args) {
        try {
            byte[] grid = Files.readAllBytes(Path.of(INPUT_FILE));
            System.out.println("Removable toilet paper: " + solve(grid));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int solve(byte[] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        // Determine dimensions
        int width = 0;
        while (width < grid.length && grid[width] != '\n' && grid[width] != '\r') {
            width++;
        }

        if (width == 0)
            return 0;

        int stride = width;
        if (width < grid.length && grid[width] == '\r')
            stride++;
        if (width < stride && stride < grid.length && grid[stride] == '\n')
            stride++;

        // If stride didn't advance (single line file without newline at end?), handle
        // it
        if (stride == width && width < grid.length)
            stride++; // Should not happen if we found \r or \n

        // Adjust stride if we are at EOF
        if (stride == 0)
            stride = grid.length;

        final int COLS = width;
        final int STRIDE = stride;
        final int ROWS = (grid.length + STRIDE - 1) / STRIDE; // Ceiling division, but safer to just use length

        LongAdder removableToiletPaper = new LongAdder();

        IntStream.range(0, ROWS).parallel().forEach(row -> {
            int rowOffset = row * STRIDE;
            // Ensure we don't go out of bounds if the last line is incomplete or empty
            if (rowOffset >= grid.length)
                return;

            for (int col = 0; col < COLS; col++) {
                int index = rowOffset + col;
                if (index >= grid.length)
                    break;

                if (grid[index] == '@') {
                    if (countNeighbors(grid, row, col, ROWS, COLS, STRIDE) < 4) {
                        removableToiletPaper.increment();
                    }
                }
            }
        });

        return removableToiletPaper.intValue();
    }

    private static int countNeighbors(byte[] grid, int row, int col, int ROWS, int COLS, int STRIDE) {
        int count = 0;

        // 8 directions
        // Top-Left, Top, Top-Right
        // Left, Right
        // Bottom-Left, Bottom, Bottom-Right

        // Optimization: Unroll loop and check bounds explicitly or use safe access

        // Top Row
        if (row > 0) {
            int topRowOffset = (row - 1) * STRIDE;
            if (col > 0 && grid[topRowOffset + col - 1] == '@')
                count++;
            if (grid[topRowOffset + col] == '@')
                count++;
            if (col < COLS - 1 && grid[topRowOffset + col + 1] == '@')
                count++;
        }

        // Middle Row
        int currentRowOffset = row * STRIDE;
        if (col > 0 && grid[currentRowOffset + col - 1] == '@')
            count++;
        if (col < COLS - 1 && grid[currentRowOffset + col + 1] == '@')
            count++;

        // Bottom Row
        if (row < ROWS - 1) {
            int bottomRowOffset = (row + 1) * STRIDE;
            // Check if bottom row is within valid data range (handling last line potential
            // issues)
            if (bottomRowOffset < grid.length) {
                if (col > 0 && bottomRowOffset + col - 1 < grid.length && grid[bottomRowOffset + col - 1] == '@')
                    count++;
                if (bottomRowOffset + col < grid.length && grid[bottomRowOffset + col] == '@')
                    count++;
                if (col < COLS - 1 && bottomRowOffset + col + 1 < grid.length && grid[bottomRowOffset + col + 1] == '@')
                    count++;
            }
        }

        return count;
    }
}
