package day2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Solves Day 2 Part 1 using an optimized generative approach.
 */
public class Day2Part1Optimized {

    private static final Logger logger = LogManager.getLogger(Day2Part1Optimized.class);
    private static final String FILE_PATH = "day2/day2.txt";

    public static void main(String[] args) {
        logger.info("Starting Day 2 Part 1 Optimized calculation.");

        Optional<List<Range>> rangesResult = readRanges(FILE_PATH);

        if (rangesResult.isEmpty()) {
            logger.error("Failed to read ranges from file: {}", FILE_PATH);
            System.exit(1);
        }

        List<Range> ranges = rangesResult.get();
        if (ranges.isEmpty()) {
            logger.warn("No ranges found.");
            System.out.println(0);
            return;
        }

        long totalInvalidIDs = calculateTotalInvalidIDs(ranges);
        System.out.println(totalInvalidIDs);
        logger.info("Calculation completed. Result: {}", totalInvalidIDs);
    }

    /**
     * Calculates the sum of all invalid IDs within the given ranges.
     * Uses a generative approach: generates potential invalid IDs and checks if
     * they fall in the ranges.
     *
     * @param ranges The list of ranges to check against.
     * @return The sum of invalid IDs.
     */
    private static long calculateTotalInvalidIDs(List<Range> ranges) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (Range range : ranges) {
            if (range.start < min)
                min = range.start;
            if (range.end > max)
                max = range.end;
        }

        if (min > max)
            return 0; // Should not happen if ranges is not empty

        logger.debug("Generating invalid IDs between {} and {}", min, max);

        InvalidIDGenerator generator = new InvalidIDGenerator.Builder()
                .withMin(min)
                .withMax(max)
                .build();

        AtomicLong totalSum = new AtomicLong(0);

        // Use forEach to avoid stream overhead
        generator.forEach(id -> {
            for (Range range : ranges) {
                if (range.contains(id)) {
                    totalSum.addAndGet(id);
                    break;
                }
            }
        });

        return totalSum.get();
    }

    /**
     * Reads ranges from the input file.
     *
     * @param filePath The path to the file.
     * @return An Optional containing the list of ranges, or empty if an error
     *         occurred.
     */
    private static Optional<List<Range>> readRanges(String filePath) {
        List<Range> ranges = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                for (String part : parts) {
                    try {
                        String[] bounds = part.split("-");
                        if (bounds.length == 2) {
                            long start = Long.parseLong(bounds[0]);
                            long end = Long.parseLong(bounds[1]);
                            ranges.add(new Range(start, end));
                        }
                    } catch (NumberFormatException e) {
                        logger.warn("Skipping invalid range format: {}", part);
                    }
                }
            }
            return Optional.of(ranges);
        } catch (IOException e) {
            logger.error("IOException reading file: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Value Object representing a numeric range.
     */
    public record Range(long start, long end) {
        public boolean contains(long value) {
            return value >= start && value <= end;
        }
    }

    /**
     * Domain Service for generating invalid IDs.
     * An invalid ID is a number formed by repeating a sequence of digits twice
     * (e.g., 1212, 55).
     */
    public static class InvalidIDGenerator {
        private final long min;
        private final long max;

        private InvalidIDGenerator(long min, long max) {
            this.min = min;
            this.max = max;
        }

        /**
         * Executes the given action for each generated invalid ID.
         * This avoids Stream overhead and boxing.
         * 
         * @param action The action to perform on each ID.
         */
        public void forEach(LongConsumer action) {
            int maxDigits = String.valueOf(max).length();

            for (int L = 1; L <= maxDigits / 2; L++) {
                long startHalf = (long) Math.pow(10, L - 1);
                long endHalf = (long) Math.pow(10, L) - 1;
                long multiplier = (long) Math.pow(10, L) + 1;

                for (long half = startHalf; half <= endHalf; half++) {
                    long id = half * multiplier;
                    if (id >= min && id <= max) {
                        action.accept(id);
                    }
                }
            }
        }

        /**
         * Generates a stream of all invalid IDs within the configured [min, max]
         * bounds.
         * Useful for testing.
         *
         * @return A LongStream of invalid IDs.
         */
        public LongStream generate() {
            LongStream.Builder builder = LongStream.builder();
            forEach(builder::add);
            return builder.build();
        }

        public static class Builder {
            private long min = 0;
            private long max = Long.MAX_VALUE;

            public Builder withMin(long min) {
                this.min = min;
                return this;
            }

            public Builder withMax(long max) {
                this.max = max;
                return this;
            }

            public InvalidIDGenerator build() {
                return new InvalidIDGenerator(min, max);
            }
        }
    }

}
