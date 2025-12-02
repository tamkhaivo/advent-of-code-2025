package day1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
    Problem Space; 
    Find the number of times the dial hits zero when the dial starts at 50;
    
    Part 2: Count EVERY time it hits 0, even during rotation.
*/
public class Day1Part2 {
    static String filePath = "day1/day1.txt";

    public static void main(String[] args) {
        try {
            long result = solve(filePath);
            System.out.println(result);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    public static long solve(String filePath) throws IOException {
        int dialPosition = 50;
        long count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int steps = Integer.parseInt(line.substring(1));
                char direction = line.charAt(0);

                for (int i = 0; i < steps; i++) {
                    if (direction == 'L') {
                        dialPosition--;
                        if (dialPosition < 0) {
                            dialPosition = 99;
                        }
                    } else {
                        dialPosition++;
                        if (dialPosition > 99) {
                            dialPosition = 0;
                        }
                    }

                    if (dialPosition == 0) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
