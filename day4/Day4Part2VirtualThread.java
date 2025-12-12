package day4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Day4Part2VirtualThread {
    final static String INPUT_FILE = "day4/day4.txt";

    public static void main(String[] args) {
        List<char[]> toiletPaperGrid = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                char[] rowArray = line.toCharArray();
                toiletPaperGrid.add(rowArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Removable toilet paper: " + solve(toiletPaperGrid));
    }

    public static int solve(List<char[]> grid) {
        AtomicInteger removableToiletPaperCount = new AtomicInteger(0);
        int ROWS = grid.size();
        if (ROWS == 0)
            return 0;
        int COLS = grid.get(0).length;

        char[][] notRemovableToiletPaper = grid.toArray(new char[ROWS][COLS]);
        // We need an initial copy.
        // In the original, they use 'grid' to populate 'notRemovableToiletPaper',
        // and also 'notRemovableToiletPaperCopy'.
        // Let's create the copy.
        char[][] notRemovableToiletPaperCopy = new char[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            notRemovableToiletPaperCopy[i] = grid.get(i).clone();
        }

        AtomicBoolean hasToiletPaperRemoved = new AtomicBoolean(true);

        while (hasToiletPaperRemoved.get()) {
            hasToiletPaperRemoved.set(false);

            // Need final reference for lambda
            final char[][] currentCopy = notRemovableToiletPaperCopy;

            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int row = 0; row < ROWS; row++) {
                    final int r = row;
                    executor.submit(() -> {
                        for (int col = 0; col < COLS; col++) {
                            if (currentCopy[r][col] == '@') {
                                if (countNeighbors(currentCopy, r, col) < 4) {
                                    removableToiletPaperCount.incrementAndGet();
                                    notRemovableToiletPaper[r][col] = '.';
                                    hasToiletPaperRemoved.set(true);
                                }
                            }
                        }
                    });
                }
            } // waits for all threads to finish

            // Update copy for next iteration
            // We can do this in parallel too if we want, but array clone is fast enough or
            // we can use System.arraycopy
            // Or just swap/clone.
            // Original code: notRemovableToiletPaperCopy = notRemovableToiletPaper.clone();
            // Note: 2D array clone is shallow copy of rows? No, grid.toArray gave us new
            // arrays.
            // But doing `clone()` on 2D array only clones the outer array (references to
            // rows).
            // We need deep copy if we are modifying rows.
            // Wait, `notRemovableToiletPaper` is modified in place.
            // We need `notRemovableToiletPaperCopy` to be a snapshot of
            // `notRemovableToiletPaper` BEFORE modifications of the *next* round?
            // No, the logic is: read from `Copy`, write to `Original`.
            // Then make `Copy` look like `Original`.

            // Deep copy needed.
            for (int i = 0; i < ROWS; i++) {
                notRemovableToiletPaperCopy[i] = notRemovableToiletPaper[i].clone();
            }
        }
        return removableToiletPaperCount.get();
    }

    private static int countNeighbors(char[][] grid, int row, int col) {
        int count = 0;
        int ROWS = grid.length;
        int COLS = grid[0].length;

        // 8 directions
        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
                if (grid[newRow][newCol] == '@') {
                    count++;
                }
            }
        }
        return count;
    }
}
