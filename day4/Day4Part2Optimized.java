package day4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Day4Part2Optimized {
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
        if (grid == null || grid.isEmpty()) {
            return 0;
        }

        int ROWS = grid.size();
        int COLS = grid.get(0).length;
        int totalCells = ROWS * COLS;

        // Flatten the grid for better cache locality and easier indexing
        byte[] flatGrid = new byte[totalCells];
        for (int r = 0; r < ROWS; r++) {
            char[] row = grid.get(r);
            for (int c = 0; c < COLS; c++) {
                flatGrid[r * COLS + c] = (byte) row[c];
            }
        }

        Queue<Integer> queue = new ArrayDeque<>();
        int removableToiletPaperCount = 0;

        // Pre-calculate neighbor offsets
        int[] neighborOffsets = {
                -COLS - 1, -COLS, -COLS + 1,
                -1, 1,
                COLS - 1, COLS, COLS + 1
        };

        // Initial scan: Find all '@' cells that have < 4 neighbors
        for (int i = 0; i < totalCells; i++) {
            if (flatGrid[i] == '@') {
                if (countNeighbors(flatGrid, i, ROWS, COLS, neighborOffsets) < 4) {
                    queue.add(i);
                    // Mark as pending removal to avoid adding duplicates in initial scan?
                    // Actually, we can just process them.
                    // But to be safe against duplicates if we were adding from multiple sources,
                    // we could mark. Here, initial scan visits each once.
                }
            }
        }

        // Process the queue
        while (!queue.isEmpty()) {
            int index = queue.poll();

            // Check if already removed (could happen if added multiple times by neighbors)
            if (flatGrid[index] == '.') {
                continue;
            }

            // Remove the toilet paper
            flatGrid[index] = '.';
            removableToiletPaperCount++;

            // Check neighbors
            int row = index / COLS;
            int col = index % COLS;

            // Re-implementing neighbor check loop correctly for 1D array without padding:
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0)
                        continue;

                    int nRow = row + dr;
                    int nCol = col + dc;

                    if (nRow >= 0 && nRow < ROWS && nCol >= 0 && nCol < COLS) {
                        int nIndex = nRow * COLS + nCol;
                        if (flatGrid[nIndex] == '@') {
                            // If this neighbor now has < 4 neighbors, add to queue
                            if (countNeighbors(flatGrid, nIndex, ROWS, COLS, neighborOffsets) < 4) {
                                queue.add(nIndex);
                            }
                        }
                    }
                }
            }
        }

        return removableToiletPaperCount;
    }

    private static int countNeighbors(byte[] grid, int index, int ROWS, int COLS, int[] offsets) {
        int count = 0;
        int row = index / COLS;
        int col = index % COLS;

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0)
                    continue;

                int nRow = row + dr;
                int nCol = col + dc;

                if (nRow >= 0 && nRow < ROWS && nCol >= 0 && nCol < COLS) {
                    if (grid[nRow * COLS + nCol] == '@') {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
