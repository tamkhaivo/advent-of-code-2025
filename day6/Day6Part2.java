package day6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day6Part2 {
    private static final String INPUT_FILE = "day6/day6.txt";

    public static void main(String[] args) {
        String inputFile = (args.length > 0) ? args[0] : INPUT_FILE;
        List<String> lines = new ArrayList<>();
        int maxLen = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ensure we don't drop trailing spaces if they exist, but readLine trims
                // newline
                lines.add(line);
                if (line.length() > maxLen) {
                    maxLen = line.length();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Pad lines
        for (int i = 0; i < lines.size(); i++) {
            StringBuilder sb = new StringBuilder(lines.get(i));
            while (sb.length() < maxLen) {
                sb.append(" ");
            }
            lines.set(i, sb.toString());
        }

        long grandTotal = 0;
        int rows = lines.size();

        List<Integer> currentProblemCols = new ArrayList<>();

        // Scan Right to Left
        // Note: cols list will accumulate R->L, so first added is Rightmost col.
        for (int col = maxLen - 1; col >= -1; col--) {
            boolean isSeparator = false;
            if (col == -1) {
                isSeparator = true; // End of processing
            } else {
                boolean allSpaces = true;
                for (int r = 0; r < rows; r++) {
                    if (lines.get(r).charAt(col) != ' ') {
                        allSpaces = false;
                        break;
                    }
                }
                isSeparator = allSpaces;
            }

            if (!isSeparator) {
                currentProblemCols.add(col);
            } else {
                if (!currentProblemCols.isEmpty()) {
                    // Process the accumulated problem
                    long problemResult = solveProblem(lines, currentProblemCols);
                    grandTotal += problemResult;
                    currentProblemCols.clear();
                }
            }
        }

        System.out.println(grandTotal);
    }

    private static long solveProblem(List<String> lines, List<Integer> cols) {
        List<Long> numbers = new ArrayList<>();
        char operator = '?';

        for (int col : cols) {
            StringBuilder numStr = new StringBuilder();
            char colOp = ' ';

            for (int r = 0; r < lines.size(); r++) {
                char ch = lines.get(r).charAt(col);
                if (Character.isDigit(ch)) {
                    numStr.append(ch);
                } else if (ch == '+' || ch == '*') {
                    colOp = ch;
                }
            }

            if (numStr.length() > 0) {
                numbers.add(Long.parseLong(numStr.toString()));
            }
            if (colOp != ' ') {
                operator = colOp;
            }
        }

        if (operator == '?') {
            // It's possible a "problem" is just noise or empty space if logic is loose,
            // but strictly per Spec, valid problems have an operator.
            // If we find no operator, ignore or error?
            // With "spaces separators", we might accidentally pick up empty blocks if not
            // careful?
            // "consisting ONLY of spaces".
            // So a non-empty block MUST have something.
            // If it has just numbers and no operator?
            // I'll log and return 0 to be safe, or throw.
            // Given well-formed input, this shouldn't happen.
            System.err.println("No operator found for problem at cols " + cols);
            return 0;
        }

        long result;
        if (operator == '+') {
            result = 0;
            for (long n : numbers) {
                result += n;
            }
        } else if (operator == '*') {
            result = 1;
            for (long n : numbers) {
                result *= n;
            }
        } else {
            System.err.println("Unknown operator: " + operator);
            return 0;
        }

        return result;
    }
}
