package day1.simulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Optional;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simulation strategy that uses the Vector API and a thread pool to optimize
 * performance. It uses a prefix sum approach to determine the start position of
 * each step, and then uses SIMD operations to count crossings for multiple
 * steps in parallel.
 */
public class VectorizedSimulationStrategy implements SimulationStrategy {

    public enum SimulationType {
        PART1_LAND_ON_ZERO,
        PART2_CROSS_ZERO
    }

    private static final Logger logger = LogManager.getLogger(VectorizedSimulationStrategy.class);
    private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
    private final SimulationType type;

    public VectorizedSimulationStrategy() {
        this(SimulationType.PART2_CROSS_ZERO);
    }

    public VectorizedSimulationStrategy(SimulationType type) {
        this.type = type;
    }

    @Override
    public Optional<Integer> run(int[] steps, int initialPosition) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try {
            int totalSize = steps.length;
            int chunkSize = (int) Math.ceil((double) totalSize / numThreads);
            List<Chunk> chunks = new ArrayList<>();

            for (int i = 0; i < totalSize; i += chunkSize) {
                int end = Math.min(i + chunkSize, totalSize);
                chunks.add(new Chunk(steps, i, end));
            }

            // Phase 1: Calculate net displacement per chunk
            List<Future<Integer>> phase1Futures = new ArrayList<>();
            for (Chunk chunk : chunks) {
                phase1Futures.add(executor.submit(new NetDisplacementTask(chunk)));
            }

            int[] startPositions = new int[chunks.size()];
            startPositions[0] = initialPosition;

            // Wait for Phase 1 and calculate start positions
            for (int i = 0; i < chunks.size() - 1; i++) {
                int netChange = phase1Futures.get(i).get();
                startPositions[i + 1] = (startPositions[i] + netChange) % 100;
            }

            // Phase 2: Count zeroes
            List<Future<Integer>> phase2Futures = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++) {
                ZeroCountTask task;
                if (type == SimulationType.PART1_LAND_ON_ZERO) {
                    task = new Part1ZeroCountTask(chunks.get(i), startPositions[i]);
                } else {
                    task = new Part2ZeroCountTask(chunks.get(i), startPositions[i]);
                }
                phase2Futures.add(executor.submit(task));
            }

            int totalZeroes = 0;
            for (Future<Integer> future : phase2Futures) {
                totalZeroes += future.get();
            }

