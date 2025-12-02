package day2.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import day2.Day2Part1Optimized.InvalidIDGenerator;
import day2.Day2Part1Optimized.Range;

public class Day2Part1OptimizedTests {

    @Test
    public void testRangeContains() {
        Range range = new Range(10, 20);
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
        InvalidIDGenerator generator = new InvalidIDGenerator.Builder()
                .withMin(10)
                .withMax(100)
                .build();

        List<Long> ids = generator.generate().boxed().collect(Collectors.toList());

        // 11, 22, 33, 44, 55, 66, 77, 88, 99 -> 9 items
        assertEquals(9, ids.size());
        assertTrue(ids.contains(11L));
        assertTrue(ids.contains(99L));
        assertFalse(ids.contains(10L)); // 10 is not invalid (1 repeated is 11)
        assertFalse(ids.contains(100L));
    }

    @Test
    public void testInvalidIDGenerator_LargeRange() {
        // Range 1000-2000.
        // Invalid IDs are 4 digits: XYXY.
        // 1010, 1111, 1212, ..., 1919.
        // 2020 is outside.

        InvalidIDGenerator generator = new InvalidIDGenerator.Builder()
                .withMin(1000)
                .withMax(2000)
                .build();

        List<Long> ids = generator.generate().boxed().collect(Collectors.toList());

        // 1010, 1111, 1212, 1313, 1414, 1515, 1616, 1717, 1818, 1919 -> 10 items
        assertEquals(10, ids.size());
        assertTrue(ids.contains(1010L));
        assertTrue(ids.contains(1919L));
        assertFalse(ids.contains(2020L));
    }

    @Test
    public void testInvalidIDGenerator_ExampleCase() {
        // Example from prompt:
        // 11-22 -> 11, 22
        // 95-115 -> 99, (1010 is > 115)
        // 998-1012 -> (999 is not invalid), 1010

        // Let's test the generator for a range covering all these: 10 to 1012
        InvalidIDGenerator generator = new InvalidIDGenerator.Builder()
                .withMin(10)
                .withMax(1012)
                .build();

        List<Long> ids = generator.generate().boxed().collect(Collectors.toList());

        assertTrue(ids.contains(11L));
        assertTrue(ids.contains(22L));
        assertTrue(ids.contains(99L));
        assertTrue(ids.contains(1010L));

        // Ensure no 3-digit numbers are generated (since they can't be "repeated twice"
        // as length is odd)
        boolean hasThreeDigit = ids.stream().anyMatch(id -> id >= 100 && id <= 999);
        assertFalse(hasThreeDigit, "Should not generate 3-digit IDs");
    }
}
