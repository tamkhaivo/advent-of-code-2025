# Day 4: Toilet Paper Optimization

This directory contains the solutions for Day 4 of the Advent of Code 2025. The challenge involves optimizing the removal of toilet paper rolls from a grid based on neighbor counts.

## Project Structure

```
day4/
├── BenchmarkRunner.java           # Runs performance benchmarks
├── Day4Part1.java                 # Solution for Part 1 (Sequential)
├── Day4Part1Optimized.java        # Optimized solution for Part 1
├── Day4Part1AIOptimized.java      # AI Optimized solution for Part 1 (Java 25)
├── Day4Part1VirtualThread.java    # Virtual Thread solution for Part 1
├── Day4Part2.java                 # Solution for Part 2 (Sequential)
├── Day4Part2Optimized.java        # Optimized solution for Part 2
├── Day4Part2AIOptimized.java      # AI Optimized solution for Part 2 (Java 25)
├── Day4Part2VirtualThread.java    # Virtual Thread solution for Part 2
├── day4.txt                       # Input data file
└── tests/                         # Unit tests
    └── Day4Tests.java             # JUnit tests (Unit + Consistency)
```

## Prerequisites

- **Java 21+**
- **Libraries**:
  - `junit-platform-console-standalone-1.10.2.jar`
  - `log4j-api-2.23.1.jar`
  - `log4j-core-2.23.1.jar`

## Usage

All commands should be run from the **root of the repository** (parent of `day4/`).

### 1. Compilation

Compile all source files.

```bash
javac --add-modules jdk.incubator.vector -cp ".:lib/*" -d . day4/*.java day4/tests/*.java
```

### 2. Running Solutions

**Part 1 (Sequential):**
```bash
java -cp ".:lib/*" day4.Day4Part1
```

**Part 1 (Optimized):**
```bash
java -cp ".:lib/*" day4.Day4Part1Optimized
```

**Part 2 (Sequential):**
```bash
java -cp ".:lib/*" day4.Day4Part2
```

**Part 2 (Optimized):**
```bash
java -cp ".:lib/*" day4.Day4Part2Optimized
```

**Part 1 (AI Optimized - Java 25+):**
```bash
java --add-modules jdk.incubator.vector --enable-native-access=ALL-UNNAMED -cp ".:lib/*" day4.Day4Part1AIOptimized
```

**Part 2 (AI Optimized - Java 25+):**
```bash
java --add-modules jdk.incubator.vector --enable-native-access=ALL-UNNAMED -cp ".:lib/*" day4.Day4Part2AIOptimized
```

**Part 1 (Virtual Thread):**
```bash
java -cp ".:lib/*" day4.Day4Part1VirtualThread
```

**Part 2 (Virtual Thread):**
```bash
java -cp ".:lib/*" day4.Day4Part2VirtualThread
```

### 3. Running Benchmarks

Compare the performance of the different implementations.

```bash
java --add-modules jdk.incubator.vector --enable-native-access=ALL-UNNAMED -cp ".:lib/*" day4.BenchmarkRunner
```

## Results

Benchmarks run on an 8-core machine (Apple Silicon):

| Implementation | Total Time | Average Time | Min Time | Max Time | Max Memory |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Part 1 (Sequential)** | 125.071 ms | 1.251 ms | 0.611 ms | 14.253 ms | 2.89 MB |
| **Part 1 (Optimized)** | 34.159 ms | 0.342 ms | 0.147 ms | 2.455 ms | 0.24 MB |
| **Part 1 (AI Optimized)** | **105.528 ms** | **1.055 ms** | **0.124 ms** | **3.934 ms** | **0.97 MB** |
| **Part 2 (Sequential)** | 698.487 ms | 6.985 ms | 6.216 ms | 10.220 ms | 6.48 MB |
| **Part 2 (Optimized)** | 147.266 ms | 1.473 ms | 1.330 ms | 2.156 ms | 0.31 MB |
| **Part 2 (AI Optimized)** | **382.490 ms** | **3.825 ms** | **2.187 ms** | **8.511 ms** | **1.35 MB** |

The **AI Optimized** solution for Part 2 leverages Java 25 features:
- **Foreign Memory API**: Off-heap memory storage for the grid to improve cache locality and reduce GC pressure.
- **Vector API**: SIMD instructions for parallel neighbor counting during the initial scan.
- **Bulk Synchronous Parallel (BSP)**: Parallel processing of candidate removal in rounds, ensuring consistency without heavy locking.
- **Virtual Threads**: Efficient task submission for parallel scanning.

### 4. Running Tests

Run the JUnit tests to verify the correctness of the solutions.

```bash
java --add-modules jdk.incubator.vector --enable-native-access=ALL-UNNAMED -cp ".:lib/*" org.junit.platform.console.ConsoleLauncher -c day4.tests.Day4Tests
```
