package day3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Day3Part2VirtualThread {
    private static final String FILE_PATH = "day3/day3.txt";

    public static void main(String[] args) {
        long maxPower = solve(FILE_PATH);
        System.out.println(maxPower);
    }

    public static long solve(String filePath) {
        long sum = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
                ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<Future<Long>> futures = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String currentLine = line;
                futures.add(executor.submit(() -> Day3Part2.findMaxJoltage(currentLine)));
            }

            for (Future<Long> future : futures) {
                sum += future.get();
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }

        return sum;
    }
}
