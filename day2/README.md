# Day 2: Invalid ID Counting

This directory contains the solutions for Day 2 of the Advent of Code 2025. The challenge involves identifying and counting "invalid" IDs based on specific pattern matching rules (palindromes, repeated sequences).

## Project Structure

```
day2/
├── BenchmarkRunner.java           # Runs performance benchmarks
├── day2part1.java                 # Solution for Part 1 (Sequential)
├── Day2Part1Optimized.java        # Optimized solution for Part 1 (Generative)
├── Day2Part1ExtraCredit.java      # Thread-pool solution for Part 1
├── day2part2.java                 # Solution for Part 2 (Sequential)
├── Day2Part2Optimized.java        # Optimized solution for Part 2 (Generative + Parallel)
├── Day2Part2ExtraCredit.java      # Thread-pool solution for Part 2
├── day2.txt                       # Input data file
└── tests/                         # Unit tests
    └── Day2Tests.java             # JUnit tests (Unit + Consistency)
```

## Prerequisites

- **Java 21+**
- **Libraries**:
  - `junit-platform-console-standalone-1.10.2.jar`
  - `log4j-api-2.23.1.jar`
  - `log4j-core-2.23.1.jar`

## Usage

All commands should be run from the **root of the repository** (parent of `day2/`).

### 1. Compilation

Compile all source files.

```bash
javac -cp ".:lib/*" -d . day2/*.java day2/tests/*.java
```

### 2. Running Solutions

**Part 1 (Sequential):**
```bash
java -cp ".:lib/*" day2.day2part1
```

**Part 1 (Optimized):**
```bash
java -cp ".:lib/*" day2.Day2Part1Optimized
```

**Part 1 (Extra Credit):**
```bash
java -cp ".:lib/*" day2.Day2Part1ExtraCredit
```

**Part 2 (Sequential):**
```bash
java -cp ".:lib/*" day2.day2part2
```

**Part 2 (Optimized):**
```bash
java -cp ".:lib/*" day2.Day2Part2Optimized
```

**Part 2 (Extra Credit):**
```bash
java -cp ".:lib/*" day2.Day2Part2ExtraCredit
```

### 3. Running Benchmarks

Compare the performance of the different implementations.

```bash
java -cp ".:lib/*" day2.BenchmarkRunner
```

### 4. Running Tests

Run the JUnit tests to verify the correctness of the solutions.

```bash
java --add-modules jdk.incubator.vector -cp ".:lib/*" org.junit.platform.console.ConsoleLauncher -c day2.tests.Day2Tests
```
