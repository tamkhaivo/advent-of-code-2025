package day4;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorMask;

import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import java.lang.invoke.VarHandle;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Optimized solution for Day 4 Part 2 using Java 25 features.
 * Requires: --add-modules jdk.incubator.vector
 * --enable-native-access=ALL-UNNAMED
 */
public class Day4Part2AIOptimized {
    final static String INPUT_FILE = "day4/day4.txt";
    private static final byte CHAR_AT = (byte) '@';
    private static final byte CHAR_DOT = (byte) '.';

    // VarHandle for atomic access to the grid
    private static final VarHandle BYTE_HANDLE = ValueLayout.JAVA_BYTE.varHandle();

    public static void main(String[] args) {
        try {
            System.out.println("Removable toilet paper: " + solve(INPUT_FILE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int solve(String filePath) throws Exception {
        // 1. Read file and determine dimensions
        List<byte[]> rawLines = new ArrayList<>();
        int rows = 0;
        int cols = 0;

        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            long len = raf.length();
            try (FileChannel channel = raf.getChannel()) {
                MemorySegment fileSegment = channel.map(FileChannel.MapMode.READ_ONLY, 0, len, Arena.global());
                long offset = 0;
                while (offset < len) {
                    long lineEnd = offset;
                    while (lineEnd < len && fileSegment.get(ValueLayout.JAVA_BYTE, lineEnd) != '\n') {
                        lineEnd++;
                    }
                    // Handle CRLF or LF
                    long contentEnd = lineEnd;
                    if (contentEnd > offset && fileSegment.get(ValueLayout.JAVA_BYTE, contentEnd - 1) == '\r') {
                        contentEnd--;
                    }

                    int lineLen = (int) (contentEnd - offset);
                    if (lineLen > 0) {
                        byte[] line = new byte[lineLen];
                        MemorySegment.copy(fileSegment, ValueLayout.JAVA_BYTE, offset, line, 0, lineLen);
                        rawLines.add(line);
                        if (cols == 0)
                            cols = lineLen;
                    }
                    offset = lineEnd + 1;
                }
            }
        }

        rows = rawLines.size();
        if (rows == 0)
            return 0;

        // 2. Setup Padded Grid in Off-Heap Memory
        int paddedRows = rows + 2;
        int paddedCols = cols + 2;
        long totalCells = (long) paddedRows * paddedCols;

        try (Arena arena = Arena.ofShared()) {
            MemorySegment grid = arena.allocate(totalCells, 1);
            grid.fill(CHAR_DOT); // Fill with dots

            // Copy data to grid
            for (int r = 0; r < rows; r++) {
                byte[] line = rawLines.get(r);
                long gridOffset = (long) (r + 1) * paddedCols + 1;
                MemorySegment.copy(line, 0, grid, ValueLayout.JAVA_BYTE, gridOffset, cols);
            }

            // 3. Initial Scan with Vector API
            // We find all '@' that have < 4 neighbors.
            List<Integer> initialCandidates = new ArrayList<>();
            int numThreads = Runtime.getRuntime().availableProcessors();

            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<List<Integer>>> futures = new ArrayList<>();
                int chunkSize = rows / numThreads;
                if (chunkSize == 0)
                    chunkSize = rows;

                final int finalCols = cols;

                for (int i = 0; i < rows; i += chunkSize) {
                    int startRow = i;
                    int endRow = Math.min(i + chunkSize, rows);
                    futures.add(executor.submit(() -> scanRange(grid, startRow, endRow, paddedCols, finalCols)));
                }

                for (var future : futures) {
                    initialCandidates.addAll(future.get());
                }
            }

            // 4. Process Queue using Bulk Synchronous Parallel (BSP) approach
            AtomicInteger removableCount = new AtomicInteger(0);

            // Offsets for 8 neighbors
            int[] neighborOffsets = {
                    -paddedCols - 1, -paddedCols, -paddedCols + 1,
                    -1, 1,
                    paddedCols - 1, paddedCols + 1,
                    paddedCols
            };

            List<Integer> currentRound = initialCandidates;

            // We use a fixed thread pool for the compute-heavy cascade phase to avoid
            // overhead
            try (var executor = Executors.newFixedThreadPool(numThreads)) {
                while (!currentRound.isEmpty()) {
                    ConcurrentLinkedQueue<Integer> nextRoundQueue = new ConcurrentLinkedQueue<>();

                    // Split current round into chunks
                    int roundSize = currentRound.size();
                    int batchSize = Math.max(1024, roundSize / numThreads);
                    List<Future<?>> futures = new ArrayList<>();

                    for (int i = 0; i < roundSize; i += batchSize) {
                        int start = i;
                        int end = Math.min(i + batchSize, roundSize);
                        List<Integer> batch = currentRound.subList(start, end);

                        futures.add(executor.submit(() -> {
                            for (int idx : batch) {
                                // Since we deduplicated currentRound, we have exclusive access to 'remove' this
                                // cell
                                // (assuming it wasn't removed in a previous round, which it shouldn't be if
                                // logic is correct)
                                // But to be safe and consistent with visibility:
                                byte currentVal = (byte) BYTE_HANDLE.getVolatile(grid, (long) idx);
                                if (currentVal == CHAR_AT) {
                                    BYTE_HANDLE.setVolatile(grid, (long) idx, CHAR_DOT);
                                    removableCount.incrementAndGet();

                                    // Check neighbors
                                    for (int offset : neighborOffsets) {
                                        int nIdx = idx + offset;
                                        // Optimistic read first
                                        if ((byte) BYTE_HANDLE.getVolatile(grid, (long) nIdx) == CHAR_AT) {
                                            if (countNeighborsScalar(grid, nIdx, neighborOffsets) < 4) {
                                                nextRoundQueue.add(nIdx);
                                            }
                                        }
                                    }
                                }
                            }
                        }));
                    }

                    for (var f : futures) {
                        f.get();
                    }

                    // Prepare for next round
                    // Deduplicate
                    if (!nextRoundQueue.isEmpty()) {
                        currentRound = new ArrayList<>(new HashSet<>(nextRoundQueue));
                    } else {
                        currentRound = Collections.emptyList();
                    }
                }
            }

            return removableCount.get();
        }
    }

    private static List<Integer> scanRange(MemorySegment grid, int startRow, int endRow, int paddedCols, int cols) {
        List<Integer> candidates = new ArrayList<>();
        VectorSpecies<Byte> species = ByteVector.SPECIES_PREFERRED;
        int loopBound = species.loopBound(cols);

        // Offsets for neighbors relative to current position
        int[] offsets = {
                -paddedCols - 1, -paddedCols, -paddedCols + 1,
                -1, 1,
                paddedCols - 1, paddedCols, paddedCols + 1
        };

        for (int r = startRow; r < endRow; r++) {
            long rowOffset = (long) (r + 1) * paddedCols + 1;
            int c = 0;

            // Vector Loop
            for (; c < loopBound; c += species.length()) {
                long curPos = rowOffset + c;
                ByteVector vCurr = ByteVector.fromMemorySegment(species, grid, curPos,
                        java.nio.ByteOrder.nativeOrder());

                // Mask of cells that are '@'
                VectorMask<Byte> maskAt = vCurr.eq(CHAR_AT);

                if (!maskAt.anyTrue())
                    continue;

                // Count neighbors for this vector of cells
                ByteVector vCounts = ByteVector.zero(species);

                for (int off : offsets) {
                    ByteVector vNeighbor = ByteVector.fromMemorySegment(species, grid, curPos + off,
                            java.nio.ByteOrder.nativeOrder());
                    // If neighbor is '@', subtract -1 (add 1)
                    vCounts = vCounts.sub(vNeighbor.eq(CHAR_AT).toVector());
                }

                // We want cells where count < 4
                VectorMask<Byte> maskLess4 = vCounts.lt((byte) 4);

                // Intersection: is '@' AND has < 4 neighbors
                VectorMask<Byte> maskTarget = maskAt.and(maskLess4);

                if (maskTarget.anyTrue()) {
                    long res = maskTarget.toLong();
                    int lane = 0;
                    while (res != 0) {
                        if ((res & 1) != 0) {
                            candidates.add((int) (curPos + lane));
                        }
                        res >>>= 1;
                        lane++;
                    }
                }
            }

            // Scalar Loop for remaining
            for (; c < cols; c++) {
                long curPos = rowOffset + c;
                if (grid.get(ValueLayout.JAVA_BYTE, curPos) == CHAR_AT) {
                    if (countNeighborsScalar(grid, (int) curPos, offsets) < 4) {
                        candidates.add((int) curPos);
                    }
                }
            }
        }
        return candidates;
    }

    private static int countNeighborsScalar(MemorySegment grid, int idx, int[] offsets) {
        int count = 0;
        for (int offset : offsets) {
            // Use volatile get to ensure visibility of updates from other threads
            if ((byte) BYTE_HANDLE.getVolatile(grid, (long) (idx + offset)) == CHAR_AT) {
                count++;
            }
        }
        return count;
    }
}
