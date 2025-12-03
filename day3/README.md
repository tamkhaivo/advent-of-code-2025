# Day 3: Battery Optimization

This directory contains the solutions for Day 3 of the Advent of Code 2025. The challenge involves analyzing battery banks to find the largest possible voltage output by selecting specific digits from each bank.

## Project Structure

```
day3/
├── BenchmarkRunner.java           # Runs performance benchmarks
├── Day3Part1.java                 # Solution for Part 1 (Sequential)
├── Day3Part1Optimized.java        # Optimized solution for Part 1
├── Day3Part1ExtraCredit.java      # Thread-pool solution for Part 1
├── Day3Part2.java                 # Solution for Part 2 (Sequential)
├── Day3Part2Optimized.java        # Optimized solution for Part 2
├── day3.txt                       # Input data file
└── tests/                         # Unit tests
    └── Day3Tests.java             # JUnit tests (Unit + Consistency)
```

## Prerequisites

- **Java 21+**
- **Libraries**:
  - `junit-platform-console-standalone-1.10.2.jar`
  - `log4j-api-2.23.1.jar`
  - `log4j-core-2.23.1.jar`

## Usage

All commands should be run from the **root of the repository** (parent of `day3/`).

### 1. Compilation

Compile all source files.

```bash
javac -cp ".:lib/*" -d . day3/*.java day3/tests/*.java
```

### 2. Running Solutions

**Part 1 (Sequential):**
```bash
java -cp ".:lib/*" day3.Day3Part1
```

**Part 1 (Optimized):**
```bash
java -cp ".:lib/*" day3.Day3Part1Optimized
```

**Part 1 (Extra Credit):**
```bash
java -cp ".:lib/*" day3.Day3Part1ExtraCredit
```

**Part 2 (Sequential):**
```bash
java -cp ".:lib/*" day3.Day3Part2
```

**Part 2 (Optimized):**
```bash
java -cp ".:lib/*" day3.Day3Part2Optimized
```

### 3. Running Benchmarks

Compare the performance of the different implementations.

```bash
java -cp ".:lib/*" day3.BenchmarkRunner
```

### 4. Running Tests

Run the JUnit tests to verify the correctness of the solutions.

```bash
java --add-modules jdk.incubator.vector -cp ".:lib/*" org.junit.platform.console.ConsoleLauncher -c day3.tests.Day3Tests
```
