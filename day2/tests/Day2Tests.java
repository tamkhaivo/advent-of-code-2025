package day2.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import day2.day2part1;
import day2.Day2Part1Optimized;
import day2.Day2Part1ExtraCredit;
import day2.Day2Part1VirtualThread;
import day2.day2part2;
import day2.Day2Part2Optimized;
import day2.Day2Part2ExtraCredit;
import day2.Day2Part2VirtualThread;

public class Day2Tests {

    // ==========================================
    // Part 1 Unit Tests
    // ==========================================

    @Test
    public void testIsInvalidID_SimplePalindrome() {
        // 1221 is a palindrome but NOT a repeated sequence -> VALID ID
        assertFalse(day2part1.isInvalidID(1221));
    }

    @Test
    public void testIsInvalidID_OddLength() {
        // 121 is a palindrome but odd length -> VALID ID (ignored by isInvalidID)
        assertFalse(day2part1.isInvalidID(121));
    }

    @Test
    public void testIsInvalidID_NotPalindrome() {
        // 1234 is not a palindrome -> VALID ID
        assertFalse(day2part1.isInvalidID(1234));
    }

    @Test
    public void testIsInvalidID_RepeatedSequence() {
        // 1212 is a repeated sequence (12 repeated twice) -> INVALID ID
        assertTrue(day2part1.isInvalidID(1212));
    }

    @Test
    public void testIsInvalidID_ExampleCases() {
        // 11 -> invalid
        assertTrue(day2part1.isInvalidID(11));
        // 22 -> invalid
        assertTrue(day2part1.isInvalidID(22));
        // 99 -> invalid
        assertTrue(day2part1.isInvalidID(99));
        // 1010 -> invalid
        assertTrue(day2part1.isInvalidID(1010));
        // 123123 -> invalid
        assertTrue(day2part1.isInvalidID(123123));
        // 100001 -> invalid
        assertFalse(day2part1.isInvalidID(100001));

        // 101 -> valid (odd length)
        assertFalse(day2part1.isInvalidID(101));
        // 123 -> valid (odd length)
        assertFalse(day2part1.isInvalidID(123));
    }

    @Test
    public void testIsInvalidID_ExtraCredit() {
        assertTrue(Day2Part1ExtraCredit.isInvalidID(1212));
        assertTrue(Day2Part1ExtraCredit.isInvalidID(99));
        assertTrue(Day2Part1ExtraCredit.isInvalidID(123123));

        assertFalse(Day2Part1ExtraCredit.isInvalidID(123)); // Odd length
        assertFalse(Day2Part1ExtraCredit.isInvalidID(1234)); // Even length, 12 != 34
        assertFalse(Day2Part1ExtraCredit.isInvalidID(10)); // Even length, 1 != 0
    }

    // ==========================================
    // Part 1 Optimized Unit Tests
    // ==========================================

    @Test
    public void testRangeContains() {
        Day2Part1Optimized.Range range = new Day2Part1Optimized.Range(10, 20);
        assertTrue(range.contains(10));
        assertTrue(range.contains(20));
        assertTrue(range.contains(15));
        assertFalse(range.contains(9));
        assertFalse(range.contains(21));
    }

    @Test
    public void testInvalidIDGenerator_SmallRange() {
        // Range 10-100.
        // Expected invalid IDs: 11, 22, ..., 99.
        Day2Part1Optimized.InvalidIDGenerator generator = new Day2Part1Optimized.InvalidIDGenerator.Builder()
                .withMin(10)
                .withMax(100)
                .build();

        List<Long> ids = generator.generate().boxed().collect(Collectors.toList());

        // 11, 22, 33, 44, 55, 66, 77, 88, 99 -> 9 items
        assertEquals(9, ids.size());
        assertTrue(ids.contains(11L));
        assertTrue(ids.contains(99L));
        assertFalse(ids.contains(10L));
        assertFalse(ids.contains(100L));
    }

    @Test
    public void testInvalidIDGenerator_LargeRange() {
        // Range 1000-2000.
        // Invalid IDs are 4 digits: XYXY.
        Day2Part1Optimized.InvalidIDGenerator generator = new Day2Part1Optimized.InvalidIDGenerator.Builder()
                .withMin(1000)
                .withMax(2000)
                .build();

        List<Long> ids = generator.generate().boxed().collect(Collectors.toList());

        // 1010, 1111, 1212, ..., 1919 -> 10 items
        assertEquals(10, ids.size());
        assertTrue(ids.contains(1010L));
        assertTrue(ids.contains(1919L));
        assertFalse(ids.contains(2020L));
    }

