package day2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Solves Day 2 Part 1 using a FixedThreadPool for concurrent processing.
 */
public class Day2Part1ExtraCredit {

    private static final Logger logger = LogManager.getLogger(Day2Part1ExtraCredit.class);
    private static final String FILE_PATH = "day2/day2.txt";

    public static void main(String[] args) {
        logger.info("Starting Day 2 Part 1 Extra Credit calculation.");
        Optional<List<Range>> linesResult = readLines(FILE_PATH);

        if (linesResult.isEmpty()) {
            logger.error("Failed to read lines from file: {}", FILE_PATH);
            return;
        }

        List<Range> lines = linesResult.get();
        if (lines.isEmpty()) {
            logger.warn("No lines found in file or file is empty.");
            System.out.println("0");
            return;
        }

        long invalidIDCount = countInvalidIDsConcurrent(lines);
        System.out.println(invalidIDCount);

        logger.info("Calculation completed. Result: {}", invalidIDCount);
    }

    /**
     * Reads lines from the input file and parses them into start/end ranges.
     *
     * @param filePath The path to the input file.
     * @return An Optional containing a list of Range objects on success, or empty
     *         on failure.
     */
    private static Optional<List<Range>> readLines(String filePath) {
        List<Range> lines = new ArrayList<>();
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
                        lines.add(new Range(start, end));
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
     * Counts the sum of invalid IDs using a thread pool.
     *
     * @param lines The list of ranges to process.
     * @return The sum of invalid IDs.
     */
    private static long countInvalidIDsConcurrent(List<Range> lines) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        logger.debug("Using {} threads for concurrent processing.", numThreads);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Long>> futures = new ArrayList<>();

        // Determine chunk size to balance load
        int totalRanges = lines.size();
        int chunkSize = Math.max(1, totalRanges / (numThreads * 4));

        for (int currentIdx = 0; currentIdx < totalRanges; currentIdx += chunkSize) {
            int end = Math.min(currentIdx + chunkSize, totalRanges);
            List<Range> chunk = lines.subList(currentIdx, end);
            futures.add(executor.submit(new RangeProcessor(chunk)));
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
        private final List<Range> ranges;

        public RangeProcessor(List<Range> ranges) {
            this.ranges = ranges;
        }

        @Override
        public Long call() {
            long localSum = 0;
            for (Range range : ranges) {
                for (long currentID = range.start(); currentID <= range.end(); currentID++) {
                    if (isInvalidID(currentID)) {
                        localSum += currentID;
                    }
                }
            }
            return localSum;
        }
    }

    /**
     * Value Object representing a numeric range.
     */
    private record Range(long start, long end) {
    }

    /**
     * Checks if an ID is invalid according to Part 1 rules.
     * Rule: Even length and first half equals second half.
     * 
     * @param ID The ID to check.
     * @return True if the ID is invalid, false otherwise.
     */
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
