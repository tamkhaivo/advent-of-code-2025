package day3;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Day 3 Part 1 Extra Credit solution using ThreadPools.
 */
public class Day3Part1ExtraCredit {

    private static final String FILE_PATH = "day3/day3.txt";
    private static final Logger logger = LogManager.getLogger(Day3Part1ExtraCredit.class);
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        logger.info("Application started");
        Optional<Long> result = solve(FILE_PATH);
        if (result.isPresent()) {
            logger.info("Calculation completed successfully");
            System.out.println(result.get());
        } else {
            logger.error("Calculation failed");
        }
    }

    public static Optional<Long> solve(String filePath) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<Long>> futures = new ArrayList<>();
        long totalSum = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                futures.add(executor.submit(new LineProcessor(line)));
            }

            for (Future<Long> future : futures) {
                try {
                    totalSum += future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error processing line task", e);
                    return Optional.empty();
                }
            }
        } catch (IOException e) {
            logger.error("Error reading file: " + filePath, e);
            return Optional.empty();
        } finally {
            executor.shutdown();
        }

        return Optional.of(totalSum);
    }

    /**
     * Task to process a single line and calculate the max value.
     */
    private static class LineProcessor implements Callable<Long> {
        private final String line;

        public LineProcessor(String line) {
            this.line = line;
        }

        @Override
        public Long call() {
            char[] values = line.toCharArray();
            int leftNumberIdx = 0;
            int rightNumberIdx = 1;
            long maxValue = 0;

            for (int numIdx = 0; numIdx < values.length - 1; numIdx++) {
                if (values[numIdx] - '0' > values[leftNumberIdx] - '0') {
                    leftNumberIdx = numIdx;
                }
                rightNumberIdx = numIdx + 1;
                long currentValue = (long) (values[leftNumberIdx] - '0') * 10 + (values[rightNumberIdx] - '0');
                maxValue = Math.max(maxValue, currentValue);
            }
            return maxValue;
        }
    }
}