    @Test
    public void testInvalidIDGenerator_ExampleCase() {
        // 11-22 -> 11, 22
        // 95-115 -> 99, (1010 is > 115)
        // 998-1012 -> (999 is not invalid), 1010
        Day2Part1Optimized.InvalidIDGenerator generator = new Day2Part1Optimized.InvalidIDGenerator.Builder()
                .withMin(10)
                .withMax(1012)
                .build();

        List<Long> ids = generator.generate().boxed().collect(Collectors.toList());

        assertTrue(ids.contains(11L));
        assertTrue(ids.contains(22L));
        assertTrue(ids.contains(99L));
        assertTrue(ids.contains(1010L));

        boolean hasThreeDigit = ids.stream().anyMatch(id -> id >= 100 && id <= 999);
        assertFalse(hasThreeDigit, "Should not generate 3-digit IDs");
    }

    // ==========================================
    // Part 2 Optimized Unit Tests
    // ==========================================

    @Test
    public void testGenerator_SimpleRepetition() {
        // Test range [10, 100]
        Day2Part2Optimized.InvalidIDGenerator generator = new Day2Part2Optimized.InvalidIDGenerator.Builder()
                .withMin(10)
                .withMax(100)
                .build();

        Set<Long> generated = ConcurrentHashMap.newKeySet();
        generator.generateInParallel(generated::add);

        assertTrue(generated.contains(11L));
        assertTrue(generated.contains(99L));
        assertFalse(generated.contains(10L));
        assertFalse(generated.contains(100L));
    }

    @Test
    public void testGenerator_ComplexRepetition() {
        // Test range [1000, 2000]
        Day2Part2Optimized.InvalidIDGenerator generator = new Day2Part2Optimized.InvalidIDGenerator.Builder()
                .withMin(1000)
                .withMax(2000)
                .build();

        Set<Long> generated = ConcurrentHashMap.newKeySet();
        generator.generateInParallel(generated::add);

        assertTrue(generated.contains(1212L));
        assertTrue(generated.contains(1010L));
        assertFalse(generated.contains(1234L));
    }

    @Test
    public void testGenerator_TripleRepetition() {
        // Test range [100, 1000]
        Day2Part2Optimized.InvalidIDGenerator generator = new Day2Part2Optimized.InvalidIDGenerator.Builder()
                .withMin(100)
                .withMax(1000)
                .build();

        Set<Long> generated = ConcurrentHashMap.newKeySet();
        generator.generateInParallel(generated::add);

        assertTrue(generated.contains(111L)); // 1 repeated 3 times
        assertTrue(generated.contains(555L));
        assertFalse(generated.contains(1212L));
    }

    @Test
    public void testGenerator_Overlap() {
        // 1111 can be generated by seed 1 and seed 11
        Day2Part2Optimized.InvalidIDGenerator generator = new Day2Part2Optimized.InvalidIDGenerator.Builder()
                .withMin(1110)
                .withMax(1112)
                .build();

        Set<Long> generated = ConcurrentHashMap.newKeySet();
        generator.generateInParallel(generated::add);

        assertTrue(generated.contains(1111L));
        assertEquals(1, generated.size());
    }

    // ==========================================
    // Consistency Tests
    // ==========================================

    private static final String FILE_PATH = "day2/day2.txt";

    @Test
    public void testPart1Consistency() {
        long legacyResult = day2part1.solve(FILE_PATH);
        long optimizedResult = Day2Part1Optimized.solve(FILE_PATH);
        long extraCreditResult = Day2Part1ExtraCredit.solve(FILE_PATH);
        long virtualThreadResult = Day2Part1VirtualThread.solve(FILE_PATH);

        assertEquals(legacyResult, optimizedResult, "Legacy and Optimized solutions should match");
        assertEquals(legacyResult, extraCreditResult, "Legacy and Extra Credit solutions should match");
        assertEquals(legacyResult, virtualThreadResult, "Legacy and Virtual Thread solutions should match");
    }

    @Test
    public void testPart2Consistency() {
        long legacyResult = day2part2.solve(FILE_PATH);
        long optimizedResult = Day2Part2Optimized.solve(FILE_PATH);
        long extraCreditResult = Day2Part2ExtraCredit.solve(FILE_PATH);
        long virtualThreadResult = Day2Part2VirtualThread.solve(FILE_PATH);

        assertEquals(legacyResult, optimizedResult, "Legacy and Optimized solutions should match");
        assertEquals(legacyResult, extraCreditResult, "Legacy and Extra Credit solutions should match");
        assertEquals(legacyResult, virtualThreadResult, "Legacy and Virtual Thread solutions should match");
    }
}
