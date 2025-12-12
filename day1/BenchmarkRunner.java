package day1;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class BenchmarkRunner {

    private static final int WARMUP_ITERATIONS = 100;
    private static final int MEASUREMENT_ITERATIONS = 1000;

    public static void main(String[] args) {
        System.out.println("Starting Day 1 Benchmarks...");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream nullOut = new ByteArrayOutputStream();
        PrintStream nullPrintStream = new PrintStream(nullOut);

        try {
            runBenchmark("Day 1 Part 1", () -> Day1Part1.main(new String[] {}), nullPrintStream);
            runBenchmark("Day 1 Part 1 Extra Credit", () -> Day1Part1ExtraCredit.main(new String[] {}),
                    nullPrintStream);
            runBenchmark("Day 1 Part 1 Optimized", () -> Day1Part1Optimized.main(new String[] {}),
                    nullPrintStream);
            runBenchmark("Day 1 Part 1 Optimized Streams", () -> Day1Part1OptimizedStreams.main(new String[] {}),
                    nullPrintStream);
            runBenchmark("Day 1 Part 1 Virtual Thread",
                    () -> Day1Part1VirtualThread.main(new String[] {}),
                    nullPrintStream);
            runBenchmark("Day 1 Part 2", () -> Day1Part2.main(new String[] {}), nullPrintStream);
            runBenchmark("Day 1 Part 2 Extra Credit", () -> Day1Part2ExtraCredit.main(new String[] {}),
                    nullPrintStream);
            runBenchmark("Day 1 Part 2 Optimized", () -> Day1Part2Optimized.main(new String[] {}),
                    nullPrintStream);
            runBenchmark("Day 1 Part 2 Virtual Thread",
                    () -> Day1Part2VirtualThread.main(new String[] {}),
                    nullPrintStream);

        } finally {
            System.setOut(originalOut);
        }
    }

    private static void runBenchmark(String name, Runnable task, PrintStream nullPrintStream) {
        PrintStream originalOut = System.out;

        // Warmup
        System.setOut(nullPrintStream);
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            task.run();
        }
        System.setOut(originalOut);

        // Time Measurement
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;

        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            System.setOut(nullPrintStream);
            long start = System.nanoTime();
            task.run();
            long end = System.nanoTime();
            System.setOut(originalOut);

            long duration = end - start;
            totalTime += duration;
            minTime = Math.min(minTime, duration);
            maxTime = Math.max(maxTime, duration);
        }

        double avgTimeMs = (totalTime / (double) MEASUREMENT_ITERATIONS) / 1_000_000.0;
        double minTimeMs = minTime / 1_000_000.0;
        double maxTimeMs = maxTime / 1_000_000.0;
        double totalTimeMs = totalTime / 1_000_000.0;

        // Memory Measurement
        long maxMemory = 0;
        int memoryIterations = 5; // Reduced iterations for memory to save time

        for (int i = 0; i < memoryIterations; i++) {
            System.gc();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.setOut(nullPrintStream);
            long startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            task.run();
            long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.setOut(originalOut);

            long used = Math.max(0, endMem - startMem);
            maxMemory = Math.max(maxMemory, used);
        }

        double maxMemoryMB = maxMemory / (1024.0 * 1024.0);
        int cores = Runtime.getRuntime().availableProcessors();

        System.out.printf(
                "%-25s | Total: %8.3f ms | Avg: %8.3f ms | Min: %8.3f ms | Max: %8.3f ms | Max Mem: %6.2f MB | Cores: %d%n",
                name, totalTimeMs, avgTimeMs, minTimeMs, maxTimeMs, maxMemoryMB, cores);
        System.gc();
    }
}
