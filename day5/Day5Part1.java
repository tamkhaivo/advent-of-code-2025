package day5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Day5Part1 {
    private static final String INPUT_FILE = "day5/day5.txt";

    public static void main(String[] args) {
        List<List<Long>> bounds = new ArrayList<>();
        Long freshIngredients = 0L;
        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE))) {
            String line;
            boolean checkIngredients = false;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    checkIngredients = true;
                    continue;
                }
                if (!checkIngredients) {
                    String[] rowArray = line.split("-");
                    bounds.add(List.of(Long.parseLong(rowArray[0]), Long.parseLong(rowArray[1])));
                }
                if (checkIngredients) {
                    for (int boundIdx = 0; boundIdx < bounds.size(); boundIdx++) {
                        if (isBetween(bounds.get(boundIdx).get(0), bounds.get(boundIdx).get(1),
                                Long.parseLong(line))) {
                            freshIngredients++;
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(freshIngredients);
    }

    public static int solve(String input) {

        return 0;
    }

    public static boolean isBetween(long leftBounds, long rightBounds, long currentNumber) {
        return leftBounds <= currentNumber && currentNumber <= rightBounds;
    }
}
