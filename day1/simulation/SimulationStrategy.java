package day1.simulation;

import java.util.Optional;

/**
 * Strategy interface for running the dial simulation.
 */
public interface SimulationStrategy {
    /**
     * Runs the simulation.
     *
     * @param steps           The array of steps.
     * @param initialPosition The initial position of the dial.
     * @return A Result containing the count of zero crossings.
     */
    Optional<Integer> run(int[] steps, int initialPosition);
}
