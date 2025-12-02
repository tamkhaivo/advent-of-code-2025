package day1;

import day1.simulation.VectorizedSimulationStrategy;
import day1.simulation.VectorizedSimulationStrategy.SimulationType;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Optional;

/**
 * Optimized solution for Day 1 Part 2.
 *
 * This implementation leverages:
 *
 * - Memory Mapped I/O for high-performance file reading.
 * - Custom integer parsing to minimize object allocation.
 * - {@link VectorizedSimulationStrategy} for SIMD-accelerated zero-crossing
 * counting.
 * - Vectorized integer parsing for SIMD-accelerated parsing.
 */
public class Day1Part2Optimized {

    private static final String FILE_PATH = "day1/day1.txt";
    private static final int INITIAL_POSITION = 50;

    public static void main(String[] args) {
        try {
            long result = solve(FILE_PATH);
            System.out.println(result);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Solves the Day 1 Part 2 problem for the given file path.
     *
     * @param filePath The path to the input file.
     * @return The total number of zero crossings.
     * @throws IOException If an I/O error occurs.
     */
    public static long solve(String filePath) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
                FileChannel channel = file.getChannel()) {

            long fileSize = channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);
            return solve(buffer, (int) fileSize);
        }
    }

    /**
     * Solves the Day 1 Part 2 problem for the given buffer.
     *
     * @param buffer       The memory-mapped buffer containing the input data.
     * @param sizeEstimate An estimate of the file size for array allocation.
     * @return The total number of zero crossings.
     */
    public static long solve(java.nio.ByteBuffer buffer, int sizeEstimate) {
        // 1. Parse file into int[] steps
        // We overestimate size to avoid resizing.
        int[] steps = new int[sizeEstimate / 2 + 1];
        int count = 0;

        while (buffer.hasRemaining()) {
            // Skip whitespace/newlines if any
            if (buffer.remaining() == 0)
                break;

            byte b = buffer.get();
            while (b == '\n' || b == '\r' || b == ' ') {
                if (!buffer.hasRemaining())
                    break;
                b = buffer.get();
            }
            if (!buffer.hasRemaining() && (b == '\n' || b == '\r' || b == ' '))
                break;

            // Parse 'L' or 'R'
            // 'L' = 76, 'R' = 82
            // L means subtract, R means add.
            int sign = (b == 'R') ? 1 : -1;

            // Parse number
            int val = 0;
            if (buffer.hasRemaining()) {
                b = buffer.get();
                while (b >= '0' && b <= '9') {
                    val = val * 10 + (b - '0');
                    if (!buffer.hasRemaining())
                        break;
                    b = buffer.get();
                }
            }

            steps[count++] = val * sign;
        }

        int[] actualSteps = new int[count];
        System.arraycopy(steps, 0, actualSteps, 0, count);

        // 2. Execute Strategy
        VectorizedSimulationStrategy strategy = new VectorizedSimulationStrategy(SimulationType.PART2_CROSS_ZERO);
        Optional<Integer> result = strategy.run(actualSteps, INITIAL_POSITION);

        return result.orElse(0);
    }
}
