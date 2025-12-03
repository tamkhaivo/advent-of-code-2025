package day1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Day1Part1OptimizedStreams {
    private static final String FILE_PATH = "day1/day1.txt";

    public static void main(String[] args) {
        try {
            long result = solve(FILE_PATH).get();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<Long> solve(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                Transition totalTransition = reader.lines()
                        .parallel()
                        .map(Transition::new)
                        .reduce(Transition.IDENTITY, Transition::merge);

                // We start at position 50.
                // The result is the number of hits starting from 50.
                return (long) totalTransition.hits[50];
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class Transition {
        // hits[i] = number of times we hit 0 starting from position i
        final int[] hits;
        // delta = net change in position (modulo 100)
        final int delta;

        static final Transition IDENTITY = new Transition();

        // Identity constructor
        private Transition() {
            this.hits = new int[100]; // All zeros
            this.delta = 0;
        }

        // Parse from line "L50" or "R100"
        Transition(String line) {
            this.hits = new int[100];
            int steps = Integer.parseInt(line.substring(1));
            if (line.startsWith("L")) {
                this.delta = Math.floorMod(-steps, 100);
            } else {
                this.delta = Math.floorMod(steps, 100);
            }

            // Calculate hits for each starting position
            for (int start = 0; start < 100; start++) {
                int end = (start + this.delta) % 100;
                if (end == 0) {
                    this.hits[start] = 1;
                } else {
                    this.hits[start] = 0;
                }
            }
        }

        // Merge two transitions: first then second
        // new_hits[s] = first.hits[s] + second.hits[(s + first.delta) % 100]
        // new_delta = (first.delta + second.delta) % 100
        static Transition merge(Transition first, Transition second) {
            if (first == IDENTITY)
                return second;
            if (second == IDENTITY)
                return first;

            int newDelta = (first.delta + second.delta) % 100;
            int[] newHits = new int[100];

            for (int i = 0; i < 100; i++) {
                // Hits from first part + hits from second part (starting where first part
                // ended)
                newHits[i] = first.hits[i] + second.hits[(i + first.delta) % 100];
            }

            return new Transition(newHits, newDelta);
        }

        private Transition(int[] hits, int delta) {
            this.hits = hits;
            this.delta = delta;
        }
    }
}
