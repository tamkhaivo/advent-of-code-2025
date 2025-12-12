package day5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Day5Part1VirtualThread {
    private static final String INPUT_FILE = "day5/day5.txt";

    public static void main(String[] args) {
        System.out.println(solve());
    }

    public static long solve() {
        List<List<Long>> bounds = new ArrayList<>();
        AtomicLong freshIngredients = new AtomicLong(0);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<String> linesToCheck = new ArrayList<>();

            // Read file sequentially first to separate bounds from numbers
            try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE))) {
                String line;
                boolean checkIngredients = false;
                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) {
                        checkIngredients = true;
                        continue;
                    }
                    if (!checkIngredients) {
                        String[] rowArray = line.split("-");
                        bounds.add(List.of(Long.parseLong(rowArray[0]), Long.parseLong(rowArray[1])));
                    }
                    if (checkIngredients) {
                        linesToCheck.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Process checks in parallel
            for (String line : linesToCheck) {
                executor.submit(() -> {
                    long currentNumber = Long.parseLong(line);
                    boolean found = false;
                    for (List<Long> bound : bounds) {
                        if (isBetween(bound.get(0), bound.get(1), currentNumber)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        freshIngredients.incrementAndGet();
                    }
                });
            }
        } // Executor auto-close waits for all tasks

        return freshIngredients.get();
    }

    public static boolean isBetween(long leftBounds, long rightBounds, long currentNumber) {
        return leftBounds <= currentNumber && currentNumber <= rightBounds;
    }
}
