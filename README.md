# Advent of Code 2025

This project implements solutions for the Advent of Code 2025 challenges using Java.

## Prerequisites

- JDK 21 or later (must support `jdk.incubator.vector` for Day 1).

## Dependencies

Please download the following libraries and place them in the `lib/` directory:

- [junit-platform-console-standalone-1.10.2.jar](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar)
- [log4j-api-2.23.1.jar](https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.23.1/log4j-api-2.23.1.jar)
- [log4j-core-2.23.1.jar](https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.23.1/log4j-core-2.23.1.jar)

## Day 1

### Compilation & Running

#### Main Application

To compile and run the main application:

```bash
# Compile
javac --add-modules jdk.incubator.vector -cp .:lib/log4j-api-2.23.1.jar:lib/log4j-core-2.23.1.jar -d . day1/simulation/*.java day1/Day1Part1ExtraCredit.java

# Run
java --add-modules jdk.incubator.vector -cp .:lib/log4j-api-2.23.1.jar:lib/log4j-core-2.23.1.jar day1.Day1Part1ExtraCredit
```

### Tests

This project uses JUnit 5 for testing. A standalone console launcher is provided in the `lib` directory.

To compile and run the tests:

```bash
# Compile Tests (and dependencies)
javac --add-modules jdk.incubator.vector -cp ".:lib/*" -d . day1/*.java day1/simulation/*.java day1/tests/*.java


# Run Tests
java --add-modules jdk.incubator.vector -jar lib/junit-platform-console-standalone-1.10.2.jar -cp .:lib/log4j-api-2.23.1.jar:lib/log4j-core-2.23.1.jar -c day1.tests.Day1Tests
```

## Day 2

### Compilation & Running

#### Solutions

To compile and run the solutions:

```bash
# Compile
javac -cp ".:lib/*" day2/*.java

# Run Part 1
java -cp ".:lib/*" day2.day2part1

# Run Part 2
java -cp ".:lib/*" day2.day2part2

# Run Part 1 Extra Credit (Thread Pool)
java -cp ".:lib/*" day2.Day2Part1ExtraCredit
```

### Tests

To compile and run the Day 2 tests:

```bash
# Compile Tests
javac -cp ".:lib/*" day2/*.java day2/tests/*.java

# Run Tests
java -jar lib/junit-platform-console-standalone-1.10.2.jar -cp ".:lib/*" -c day2.tests.Day2Tests
```

### Benchmarks

To run the benchmarks comparing Part 1, Part 2, and Extra Credit:

```bash
# Compile & Run Benchmarks
javac -cp ".:lib/*" day2/BenchmarkRunner.java && java -cp ".:lib/*" day2.BenchmarkRunner
```

## Project Structure

- `day1/`: Day 1 solutions and extra credit (Vector API).
- `day2/`: Day 2 solutions and extra credit (Thread Pool).
- `lib/`: Dependencies (JUnit, Log4j).

## Optimized Solutions

### Day 1

To compile and run the optimized solutions for Day 1:

```bash
# Compile Optimized Solutions
javac --add-modules jdk.incubator.vector -cp ".:lib/*" -d . day1/Day1Part1Optimized.java day1/Day1Part2Optimized.java

# Run Part 1 Optimized
java --add-modules jdk.incubator.vector -cp ".:lib/*" day1.Day1Part1Optimized

# Run Part 2 Optimized
java --add-modules jdk.incubator.vector -cp ".:lib/*" day1.Day1Part2Optimized
```

### Day 2

To compile and run the optimized solutions for Day 2:

```bash
# Compile Optimized Solutions
javac -cp ".:lib/*" day2/Day2Part1Optimized.java day2/Day2Part2Optimized.java

# Run Part 1 Optimized
java -cp ".:lib/*" day2.Day2Part1Optimized

# Run Part 2 Optimized
java -cp ".:lib/*" day2.Day2Part2Optimized
```

