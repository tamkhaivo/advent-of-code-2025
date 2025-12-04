package day4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day4Part1 {
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
        int removableToiletPaper = 0;
        int ROWS = grid.size();
        if (ROWS == 0)
            return 0;
        int COLS = grid.get(0).length;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid.get(row)[col] == '@') {
                    if (countNeighbors(grid, row, col) < 4) {
                        removableToiletPaper++;
                    }
                }
            }
        }
        return removableToiletPaper;
    }

    private static int countNeighbors(List<char[]> grid, int row, int col) {
        int count = 0;
        int ROWS = grid.size();
        int COLS = grid.get(0).length;

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
                if (grid.get(newRow)[newCol] == '@') {
                    count++;
                }
            }
        }
        return count;
    }
}
