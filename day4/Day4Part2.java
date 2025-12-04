package day4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day4Part2 {
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
        int removableToiletPaperCount = 0;
        int ROWS = grid.size();
        int COLS = grid.get(0).length;

        char[][] notRemovableToiletPaper = grid.toArray(new char[ROWS][COLS]);
        char[][] notRemovableToiletPaperCopy = grid.toArray(new char[ROWS][COLS]);
        boolean hasToiletPaperRemoved = true;
        while (hasToiletPaperRemoved) {
            hasToiletPaperRemoved = false;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (notRemovableToiletPaperCopy[row][col] == '@') {
                        if (countNeighbors(notRemovableToiletPaperCopy, row, col) < 4) {
                            removableToiletPaperCount++;
                            notRemovableToiletPaper[row][col] = '.';
                            hasToiletPaperRemoved = true;
                        }
                    }
                }
            }
            notRemovableToiletPaperCopy = notRemovableToiletPaper.clone();
        }
        return removableToiletPaperCount;
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
