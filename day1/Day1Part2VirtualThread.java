package day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Day1Part2VirtualThread {

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
        // Oversubscribe chunks to leverage virtual threads
        int numChunks = Runtime.getRuntime().availableProcessors() * 4;
        if (lines.size() < numChunks) {
            numChunks = 1;
        }

        int chunkSize = (int) Math.ceil((double) lines.size() / numChunks);
        List<List<String>> chunks = new ArrayList<>();

        for (int i = 0; i < lines.size(); i += chunkSize) {
            chunks.add(lines.subList(i, Math.min(i + chunkSize, lines.size())));
        }

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        // Pass 1: Net displacement per chunk (Identical to Part 1 logic)
        List<Future<Long>> displacementFutures = new ArrayList<>();
        for (List<String> chunk : chunks) {
            displacementFutures.add(executor.submit(() -> calculateNetDisplacement(chunk)));
        }

        long[] chunkDisplacements = new long[chunks.size()];
        for (int i = 0; i < chunks.size(); i++) {
            chunkDisplacements[i] = displacementFutures.get(i).get();
        }

        // Pass 2: Prefix sum for start positions
        long[] startPositions = new long[chunks.size()];
        long currentPos = 50;
        for (int i = 0; i < chunks.size(); i++) {
            startPositions[i] = currentPos;
            currentPos = updatePosition(currentPos, chunkDisplacements[i]);
        }

        // Pass 3: Detailed simulation counting
        List<Future<Long>> countFutures = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            final long startPos = startPositions[i];
            final List<String> chunk = chunks.get(i);
            countFutures.add(executor.submit(() -> countZeroCrossings(chunk, startPos)));
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
            int steps = Integer.parseInt(line.substring(1));
            if (line.startsWith("L")) {
                displacement -= steps;
            } else {
                displacement += steps;
            }
        }
        return displacement;
    }

    private static long updatePosition(long currentPos, long displacement) {
        long newPos = (currentPos + displacement) % 100;
        if (newPos < 0)
            newPos += 100;
        return newPos;
    }

    private static long countZeroCrossings(List<String> chunk, long startPos) {
        long currentPos = startPos;
        long count = 0;

        for (String line : chunk) {
            int steps = Integer.parseInt(line.substring(1));
            char direction = line.charAt(0);

            // Simulate step-by-step
            for (int k = 0; k < steps; k++) {
                if (direction == 'L') {
                    currentPos--;
                    if (currentPos < 0)
                        currentPos = 99;
                } else {
                    currentPos++;
                    if (currentPos > 99)
                        currentPos = 0;
                }

                if (currentPos == 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
