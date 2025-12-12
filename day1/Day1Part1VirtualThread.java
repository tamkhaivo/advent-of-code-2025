package day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Day1Part1VirtualThread {

    static String filePath = "day1/day1.txt";

    public static void main(String[] args) {
        try {
            long result = solve(filePath);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long solve(String filePath) throws IOException, InterruptedException, ExecutionException {
        List<String> lines = Files.readAllLines(Path.of(filePath));
        int numChunks = Runtime.getRuntime().availableProcessors() * 4; // Oversegment for virtual threads
        if (lines.size() < numChunks) {
            numChunks = 1;
        }

        int chunkSize = (int) Math.ceil((double) lines.size() / numChunks);
        List<List<String>> chunks = new ArrayList<>();

        for (int i = 0; i < lines.size(); i += chunkSize) {
            chunks.add(lines.subList(i, Math.min(i + chunkSize, lines.size())));
        }

        // Pass 1: Calculate net displacements for each chunk
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<Long>> displacementFutures = new ArrayList<>();

        for (List<String> chunk : chunks) {
            displacementFutures.add(executor.submit(() -> calculateNetDisplacement(chunk)));
        }

        long[] chunkDisplacements = new long[chunks.size()];
        for (int i = 0; i < chunks.size(); i++) {
            chunkDisplacements[i] = displacementFutures.get(i).get();
        }

        // Pass 2: Calculate starting positions (Prefix Sums)
        long[] startPositions = new long[chunks.size()];
        long currentPos = 50; // Initial start
        for (int i = 0; i < chunks.size(); i++) {
            startPositions[i] = currentPos;
            currentPos = updatePosition(currentPos, chunkDisplacements[i]);
        }

        // Pass 3: Count zero landings
        List<Future<Long>> countFutures = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            final int chunkIndex = i;
            final long startPos = startPositions[i];
            final List<String> chunk = chunks.get(i);

            countFutures.add(executor.submit(() -> countZeroLandings(chunk, startPos)));
        }

        long totalCount = 0;
        for (Future<Long> f : countFutures) {
            totalCount += f.get();
        }

        executor.shutdown();
        return totalCount;
    }

    private static long calculateNetDisplacement(List<String> chunk) {
        long displacement = 0;
        for (String line : chunk) {
            displacement += parseLine(line);
        }
        return displacement;
    }

    // Helper to normalize position to 0-99
    private static long updatePosition(long currentPos, long displacement) {
        long newPos = (currentPos + displacement) % 100;
        if (newPos < 0)
            newPos += 100;
        return newPos;
    }

    private static int parseLine(String line) {
        int steps = Integer.parseInt(line.substring(1));
        if (line.startsWith("L")) {
            return -steps;
        } else {
            return steps;
        }
    }

    private static long countZeroLandings(List<String> chunk, long startPos) {
        long currentPos = startPos;
        long count = 0;
        for (String line : chunk) {
            int displacement = parseLine(line);
            currentPos = updatePosition(currentPos, displacement);
            if (currentPos == 0) {
                count++;
            }
        }
        return count;
    }
}
