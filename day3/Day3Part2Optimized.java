package day3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.Optional;

/**
 * Day 3 Part 2 Optimized solution using a Hybrid Strategy Pattern.
 * 
 * <p>
 * Employs the Strategy Pattern (GEMINI.md 3.2) to select the optimal processing
 * algorithm based on file size:
 * <ul>
 * <li><b>Sequential Strategy</b>: For small files
 * ({@code < PARALLEL_THRESHOLD}), uses
 * single-threaded MappedByteBuffer processing for minimal overhead.</li>
 * <li><b>Parallel Strategy</b>: For large files, splits the file into chunks
 * aligned
 * to newlines and processes them in parallel using CompletableFuture.</li>
 * </ul>
 * 
 * <p>
 * This approach maximizes performance for the current benchmark (~20KB) while
 * scaling
 * to GB-sized files without code changes.
 */
public class Day3Part2Optimized {

    private static final String FILE_PATH = "day3/day3.txt";
    private static final int TARGET_LENGTH = 12;

    /** Threshold in bytes. Files smaller than this use sequential processing. */
    private static final long PARALLEL_THRESHOLD = 1024 * 1024; // 1 MB

    public static void main(String[] args) {
        try {
            Optional<Long> result = solve(FILE_PATH);
            if (result.isPresent()) {
                System.out.println(result.get());
            } else {
                System.err.println("Calculation failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Solves the problem using the optimal strategy based on file size.
     *
     * @param filePath Path to the input file.
     * @return An Optional containing the sum of maximum joltages, or empty on
     *         error.
     */
    public static Optional<Long> solve(String filePath) {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
                FileChannel channel = file.getChannel()) {

            long fileSize = channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            if (fileSize < PARALLEL_THRESHOLD) {
                return Optional.of(solveSequential(buffer, 0, (int) fileSize));
            } else {
                return Optional.of(solveParallel(buffer, (int) fileSize));
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Sequential processing strategy for small files.
     * Processes a byte range and returns the sum of max joltages.
     */
    private static long solveSequential(MappedByteBuffer buffer, int start, int end) {
        long totalSum = 0;
        byte[] lineBuffer = new byte[128]; // Max expected line length
        int lineLen = 0;

        for (int i = start; i < end; i++) {
            byte b = buffer.get(i);
            if (b == '\n') {
                if (lineLen > 0) {
                    totalSum += findMaxJoltage(lineBuffer, lineLen);
                    lineLen = 0;
                }
            } else if (b != '\r') {
                lineBuffer[lineLen++] = b;
            }
        }
        // Handle last line if no trailing newline
        if (lineLen > 0) {
            totalSum += findMaxJoltage(lineBuffer, lineLen);
        }
        return totalSum;
    }

    /**
     * Parallel processing strategy for large files.
     * Splits the file into chunks aligned to newlines and processes in parallel.
     */
    private static long solveParallel(MappedByteBuffer buffer, int fileSize) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        int chunkSize = fileSize / numThreads;

        List<CompletableFuture<Long>> futures = new ArrayList<>(numThreads);

        int start = 0;
        for (int t = 0; t < numThreads; t++) {
            int end = (t == numThreads - 1) ? fileSize : start + chunkSize;

            // Align to next newline to avoid splitting lines
            while (end < fileSize && buffer.get(end) != '\n') {
                end++;
            }
            if (end < fileSize) {
                end++; // Include the newline character
            }

            final int chunkStart = start;
            final int chunkEnd = end;

            futures.add(CompletableFuture.supplyAsync(() -> {
                // Create a slice view for this thread's chunk
                return solveSequentialDirect(buffer, chunkStart, chunkEnd);
            }, ForkJoinPool.commonPool()));

            start = end;
        }

        return futures.stream()
                .mapToLong(CompletableFuture::join)
                .sum();
    }

    /**
     * Direct buffer access version for parallel chunks.
     * Avoids buffer position conflicts by using absolute get().
     */
    private static long solveSequentialDirect(MappedByteBuffer buffer, int start, int end) {
        long totalSum = 0;
        byte[] lineBuffer = new byte[128];
        int lineLen = 0;

        for (int i = start; i < end; i++) {
            byte b = buffer.get(i);
            if (b == '\n') {
                if (lineLen > 0) {
                    totalSum += findMaxJoltage(lineBuffer, lineLen);
                    lineLen = 0;
                }
            } else if (b != '\r') {
                lineBuffer[lineLen++] = b;
            }
        }
        if (lineLen > 0) {
            totalSum += findMaxJoltage(lineBuffer, lineLen);
        }
        return totalSum;
    }

    /**
     * Finds the maximum 12-digit joltage from a line using a greedy stack
     * algorithm.
     * 
     * <p>
     * Algorithm: Iterate through digits. If the current digit is larger than the
     * top of the stack, pop digits (up to the "drop budget") to maximize the
     * result.
     * 
     * @param lineBuffer Byte array containing digit characters ('0'-'9').
     * @param length     Number of valid bytes in the buffer.
     * @return The maximum 12-digit number as a long.
     */
    public static long findMaxJoltage(byte[] lineBuffer, int length) {
        int dropCount = length - TARGET_LENGTH;
        byte[] stack = new byte[length];
        int top = -1;

        for (int i = 0; i < length; i++) {
            byte digit = lineBuffer[i];
            while (dropCount > 0 && top >= 0 && stack[top] < digit) {
                top--;
                dropCount--;
            }
            stack[++top] = digit;
        }

        // Drop trailing digits if drop budget remains (e.g., descending sequence)
        while (dropCount > 0) {
            top--;
            dropCount--;
        }

        // Build result directly from the first TARGET_LENGTH bytes in stack
        long result = 0;
        for (int i = 0; i < TARGET_LENGTH; i++) {
            result = result * 10 + (stack[i] - '0');
        }
        return result;
    }
}
