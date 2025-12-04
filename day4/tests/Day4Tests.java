package day4.tests;

import day4.Day4Part1;
import java.util.ArrayList;
import java.util.List;

public class Day4Tests {
    public static void main(String[] args) {
        testExample();
    }

    private static void testExample() {
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
        int expected = 13;

        if (result == expected) {
            System.out.println("Test Passed! Result: " + result);
        } else {
            System.err.println("Test Failed. Expected: " + expected + ", Got: " + result);
            System.exit(1);
        }
    }
}
