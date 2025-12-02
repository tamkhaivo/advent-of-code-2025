package day2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class day2part1 {
    private static final String FILE_PATH = "day2/day2.txt";

    public static void main(String[] args) {
        long invalidIDCount = solve(FILE_PATH);
        System.out.println(invalidIDCount);
    }

    public static long solve(String filePath) {
        List<long[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                for (String value : values) {
                    String[] ranges = value.split("-");
                    long start = Long.parseLong(ranges[0]);
                    long end = Long.parseLong(ranges[1]);
                    lines.add(new long[] { start, end });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        return countInvalidIDs(lines);
    }

    private static long countInvalidIDs(List<long[]> lines) {
        long count = 0;
        HashSet<Long> invalidIDs = new HashSet<>();
        for (long[] range : lines) {
            long start = range[0];
            long end = range[1];
            for (long currentID = start; currentID <= end; currentID++) {
                if (isInvalidID(currentID) && !invalidIDs.contains(currentID)) {
                    count += currentID;
                    invalidIDs.add(currentID);
                }
            }
        }
        System.out.println(invalidIDs);
        return count;
    }

    public static boolean isInvalidID(long ID) {
        String s = Long.toString(ID);
        if (s.length() % 2 != 0) {
            return false;
        }
        String firstHalf = s.substring(0, s.length() / 2);
        String secondHalf = s.substring(s.length() / 2);
        return firstHalf.equals(secondHalf);
    }

    // 2121
    // mod 10 = 1
    // div 10 = 212
    // mod 10 = 2
    // div 10 = 12
}