            return Optional.of(totalZeroes);

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Simulation failed", e);
            return Optional.empty();
        } finally {
            executor.shutdown();
        }
    }

    static class Chunk {
        final int[] data;
        final int start;
        final int end;

        Chunk(int[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }
    }

    static class NetDisplacementTask implements Callable<Integer> {
        private final Chunk chunk;

        public NetDisplacementTask(Chunk chunk) {
            this.chunk = chunk;
        }

        @Override
        public Integer call() {
            int i = chunk.start;
            int sum = 0;
            // Vectorized loop
            for (; i < chunk.end - SPECIES.length(); i += SPECIES.length()) {
                IntVector v = IntVector.fromArray(SPECIES, chunk.data, i);
                sum += v.reduceLanes(VectorOperators.ADD);
            }
            // Tail loop
            for (; i < chunk.end; i++) {
                sum += chunk.data[i];
            }
            return sum % 100;
        }
    }

    abstract static class ZeroCountTask implements Callable<Integer> {
        protected final Chunk chunk;
        protected final int startPosition;

        public ZeroCountTask(Chunk chunk, int startPosition) {
            this.chunk = chunk;
            this.startPosition = startPosition;
        }

        @Override
        public Integer call() {
            // 1. Compute prefix sums sequentially
            int len = chunk.end - chunk.start;
            int[] prefixSums = new int[len];
            int currentSum = 0;
            for (int i = 0; i < len; i++) {
                currentSum += chunk.data[chunk.start + i];
                prefixSums[i] = currentSum;
            }
            return doCount(prefixSums, len);
        }

        protected abstract int doCount(int[] prefixSums, int len);
    }

    static class Part1ZeroCountTask extends ZeroCountTask {
        public Part1ZeroCountTask(Chunk chunk, int startPosition) {
            super(chunk, startPosition);
        }

        @Override
        protected int doCount(int[] prefixSums, int len) {
            int count = 0;
            int i = 0;

            // Vectorized Part 1: Check if (startPosition + prefixSum) % 100 == 0
            for (; i < len - SPECIES.length(); i += SPECIES.length()) {
                IntVector vPrefix = IntVector.fromArray(SPECIES, prefixSums, i);
                IntVector vCurr = vPrefix.add(startPosition);

                // v % 100 = v - (v / 100) * 100
                IntVector vDiv = vCurr.div(100);
                IntVector vRem = vCurr.sub(vDiv.mul(100));

                VectorMask<Integer> mask = vRem.eq(0);
                count += mask.trueCount();
            }

            // Tail loop for Part 1
            for (; i < len; i++) {
                if ((startPosition + prefixSums[i]) % 100 == 0) {
                    count++;
                }
            }
            return count;
        }
    }

    static class Part2ZeroCountTask extends ZeroCountTask {
        public Part2ZeroCountTask(Chunk chunk, int startPosition) {
            super(chunk, startPosition);
        }

        @Override
        protected int doCount(int[] prefixSums, int len) {
            int count = 0;
            int i = 0;

            // Vectorized Part 2: Check for zero crossings
            for (; i < len - SPECIES.length(); i += SPECIES.length()) {
                // Load prefix sums and steps
                IntVector vPrefix = IntVector.fromArray(SPECIES, prefixSums, i);
                IntVector vStep = IntVector.fromArray(SPECIES, chunk.data, chunk.start + i);

                // Calculate current and previous absolute positions
                IntVector vCurr = vPrefix.add(startPosition);
                IntVector vPrev = vCurr.sub(vStep);

                // Calculate floorDiv(vCurr, 100) and floorDiv(vPrev, 100)
                // floorDiv(a, 100) = (a >= 0) ? a/100 : (a - 99)/100
                IntVector vFloorCurr = floorDiv100(vCurr);
                IntVector vFloorPrev = floorDiv100(vPrev);

                // Calculate floorDiv(vCurr - 1, 100) and floorDiv(vPrev - 1, 100) for negative
                // steps
                IntVector vFloorCurrMinus1 = floorDiv100(vCurr.sub(1));
                IntVector vFloorPrevMinus1 = floorDiv100(vPrev.sub(1));

                // Hits if step > 0: floor(curr/100) - floor(prev/100)
                IntVector hitsPos = vFloorCurr.sub(vFloorPrev);

                // Hits if step < 0: floor((prev-1)/100) - floor((curr-1)/100)
                IntVector hitsNeg = vFloorPrevMinus1.sub(vFloorCurrMinus1);

                // Select based on step direction
                VectorMask<Integer> maskNeg = vStep.compare(VectorOperators.LT, 0);
                IntVector vHits = hitsPos.blend(hitsNeg, maskNeg);

                count += vHits.reduceLanes(VectorOperators.ADD);
            }

            // Tail loop for Part 2
            for (; i < len; i++) {
                int step = chunk.data[chunk.start + i];
                int curr = startPosition + prefixSums[i];
                int prev = curr - step;

                if (step > 0) {
                    count += floorDiv(curr, 100) - floorDiv(prev, 100);
                } else if (step < 0) {
                    count += floorDiv(prev - 1, 100) - floorDiv(curr - 1, 100);
                }
            }
            return count;
        }

        private IntVector floorDiv100(IntVector v) {
            VectorMask<Integer> maskNeg = v.compare(VectorOperators.LT, 0);
            return v.blend(v.sub(99), maskNeg).div(100);
        }

        private int floorDiv(int a, int b) {
            // We know b = 100 > 0
            return (a >= 0) ? (a / b) : ((a - b + 1) / b);
        }
    }
}
