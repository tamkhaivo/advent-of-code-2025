package day3;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Day3Part2Optimized {
    private static final String FILE_PATH = "day3/day3.txt";
    private static final int TARGET_LENGTH = 12;

    public static void main(String[] args) {
        try {
            long maxPower = solve(FILE_PATH).get();
            System.out.println(maxPower);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<Long> solve(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                return reader.lines()
                        .parallel()
                        .mapToLong(Day3Part2Optimized::findMaxJoltage)
                        .sum();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static long findMaxJoltage(String line) {
        int n = line.length();
        int dropCount = n - TARGET_LENGTH;
        char[] stack = new char[n];
        int top = -1;

        for (int i = 0; i < n; i++) {
            char digit = line.charAt(i);
            while (dropCount > 0 && top >= 0 && stack[top] < digit) {
                top--;
                dropCount--;
            }
            if (top < TARGET_LENGTH - 1 + dropCount) {
                stack[++top] = digit;
            } else {
                stack[++top] = digit;
            }
        }

        top = -1;
        dropCount = n - TARGET_LENGTH;

        for (int i = 0; i < n; i++) {
            char digit = line.charAt(i);
            while (dropCount > 0 && top >= 0 && stack[top] < digit) {
                top--;
                dropCount--;
            }
            stack[++top] = digit;
        }

        long result = 0;
        for (int i = 0; i < TARGET_LENGTH; i++) {
            result = result * 10 + (stack[i] - '0');
        }
        return result;
    }
}
