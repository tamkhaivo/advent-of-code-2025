package day3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Day3Part1Optimized {

    private static final String FILE_PATH = "day3/day3.txt";

    public static void main(String[] args) {
        try {
            long result = solve(FILE_PATH);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long solve(String filePath) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
                FileChannel channel = file.getChannel()) {

            long fileSize = channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            long totalSum = 0;

            // State for current line processing
            int maxDigit = -1; // Max single digit found so far in current line
            int maxPair = -1; // Max pair value found so far in current line

            // We iterate byte by byte
            for (int i = 0; i < fileSize; i++) {
                byte b = buffer.get(i);

                if (b == '\n') {
                    // End of line
                    if (maxPair != -1) {
                        totalSum += maxPair;
                    }
                    // Reset for next line
                    maxDigit = -1;
                    maxPair = -1;
                } else if (b >= '0' && b <= '9') {
                    int val = b - '0';

                    // For each digit, we consider it as the right digit of a pair.
                    // The left digit is the maximum digit seen so far to its left.
                    // If we have seen at least one digit before this one (maxDigit != -1),
                    // we can form a pair: maxDigit * 10 + val.

                    if (maxDigit != -1) {
                        int currentPair = maxDigit * 10 + val;
                        if (currentPair > maxPair) {
                            maxPair = currentPair;
                        }
                    }

                    // Update maxDigit for future digits in this line
                    if (val > maxDigit) {
                        maxDigit = val;
                    }
                }
                // Ignore other characters (like \r)
            }

            // Handle last line if it doesn't end with newline
            if (maxPair != -1) {
                totalSum += maxPair;
            }

            return totalSum;
        }
    }
}
