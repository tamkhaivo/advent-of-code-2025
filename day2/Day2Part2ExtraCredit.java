package day2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Solves Day 2 Part 2 using a FixedThreadPool for concurrent processing.
 * 
 * <p>
 * This implementation reads ranges from a file, checks for "invalid" IDs within
 * those ranges, and calculates the sum of all unique invalid IDs. It uses a
 * thread pool to parallelize the search and a ConcurrentHashMap to ensure IDs
 * are counted only once.
 * </p>
 */
public class Day2Part2ExtraCredit {

    private static final Logger logger = LogManager.getLogger(Day2Part2ExtraCredit.class);
    private static final String FILE_PATH = "day2/day2.txt";

    public static void main(String[] args) {
        logger.info("Starting Day 2 Part 2 Extra Credit calculation.");
        long invalidIDCount = solve(FILE_PATH);
        System.out.println(invalidIDCount);
        logger.info("Calculation completed. Result: {}", invalidIDCount);
    }

    public static long solve(String filePath) {
        Optional<List<long[]>> linesResult = readLines(filePath);

        if (linesResult.isEmpty()) {
            logger.error("Failed to read lines from file: {}", filePath);
            return 0;
        }

        List<long[]> lines = linesResult.get();
        if (lines.isEmpty()) {
            logger.warn("No lines found in file or file is empty.");
            return 0;
        }

        return countInvalidIDsConcurrent(lines);
    }

    /**
     * Reads lines from the input file and parses them into start/end ranges.
     *
     * @param filePath The path to the input file.
     * @return An Optional containing a list of long arrays {start, end} on success,
     *         or empty on failure.
     */
    private static Optional<List<long[]>> readLines(String filePath) {
        List<long[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                for (String value : values) {
                    try {
                        String[] ranges = value.split("-");
                        if (ranges.length != 2) {
                            logger.warn("Invalid range format: {}", value);
                            continue;
                        }
                        long start = Long.parseLong(ranges[0]);
                        long end = Long.parseLong(ranges[1]);
                        lines.add(new long[] { start, end });
                    } catch (NumberFormatException e) {
                        logger.warn("Skipping invalid number in range: {}", value, e);
                    }
                }
            }
            return Optional.of(lines);
        } catch (IOException e) {
            logger.error("IOException reading file: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Counts the sum of unique invalid IDs using a thread pool.
     *
     * @param lines The list of ranges to process.
     * @return The sum of unique invalid IDs.
     */
    private static long countInvalidIDsConcurrent(List<long[]> lines) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        logger.debug("Using {} threads for concurrent processing.", numThreads);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        Set<Long> uniqueInvalidIds = ConcurrentHashMap.newKeySet();
        List<Future<Long>> futures = new ArrayList<>();

        // Determine chunk size to balance load
        int totalRanges = lines.size();
        int chunkSize = Math.max(1, totalRanges / (numThreads * 4));

        for (int i = 0; i < totalRanges; i += chunkSize) {
            int end = Math.min(i + chunkSize, totalRanges);
            List<long[]> chunk = lines.subList(i, end);
            futures.add(executor.submit(new RangeProcessor(chunk, uniqueInvalidIds)));
        }

        long totalSum = 0;
        try {
            for (Future<Long> future : futures) {
                totalSum += future.get();
            }
        } catch (InterruptedException e) {
            logger.error("Thread interrupted during execution.", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.error("Execution exception in thread pool.", e);
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        return totalSum;
    }

    /**
     * Task to process a chunk of ranges.
     */
    private static class RangeProcessor implements Callable<Long> {
        private final List<long[]> ranges;
        private final Set<Long> uniqueInvalidIds;

        public RangeProcessor(List<long[]> ranges, Set<Long> uniqueInvalidIds) {
            this.ranges = ranges;
            this.uniqueInvalidIds = uniqueInvalidIds;
        }

        @Override
        public Long call() {
            long localSum = 0;
            for (long[] range : ranges) {
                long start = range[0];
                long end = range[1];
                for (long currentID = start; currentID <= end; currentID++) {
                    if (isInvalidID(currentID)) {
                        // add returns true if the set did not already contain the element
                        if (uniqueInvalidIds.add(currentID)) {
                            localSum += currentID;
                        }
                    }
                }
            }
            return localSum;
        }
    }

    /**
     * Checks if an ID is invalid according to the Part 2 rules.
     * 
     * @param ID The ID to check.
     * @return True if the ID is invalid, false otherwise.
     */
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
