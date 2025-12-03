package day1.tests;

import java.util.Optional;
import day1.simulation.VectorizedSimulationStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class Day1Tests {

    @Test
    public void testDay1Part2Optimized() throws IOException {
        // Create a temporary file with known content
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("day1part2_test", ".txt");
        String content = "L50\nR100";
        java.nio.file.Files.write(tempFile, content.getBytes());

        try {
            long result = day1.Day1Part2Optimized.solve(tempFile.toString());
            // Start 50.
            // L50 -> 0 (Count 1)
            // R100 -> 0 (Count 2)
            assertEquals(2, result);
        } finally {
            java.nio.file.Files.delete(tempFile);
        }
    }

    @Test
    public void testVectorizedStrategySimple() {
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy();
        // Steps: +10, -20, +30
        // Start: 50
        // 1. 50 + 10 = 60
        // 2. 60 - 20 = 40
        // 3. 40 + 30 = 70
        // No zero crossings.
        int[] steps = { 10, -20, 30 };
        Optional<Integer> result = strategy.run(steps, 50);
        assertTrue(result.isPresent());
        assertEquals(0, result.get());
    }

    @Test
    public void testVectorizedStrategyWithZeroCrossing() {
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy();
        // Steps: -50, +100
        // Start: 50
        // 1. 50 - 50 = 0 (CROSSING)
        // 2. 0 + 100 = 0 (CROSSING)

        int[] steps = { -50, 100 };
        Optional<Integer> result = strategy.run(steps, 50);
        assertTrue(result.isPresent());
        assertEquals(2, result.get());
    }

    @Test
    public void testVectorizedStrategyExactBoundary() {
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy();
        // Start: 99
        // Step: +1 -> 0 (CROSSING)
        int[] steps = { 1 };
        Optional<Integer> result = strategy.run(steps, 99);
        assertTrue(result.isPresent());
        assertEquals(1, result.get());
    }

    @Test
    public void testVectorizedStrategyNegativeBoundary() {
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy();
        // Start: 0
        // Step: -1 -> 99
        // Step: +1 -> 0 (CROSSING)
        int[] steps = { -1, 1 };
        Optional<Integer> result = strategy.run(steps, 0);
        assertTrue(result.isPresent());
        assertEquals(1, result.get());
    }

    @Test
    public void testVectorizedStrategyLargeSteps() {
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy();
        // Start: 50
        // Step: +200 -> 50 (2 full rotations, hits 0 twice)
        int[] steps = { 200 };
        Optional<Integer> result = strategy.run(steps, 50);
        assertTrue(result.isPresent());
        assertEquals(2, result.get());
    }

    @Test
    public void testVectorizedStrategyLargeNegativeSteps() {
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy();
        // Start: 50
        // Step: -200 -> 50 (2 full rotations backwards, hits 0 twice)
        int[] steps = { -200 };
        Optional<Integer> result = strategy.run(steps, 50);
        assertTrue(result.isPresent());
        assertEquals(2, result.get());
    }

    @Test
    public void testPart2Example() {
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy();
        // Example from Part 2 Extra Credit:
        // Start: 50
        // L68, L30, R48, L5, R60, L55, L1, L99, R14, L82
        int[] steps = { -68, -30, 48, -5, 60, -55, -1, -99, 14, -82 };
        Optional<Integer> result = strategy.run(steps, 50);
        assertTrue(result.isPresent());
        assertEquals(6, result.get());
    }

    @Test
    public void testVectorizedStrategyPart1Simple() {
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy(
                VectorizedSimulationStrategy.SimulationType.PART1_LAND_ON_ZERO);
        // Start: 50
        // 1. 50 + 50 = 100 (0) -> Count 1
        // 2. 0 + 100 = 100 (0) -> Count 2
        // 3. 0 + 200 = 200 (0) -> Count 3
        // 4. 0 - 50 = -50 (50) -> Count 3
        int[] steps = { 50, 100, 200, -50 };
        Optional<Integer> result = strategy.run(steps, 50);
        assertTrue(result.isPresent());
        assertEquals(3, result.get());
    }

    @Test
    public void testVectorizedStrategyPart1LargeStep() {
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy(
                VectorizedSimulationStrategy.SimulationType.PART1_LAND_ON_ZERO);
        // Start: 50
        // Step: +200 -> 250 % 100 = 50.
        // Lands on 50. Count 0.
        // (Part 2 would count 2).
        int[] steps = { 200 };
        Optional<Integer> result = strategy.run(steps, 50);
        assertTrue(result.isPresent());
        assertEquals(0, result.get());
    }

    @Test
    public void testOptimizedSolution() {
        // L50, R100
        // Start: 50
        // 1. 50 - 50 = 0 (Count 1)
        // 2. 0 + 100 = 100 (0) (Count 2)
        String input = "L50\nR100";
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        long result = day1.Day1Part1Optimized.solve(buffer, input.length());
        assertEquals(2, result);
    }

    @Test
    public void testPart1Consistency() throws IOException {
        String filePath = "day1/day1.txt";
        long legacyResult = day1.Day1Part1.solve(filePath);
        long optimizedResult = day1.Day1Part1Optimized.solve(filePath);
        Optional<Integer> extraCreditResult = day1.Day1Part1ExtraCredit.solve(filePath);

        assertTrue(extraCreditResult.isPresent(), "Extra Credit solution should return a result");

        assertEquals(legacyResult, optimizedResult, "Legacy and Optimized solutions should match");
        assertEquals(legacyResult, extraCreditResult.get().longValue(),
                "Legacy and Extra Credit solutions should match");
    }

    @Test
    public void testPart1OptimizedStreams() throws Exception {
        // L50, R100
        // Start: 50
        // 1. 50 - 50 = 0 (Count 1)
        // 2. 0 + 100 = 100 (0) (Count 2)
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("day1part1streams_test", ".txt");
        String content = "L50\nR100";
        java.nio.file.Files.write(tempFile, content.getBytes());

        try {
            long result = day1.Day1Part1OptimizedStreams.solve(tempFile.toString()).get();
            assertEquals(2, result);
        } finally {
            java.nio.file.Files.delete(tempFile);
        }
    }

    @Test
    public void testPart1OptimizedStreamsConsistency() throws Exception {
        String filePath = "day1/day1.txt";
        long legacyResult = day1.Day1Part1.solve(filePath);
        long streamsResult = day1.Day1Part1OptimizedStreams.solve(filePath).get();

        assertEquals(legacyResult, streamsResult, "Legacy and Optimized Streams solutions should match");
    }

    @Test
    public void testPart2Consistency() throws IOException {
        String filePath = "day1/day1.txt";
        long legacyResult = day1.Day1Part2.solve(filePath);
        long optimizedResult = day1.Day1Part2Optimized.solve(filePath);
        Optional<Integer> extraCreditResult = day1.Day1Part2ExtraCredit.solve(filePath);

        assertTrue(extraCreditResult.isPresent(), "Extra Credit solution should return a result");

        assertEquals(legacyResult, optimizedResult, "Legacy and Optimized solutions should match");
        assertEquals(legacyResult, extraCreditResult.get().longValue(),
                "Legacy and Extra Credit solutions should match");
    }

}
