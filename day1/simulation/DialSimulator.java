package day1.simulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Optional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service to coordinate the dial simulation.
 * Uses the Builder pattern for configuration.
 */
public class DialSimulator {
    private static final Logger logger = LogManager.getLogger(DialSimulator.class);
    private final String filePath;
    private final int initialPosition;
    private final SimulationStrategy strategy;

    private DialSimulator(Builder builder) {
        this.filePath = builder.filePath;
        this.initialPosition = builder.initialPosition;
        this.strategy = builder.strategy;
    }

    /**
     * Runs the simulation.
     *
     * @return An Optional containing the count of zero crossings, or empty if an
     *         error occurred.
     */
    public Optional<Integer> run() {
        logger.info("Starting simulation...");
        logger.info("Reading file: {}", filePath);

        List<Integer> stepsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int steps = Integer.parseInt(line.substring(1));
                if (line.startsWith("L")) {
                    stepsList.add(-steps);
                } else {
                    stepsList.add(steps);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading file", e);
            return Optional.empty();
        }

        int[] allSteps = stepsList.stream().mapToInt(i -> i).toArray();
        logger.info("File read successfully. Total steps: {}", allSteps.length);

        return strategy.run(allSteps, initialPosition);
    }

    public static class Builder {
        private String filePath;
        private int initialPosition = 0;
        private SimulationStrategy strategy;

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder initialPosition(int initialPosition) {
            this.initialPosition = initialPosition;
            return this;
        }

        public Builder strategy(SimulationStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public DialSimulator build() {
            if (filePath == null) {
                throw new IllegalStateException("File path must be set.");
            }
            if (strategy == null) {
                throw new IllegalStateException("Strategy must be set.");
            }
            return new DialSimulator(this);
        }
    }
}
