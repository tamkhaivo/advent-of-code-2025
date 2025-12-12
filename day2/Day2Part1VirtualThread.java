package day2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class Day2Part1VirtualThread {
    private static final String FILE_PATH = "day2/day2.txt";

    public static void main(String[] args) {
        long invalidIDCount = solve(FILE_PATH);
        System.out.println(invalidIDCount);
    }

    public static long solve(String filePath) {
        List<long[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
            return 0;
        }

        return countInvalidIDsVirtualThreads(lines);
    }

    private static long countInvalidIDsVirtualThreads(List<long[]> lines) {
        AtomicLong count = new AtomicLong(0);

        // We use a concurrent set or careful handling if we wanted uniqueness across
        // ALL ranges,
        // but the original logic seemingly counts IDs per range or globally?
        // Original logic: "HashSet<Long> invalidIDs = new HashSet<>();"
        // It accumulates `count += currentID` and adds to `invalidIDs`.
        // If an ID is already in `invalidIDs`, it is NOT added again to count.
        // This implies we need a global thread-safe set of visited IDs.

        // However, checking the original logic carefully:
        // for (long[] range : lines) { ... for (currentID ...) { if (... &&
        // !invalidIDs.contains(currentID)) { count += currentID;
        // invalidIDs.add(currentID); } } }
        // Yes, it is a global set of unique invalid IDs found across ALL ranges.

        // Since we are parallelizing, we need a thread-safe set.
        // ConcurrentHashMap.newKeySet() is appropriate.
        var invalidIDs = java.util.concurrent.ConcurrentHashMap.<Long>newKeySet();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();

            for (long[] range : lines) {
                futures.add(executor.submit(() -> {
                    long start = range[0];
                    long end = range[1];
                    for (long currentID = start; currentID <= end; currentID++) {
                        if (isInvalidID(currentID)) {
                            // We need to atomically check-and-add to ensure we don't double count
                            // if multiple threads find the same ID roughly at the same time.
                            // Set.add() returns true if the set did not already contain the element.
                            if (invalidIDs.add(currentID)) {
                                count.addAndGet(currentID);
                            }
                        }
                    }
                }));
            }

            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return count.get();
    }

    public static boolean isInvalidID(long ID) {
        String s = Long.toString(ID);
        if (s.length() % 2 != 0) {
            return false;
        }
        String firstHalf = s.substring(0, s.length() / 2);
        String secondHalf = s.substring(s.length() / 2);
        return firstHalf.equals(secondHalf);
    }
}
