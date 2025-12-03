package day3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day3Part1 {
    private static final String FILE_PATH = "day3/day3.txt";

    public static void main(String[] args) {
        long maxPower = solve(FILE_PATH);
        System.out.println(maxPower);
    }

    public static long solve(String filePath) {

        int leftNumberIdx = 0, rightNumberIdx = 1;
        // first highest digit and second highest digit
        long sum = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                char[] values = line.toCharArray();
                int maxValue = 0;
                for (int numIdx = 0; numIdx < values.length - 1; numIdx++) {
                    if (values[numIdx] - '0' > values[leftNumberIdx] - '0') {
                        leftNumberIdx = numIdx;
                    }
                    rightNumberIdx = numIdx + 1;
                    maxValue = Math.max(maxValue, (values[leftNumberIdx] - '0') * 10 + (values[rightNumberIdx] - '0'));
                }
                System.out.println(maxValue + " " + leftNumberIdx + " " + rightNumberIdx);
                sum += maxValue;
                leftNumberIdx = 0;
                rightNumberIdx = 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        return sum;
    }
}
