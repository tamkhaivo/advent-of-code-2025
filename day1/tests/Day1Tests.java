package day1.tests;

import java.util.Optional;
import day1.simulation.VectorizedSimulationStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Day1Tests {

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

}
