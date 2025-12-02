# Advent of Code 2025 - Day 1 Extra Credit

This project implements a solution for Day 1 Part 1 Extra Credit using Java's Vector API and a custom simulation engine.

## Prerequisites

- JDK 21 or later (must support `jdk.incubator.vector`).

## Compilation & Running

### Main Application

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

## Project Structure

- `day1/Day1Part1ExtraCredit.java`: Main entry point.
- `day1/simulation`: Domain logic (Strategies, Simulator).
- `day1/tests`: Unit tests.
