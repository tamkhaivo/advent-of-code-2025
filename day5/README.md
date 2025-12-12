# Day 5: Ingredient Freshness

This directory contains the solutions for Day 5 of the Advent of Code 2025. The challenge involves checking ingredient freshness values against valid bounds ranges.

## Project Structure

```
day5/
├── BenchmarkRunner.java           # Runs performance benchmarks
├── Day5Part1.java                 # Solution for Part 1 (Sequential)
├── Day5Part1VirtualThread.java    # Virtual Thread solution for Part 1
├── Day5Part2.java                 # Solution for Part 2 (Sequential)
├── Day5Part2VirtualThread.java    # Virtual Thread solution for Part 2
├── Day5Tests.java                 # JUnit tests (Unit + Consistency)
└── day5.txt                       # Input data file
```

## Prerequisites

- **Java 21+**
- **Libraries**:
  - `junit-platform-console-standalone-1.10.2.jar`
  - `log4j-api-2.23.1.jar`
  - `log4j-core-2.23.1.jar`

## Usage

All commands should be run from the **root of the repository** (parent of `day5/`).

### 1. Compilation

Compile all source files.

```bash
javac -cp ".:lib/*" -d . day5/*.java
```

### 2. Running Solutions

**Part 1 (Sequential):**
```bash
java -cp ".:lib/*" day5.Day5Part1
```

**Part 2 (Sequential):**
```bash
java -cp ".:lib/*" day5.Day5Part2
```

**Part 1 (Virtual Thread):**
```bash
java -cp ".:lib/*" day5.Day5Part1VirtualThread
```

**Part 2 (Virtual Thread):**
```bash
java -cp ".:lib/*" day5.Day5Part2VirtualThread
```

### 3. Running Benchmarks

Compare the performance of the different implementations.

```bash
java -cp ".:lib/*" day5.BenchmarkRunner
```

### 4. Running Tests

Run the JUnit tests to verify the correctness of the solutions.

```bash
java -cp ".:lib/*" org.junit.platform.console.ConsoleLauncher -c day5.Day5Tests
```
