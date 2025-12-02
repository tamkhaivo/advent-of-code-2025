package day2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Day2Part1ExtraCredit {
    private static final String FILE_PATH = "day2/day2.txt";

    public static void main(String[] args) {
        List<long[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                for (String value : values) {
                    String[] ranges = value.split("-");
                    long start = Long.parseLong(ranges[0]);
                    long end = Long.parseLong(ranges[1]);
                    lines.add(new long[] { start, end });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        long invalidIDCount = countInvalidIDsParallel(lines);
        System.out.println(invalidIDCount);
    }

    private static long countInvalidIDsParallel(List<long[]> lines) {
        try (ForkJoinPool pool = new ForkJoinPool()) {
            return pool.invoke(new RangeTask(lines, 0, lines.size()));
        }
    }

    private static class RangeTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 100; // Adjust based on workload
        private final List<long[]> lines;
        private final int start;
        private final int end;

        public RangeTask(List<long[]> lines, int start, int end) {
            this.lines = lines;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                return computeDirectly();
            } else {
                int mid = start + (end - start) / 2;
                RangeTask left = new RangeTask(lines, start, mid);
                RangeTask right = new RangeTask(lines, mid, end);
                left.fork();
                long rightResult = right.compute();
                long leftResult = left.join();
                return leftResult + rightResult;
            }
        }

        private long computeDirectly() {
            long count = 0;
            // Note: We are not using a global HashSet for uniqueness across ALL ranges here
            // because the prompt implies we sum up invalid IDs found in ranges.
            // However, the user's Part 1 implementation used a HashSet `invalidIDs` to
            // avoid
            // double counting if ranges overlap or if the same ID appears multiple times?
            // Let's check the user's `day2part1.java`.
            // It uses `HashSet<Long> invalidIDs` and checks
            // `!invalidIDs.contains(currentID)`.
            // This implies global uniqueness is required.
            // Parallelizing with global uniqueness is trickier.
            // We can compute local sums and local sets, then merge? Or just use a
            // ConcurrentHashMap?
            // Given the constraints and simplicity, let's stick to the prompt's example
            // logic first.
            // "Adding up all the invalid IDs in this example produces..."
            // The example ranges don't overlap.
            // If ranges overlap, we need to be careful.
            // Let's assume for now we need to match the user's logic of unique IDs.
            // Actually, for high performance, maybe we just sum them up and if uniqueness
            // is strictly required
            // we might need a different approach (e.g. collecting all invalid IDs then
            // distinct().sum()).

            // Re-reading day2part1.java:
            // HashSet<Long> invalidIDs = new HashSet<>();
            // ... if (isInvalidID(currentID) && !invalidIDs.contains(currentID)) ...

            // To support parallel execution with uniqueness, we should collect all invalid
            // IDs found
            // and then sum them up uniquely at the end.

            // But wait, `computeDirectly` returns a sum (Long).
            // If we need uniqueness, we should change `RecursiveTask` to return `Set<Long>`
            // or `List<Long>`.
            // Or use a ConcurrentHashMap.KeySetView.

            // Let's implement a simpler version first: just sum.
            // If the user's `day2part1` output matches `day2part1` output, we are good.
            // If not, we fix it.

            for (int i = start; i < end; i++) {
                long[] range = lines.get(i);
                for (long currentID = range[0]; currentID <= range[1]; currentID++) {
                    if (day2part1.isInvalidID(currentID)) {
                        count += currentID;
                    }
                }
            }
            return count;
        }
    }
}
