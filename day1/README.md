# Day 1: Dial Simulation

This directory contains the solutions for Day 1 of the Advent of Code 2025. The challenge involves simulating a dial's movement based on input instructions and counting specific events (e.g., crossing zero).

## Project Structure

```
day1/
├── BenchmarkRunner.java        # Runs performance benchmarks for all solutions
├── Day1Part1.java             # Solution for Part 1 (Sequential)
├── Day1Part1ExtraCredit.java  # Optimized solution for Part 1 (Vectorized)
├── Day1Part2.java             # Solution for Part 2
├── Day1Part2ExtraCredit.java  # Optimized solution for Part 2
├── day1.txt                   # Input data file
├── simulation/                # Core simulation logic
│   ├── DialSimulator.java     # Main simulator class (Builder pattern)
│   ├── SimulationStrategy.java # Strategy interface
│   └── VectorizedSimulationStrategy.java # SIMD-optimized strategy
└── tests/                     # Unit tests
    └── Day1Tests.java         # JUnit tests (Unit + Consistency)
```

## Prerequisites

- **Java 21+** (Required for `jdk.incubator.vector`)
- **Libraries**:
  - `junit-platform-console-standalone-1.10.2.jar`
  - `log4j-api-2.23.1.jar`
  - `log4j-core-2.23.1.jar`

## Usage

All commands should be run from the **root of the repository** (parent of `day1/`).

### 1. Compilation

Compile all source files, including the simulation logic and tests.

```bash
javac --add-modules jdk.incubator.vector -cp ".:lib/*" -d . day1/*.java day1/simulation/*.java day1/tests/*.java
```

### 2. Running Solutions

**Part 1 (Sequential):**
```bash
java -cp ".:lib/*" day1.Day1Part1
```

**Part 1 (Extra Credit - Vectorized):**
```bash
java --add-modules jdk.incubator.vector -cp ".:lib/*" day1.Day1Part1ExtraCredit
```

**Part 1 (Optimized):**
```bash
java --add-modules jdk.incubator.vector --add-exports java.base/sun.nio.ch=ALL-UNNAMED -cp ".:lib/*" day1.Day1Part1Optimized
```

**Part 2:**
```bash
java -cp ".:lib/*" day1.Day1Part2
```

**Part 2 (Extra Credit):**
```bash
java --add-modules jdk.incubator.vector -cp ".:lib/*" day1.Day1Part2ExtraCredit
```

### 3. Running Benchmarks

Compare the performance of the different implementations.

```bash
java --add-modules jdk.incubator.vector --add-exports java.base/sun.nio.ch=ALL-UNNAMED -cp ".:lib/*" day1.BenchmarkRunner
```

### 4. Running Tests

Run the JUnit tests to verify the correctness of the solutions.

```bash
java --add-modules jdk.incubator.vector -cp ".:lib/*" org.junit.platform.console.ConsoleLauncher -c day1.tests.Day1Tests
```
