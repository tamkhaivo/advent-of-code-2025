package day1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Optional;
import day1.simulation.DialSimulator;
import day1.simulation.VectorizedSimulationStrategy;

/**
 * Main entry point for the Day 1 Part 2 Extra Credit solution.
 */
public class Day1Part2ExtraCredit {
    static final String FILE_PATH = "day1/day1.txt";
    static final int INITIAL_DIAL_POSITION = 50;
    private static final Logger logger = LogManager.getLogger(Day1Part2ExtraCredit.class);

    public static void main(String[] args) {
        logger.info("Application started");
        Optional<Integer> result = solve(FILE_PATH);
        if (result.isPresent()) {
            logger.info("Simulation completed successfully");
            System.out.println(result.get());
        } else {
            logger.error("Simulation failed. See previous logs for details");
        }
    }

    public static Optional<Integer> solve(String filePath) {
        DialSimulator simulator = new DialSimulator.Builder()
                .filePath(filePath)
                .initialPosition(INITIAL_DIAL_POSITION)
                .strategy(
                        new VectorizedSimulationStrategy(VectorizedSimulationStrategy.SimulationType.PART2_CROSS_ZERO))
                .build();

        return simulator.run();
    }
}
