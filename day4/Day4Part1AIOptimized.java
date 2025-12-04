package day4;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorMask;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

/**
 * Optimized solution for Day 4 Part 1 using Java 25 features:
 * - Foreign Function & Memory API (MemorySegment) for efficient file mapping.
 * - Vector API for SIMD parallelism.
 */
public class Day4Part1AIOptimized {
    static final String INPUT_FILE = "day4/day4.txt";
    private static final VectorSpecies<Byte> SPECIES = ByteVector.SPECIES_PREFERRED;

    public static void main(String[] args) {
        try {
            long result = solve(INPUT_FILE);
            System.out.println("Removable toilet paper: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long solve(String filePath) throws IOException {
        Path path = Path.of(filePath);
        long size = Files.size(path);

        // For small files, read into heap to avoid mmap overhead
        if (size < 1024 * 1024) {
            return solve(Files.readAllBytes(path));
        }

        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
                Arena arena = Arena.ofShared()) {

            MemorySegment mappedFile = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size(), arena);
            return solve(mappedFile);
        }
    }

    // Overload for testing with byte array
    public static long solve(byte[] input) {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment segment = arena.allocate(input.length);
            segment.copyFrom(MemorySegment.ofArray(input));
            return solve(segment);
        }
    }

    public static long solve(MemorySegment grid) {
        long fileSize = grid.byteSize();
        if (fileSize == 0)
            return 0;

        // Determine dimensions
        long width = 0;
        while (width < fileSize && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, width) != '\n'
                && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, width) != '\r') {
            width++;
        }

        if (width == 0)
            return 0;

        long stride = width;
        if (width < fileSize && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, width) == '\r')
            stride++;
        if (width < stride && stride < fileSize && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, stride) == '\n')
            stride++;

        // Handle single line case without newline
        if (stride == width && width < fileSize)
            stride++;
        if (stride == 0)
            stride = fileSize;

        final long COLS = width;
        final long STRIDE = stride;
        final long ROWS = (fileSize + STRIDE - 1) / STRIDE;

        LongAdder removableToiletPaper = new LongAdder();

        // Process rows in parallel
        IntStream.range(0, (int) ROWS).parallel().forEach(r -> {
            long row = r;
            long rowOffset = row * STRIDE;
            if (rowOffset >= fileSize)
                return;

            long col = 0;

            // 1. Scalar loop for the left edge (col = 0)
            if (COLS > 0) {
                if (grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, rowOffset) == '@') {
                    if (countNeighborsScalar(grid, row, 0, ROWS, COLS, STRIDE, fileSize) < 4) {
                        removableToiletPaper.increment();
                    }
                }
                col++;
            }

            // 2. Vector loop for the "safe" inner body
            // We need [col-1] and [col+VLEN] to be valid.
            // So col >= 1 is already true.
            // We need col + VLEN < COLS.
            long vectorLimit = COLS - SPECIES.length();

            // Only enter vector loop if we have enough space and we are not at the edges of
            // the file (first/last row)
            // For simplicity and safety with unmasked loads, we can just use the vector
            // loop for middle rows.
            // Or we can handle first/last rows carefully.
            // The neighbor loading logic checks row bounds, so we just need to ensure
            // column bounds for unmasked loads.

            if (col < vectorLimit) {
                for (; col < vectorLimit; col += SPECIES.length()) {
                    long index = rowOffset + col;

                    // Load current block UNMASKED
                    ByteVector currentBlock = ByteVector.fromMemorySegment(SPECIES, grid, index,
                            java.nio.ByteOrder.nativeOrder());

                    // Mask for '@' characters
                    VectorMask<Byte> isToiletPaper = currentBlock.compare(VectorOperators.EQ, (byte) '@');

                    if (!isToiletPaper.anyTrue())
                        continue;

                    // Count neighbors for this vector of cells
                    ByteVector neighborCounts = countNeighborsVectorUnmasked(grid, row, col, ROWS, COLS, STRIDE,
                            fileSize);

                    // Check if count < 4
                    VectorMask<Byte> isRemovable = neighborCounts.compare(VectorOperators.LT, (byte) 4);

                    // Combine: is '@' AND count < 4
                    VectorMask<Byte> resultMask = isToiletPaper.and(isRemovable);

                    removableToiletPaper.add(resultMask.trueCount());
                }
            }

            // 3. Scalar loop for the remaining columns (right edge)
            for (; col < COLS; col++) {
                long index = rowOffset + col;
                if (grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, index) == '@') {
                    if (countNeighborsScalar(grid, row, col, ROWS, COLS, STRIDE, fileSize) < 4) {
                        removableToiletPaper.increment();
                    }
                }
            }
        });

        return removableToiletPaper.sum();
    }

    private static ByteVector countNeighborsVectorUnmasked(MemorySegment grid, long row, long col, long ROWS, long COLS,
            long STRIDE, long fileSize) {
        ByteVector counts = ByteVector.zero(SPECIES);
        long currentRowOffset = row * STRIDE;

        // We assume we are in the "safe" zone horizontally: col >= 1 and col + VLEN <
        // COLS.
        // So horizontal offsets -1 and +1 are safe for unmasked loading relative to row
        // start.

        // However, we still need to check vertical bounds (first/last row).

        // Top Row Neighbors
        if (row > 0) {
            long topRowOffset = (row - 1) * STRIDE;
            // We can load unmasked because we are horizontally safe
            counts = counts.add(loadVectorUnmasked(grid, topRowOffset, col, -1)); // Top-Left
            counts = counts.add(loadVectorUnmasked(grid, topRowOffset, col, 0)); // Top
            counts = counts.add(loadVectorUnmasked(grid, topRowOffset, col, 1)); // Top-Right
        }

        // Middle Row Neighbors
        counts = counts.add(loadVectorUnmasked(grid, currentRowOffset, col, -1)); // Left
        counts = counts.add(loadVectorUnmasked(grid, currentRowOffset, col, 1)); // Right

        // Bottom Row Neighbors
        if (row < ROWS - 1) {
            long bottomRowOffset = (row + 1) * STRIDE;
            counts = counts.add(loadVectorUnmasked(grid, bottomRowOffset, col, -1)); // Bottom-Left
            counts = counts.add(loadVectorUnmasked(grid, bottomRowOffset, col, 0)); // Bottom
            counts = counts.add(loadVectorUnmasked(grid, bottomRowOffset, col, 1)); // Bottom-Right
        }

        return counts;
    }

    private static ByteVector loadVectorUnmasked(MemorySegment grid, long rowOffset, long col, int colOffset) {
        long loadIndex = rowOffset + col + colOffset;

        // Unmasked load
        ByteVector v = ByteVector.fromMemorySegment(SPECIES, grid, loadIndex, java.nio.ByteOrder.nativeOrder());

        // Compare with '@'
        VectorMask<Byte> isAt = v.compare(VectorOperators.EQ, (byte) '@');

        // Convert mask to 0/1 vector (true -> -1, so neg() -> 1)
        return isAt.toVector().neg().reinterpretAsBytes();
    }

    private static int countNeighborsScalar(MemorySegment grid, long row, long col, long ROWS, long COLS, long STRIDE,
            long fileSize) {
        int count = 0;

        // Top Row
        if (row > 0) {
            long topRowOffset = (row - 1) * STRIDE;
            if (col > 0 && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, topRowOffset + col - 1) == '@')
                count++;
            if (grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, topRowOffset + col) == '@')
                count++;
            if (col < COLS - 1 && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, topRowOffset + col + 1) == '@')
                count++;
        }

        // Middle Row
        long currentRowOffset = row * STRIDE;
        if (col > 0 && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, currentRowOffset + col - 1) == '@')
            count++;
        if (col < COLS - 1 && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, currentRowOffset + col + 1) == '@')
            count++;

        // Bottom Row
        if (row < ROWS - 1) {
            long bottomRowOffset = (row + 1) * STRIDE;
            if (bottomRowOffset < fileSize) {
                if (col > 0 && bottomRowOffset + col - 1 < fileSize
                        && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, bottomRowOffset + col - 1) == '@')
                    count++;
                if (bottomRowOffset + col < fileSize
                        && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, bottomRowOffset + col) == '@')
                    count++;
                if (col < COLS - 1 && bottomRowOffset + col + 1 < fileSize
                        && grid.get(java.lang.foreign.ValueLayout.JAVA_BYTE, bottomRowOffset + col + 1) == '@')
                    count++;
            }
        }

        return count;
    }
}
