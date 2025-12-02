package day2.tests;

import day2.Day2Part1ExtraCredit;
import day2.day2part1;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Day2Tests {

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
        // Wait, the logic for Part 1 was just "palindrome" in the user's initial code,
        // but the prompt said "sequence of digits repeated twice".
        // Let's re-read the prompt carefully.
        // "Invalid IDs by looking for any ID which is made only of some sequence of
        // digits repeated twice."
        // So 1212 IS invalid.
        // My previous fix implemented:
        // String s = Long.toString(ID);
        // if (s.length() % 2 != 0) return false;
        // String firstHalf = s.substring(0, s.length() / 2);
        // String secondHalf = s.substring(s.length() / 2);
        // return firstHalf.equals(secondHalf);

        // So 1212 -> first=12, second=12 -> true.
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
        // Valid cases (should return true for "isInvalidID" based on problem
        // description)
        // Example: 1212 -> length 4, 12 == 12 -> true
        assertTrue(Day2Part1ExtraCredit.isInvalidID(1212));
        assertTrue(Day2Part1ExtraCredit.isInvalidID(99));
        assertTrue(Day2Part1ExtraCredit.isInvalidID(123123));

        // Invalid cases (should return false)
        assertFalse(Day2Part1ExtraCredit.isInvalidID(123)); // Odd length
        assertFalse(Day2Part1ExtraCredit.isInvalidID(1234)); // Even length, 12 != 34
        assertFalse(Day2Part1ExtraCredit.isInvalidID(10)); // Even length, 1 != 0
    }
}
