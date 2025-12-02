package day1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
    Problem Space; 
    Find the number of times the dial hits zero when the dial starts at 50;

    Approach 1: Sequential Iteration O(n)

*/
public class Day1Part1 {
    static String filePath = "day1/day1.txt";
    static int dialPosition = 50;
    static int count = 0;

    public static void main(String[] args) {
        dialPosition = 50;
        count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int steps = Integer.parseInt(line.substring(1));
                if (line.startsWith("L")) {
                    dialPosition -= steps;
                } else {
                    dialPosition += steps;
                }
                dialPosition %= 100;
                if (dialPosition == 0) {
                    count++;
                }
            }
            System.out.println(count);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

    }
}
