package day2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class Day2Part2VirtualThread {
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
        // Similar to Part 1, we need to ensuring unique counting of invalid IDs.
        var invalidIDs = java.util.concurrent.ConcurrentHashMap.<Long>newKeySet();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();

            for (long[] range : lines) {
                futures.add(executor.submit(() -> {
                    long start = range[0];
                    long end = range[1];
                    for (long currentID = start; currentID <= end; currentID++) {
                        if (isInvalidID(currentID)) {
                            if (invalidIDs.add(currentID)) {
                                count.addAndGet(currentID);
                            }
                        }
                    }
                }));
            }

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

    private static boolean isInvalidID(long ID) {
        String sID = Long.toString(ID);
        List<String> numSlices = new ArrayList<>();
        for (int sliceLength = 1; sliceLength <= sID.length() / 2; sliceLength++) {
            for (int currentIndex = 0; currentIndex < sID.length(); currentIndex += sliceLength) {
                numSlices.add(sID.substring(currentIndex, Math.min(currentIndex + sliceLength, sID.length())));
            }
            boolean flag = true;
            for (int currentSlice = 0; currentSlice < numSlices.size() - 1; currentSlice++) {
                if (!numSlices.get(currentSlice).equals(numSlices.get(currentSlice + 1))) {
                    flag = false;
                }
            }
            if (flag) {
                return true;
            }
            numSlices.clear();
        }
        return false;
    }
}
