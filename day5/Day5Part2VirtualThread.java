package day5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Day5Part2VirtualThread {
    private static final String INPUT_FILE = "day5/day5.txt";

    public static void main(String[] args) {
        System.out.println("Total unique ingredients: " + solve());
    }

    public static long solve() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<Long> result = executor.submit(() -> {
                // We will store bounds as simple arrays: {start, end}
                List<long[]> bounds = new ArrayList<>();

                try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE))) {
                    String line;
                    boolean checkIngredients = false;

                    while ((line = br.readLine()) != null) {
                        if (line.isEmpty()) {
                            checkIngredients = true;
                            continue;
                        }

                        // Assuming the first block contains the ranges
                        if (!checkIngredients) {
                            String[] rowArray = line.split("-");
                            long start = Long.parseLong(rowArray[0]);
                            long end = Long.parseLong(rowArray[1]);
                            bounds.add(new long[] { start, end });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 1. Sort intervals by start time
                bounds.sort(Comparator.comparingLong(a -> a[0]));

                if (bounds.isEmpty()) {
                    return 0L;
                }

                // 2. Merge Intervals
                List<long[]> merged = new ArrayList<>();
                long[] current = bounds.get(0);
                merged.add(current);

                for (long[] next : bounds) {
                    // Check if intervals overlap or are adjacent (e.g. 1-5 and 6-10)
                    // If next start is before (or exactly 1 after) current end
                    if (next[0] <= current[1] + 1) {
                        // Merge them: extend current end to the max of both ends
                        current[1] = Math.max(current[1], next[1]);
                    } else {
                        // No overlap, add as a new distinct interval
                        current = next;
                        merged.add(current);
                    }
                }

                // 3. Calculate total size
                long totalCount = 0;
                for (long[] interval : merged) {
                    // Formula: end - start + 1 (inclusive)
                    totalCount += (interval[1] - interval[0] + 1);
                }

                return totalCount;
            });

            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
