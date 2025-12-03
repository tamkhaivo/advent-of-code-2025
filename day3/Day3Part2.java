package day3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day3Part2 {
    private static final String FILE_PATH = "day3/day3.txt";

    public static void main(String[] args) {
        long maxPower = solve(FILE_PATH);
        System.out.println(maxPower);
    }

    public static long solve(String filePath) {
        long sum = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sum += findMaxJoltage(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return sum;
    }

    public static long findMaxJoltage(String line) {
        int targetLength = 12;
        int dropCount = line.length() - targetLength;
        StringBuilder stack = new StringBuilder();

        for (char digit : line.toCharArray()) {
            while (dropCount > 0 && stack.length() > 0 && stack.charAt(stack.length() - 1) < digit) {
                stack.deleteCharAt(stack.length() - 1);
                dropCount--;
            }
            stack.append(digit);
        }

        // drops (e.g., "54321" and we want 3 digits, we drop '2' and '1')
        while (dropCount > 0) {
            stack.deleteCharAt(stack.length() - 1);
            dropCount--;
        }

        return Long.parseLong(stack.toString());
    }
}
