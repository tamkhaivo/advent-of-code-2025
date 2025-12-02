package day1;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class BenchmarkRunner {

    private static final int WARMUP_ITERATIONS = 5;
    private static final int MEASUREMENT_ITERATIONS = 20;

    public static void main(String[] args) {
        System.out.println("Starting Day 1 Benchmarks...");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream nullOut = new ByteArrayOutputStream();
        PrintStream nullPrintStream = new PrintStream(nullOut);

        try {
            runBenchmark("Day 1 Part 1", () -> Day1Part1.main(new String[] {}), nullPrintStream);
            runBenchmark("Day 1 Part 1 Extra Credit", () -> Day1Part1ExtraCredit.main(new String[] {}),
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

        // Measurement
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

        System.out.printf("%-25s | Avg: %8.3f ms | Min: %8.3f ms | Max: %8.3f ms%n",
                name, avgTimeMs, minTimeMs, maxTimeMs);
        System.gc();
    }
}
