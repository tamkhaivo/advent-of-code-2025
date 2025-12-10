package day6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day6Part1 {
    private static final String INPUT_FILE = "day6/day6.txt";

    public static void main(String[] args) {
        List<List<String>> bounds = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] rowArray = line.split(" ");
                List<String> rowList = new ArrayList<>();
                for (String s : rowArray) {
                    if (s.equals("")) {
                        continue;
                    }
                    rowList.add(s);
                }
                bounds.add(rowList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean isAddition = true;
        long totalSum = 0;
        for (int col = 0; col < bounds.get(0).size(); col++) {
            long result = 0;
            for (int row = bounds.size() - 1; row >= 0; row--) {
                if (row == bounds.size() - 1) {
                    isAddition = (bounds.get(row).get(col).equals("+")) ? true : false;
                    continue;
                }
                if (isAddition) {
                    if (result == 0) {
                        result = Long.parseLong(bounds.get(row).get(col));
                    } else {
                        result += Long.parseLong(bounds.get(row).get(col));
                    }
                } else {
                    if (result == 0) {
                        result = Long.parseLong(bounds.get(row).get(col));
                    } else {
                        result *= Long.parseLong(bounds.get(row).get(col));
                    }
                }
            }
            totalSum += result;
        }
        System.out.println(totalSum);
    }
}
