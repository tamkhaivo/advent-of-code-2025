package day1;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;

import jdk.incubator.vector.VectorSpecies;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Optimized solution for Day 1 Part 1 using:
 * 1. Memory Mapped I/O for fast file reading.
 * 2. Custom integer parsing to avoid object allocation.
 * 3. Single-pass prefix sum calculation.
 * 4. SIMD (Vector API) for parallel zero-check.
 */
public class Day1Part1Optimized {

    private static final String FILE_PATH = "day1/day1.txt";
    private static final int INITIAL_POSITION = 50;
    private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    public static void main(String[] args) {
        try {
            long result = solve(FILE_PATH);
            System.out.println(result);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static long solve(String filePath) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
                FileChannel channel = file.getChannel()) {

            long fileSize = channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);
            return solve(buffer, (int) fileSize);
        }
    }

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

        // 2. Compute Prefix Sums in-place
        int currentSum = 0;
        for (int i = 0; i < count; i++) {
            currentSum += steps[i];
            steps[i] = currentSum;
        }

        // 3. Vectorized Count
        long zeroCount = 0;
        int i = 0;
        int loopBound = SPECIES.loopBound(count);

        for (; i < loopBound; i += SPECIES.length()) {
            IntVector vSums = IntVector.fromArray(SPECIES, steps, i);
            IntVector vPos = vSums.add(INITIAL_POSITION);

            // Check vPos % 100 == 0
            IntVector vDiv = vPos.div(100);
            IntVector vRem = vPos.sub(vDiv.mul(100));

            VectorMask<Integer> mask = vRem.eq(0);
            zeroCount += mask.trueCount();
        }

        // Tail loop
        for (; i < count; i++) {
            if ((steps[i] + INITIAL_POSITION) % 100 == 0) {
                zeroCount++;
            }
        }

        return zeroCount;
    }
}
