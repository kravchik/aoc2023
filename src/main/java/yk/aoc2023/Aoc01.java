package yk.aoc2023;

import org.junit.Test;
import yk.ycollections.YMap;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.*;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;

/**
 * Created by yuri at 2022.12.01
 */
public class Aoc01 {
    public static final YMap<String, String> WORD_TO_DIGIT = hm(
            "one", "on1e",
            "two", "tw2o",
            "three", "th3ree",
            "four", "fo4ur",
            "five", "fi5ve",
            "six", "si6x",
            "seven", "sev7en",
            "eight", "ei8ght",
            "nine","ni9ne"
    );

    @Test
    public void test1() {
        assertEquals(142, solve(
                "1abc2\n" +
                "pqr3stu8vwx\n" +
                "a1b2c3d4e5f\n" +
                "treb7uchet"));
    }

    @Test
    public void test2() {
        assertEquals(281, solve2(
                "two1nine\n" +
                "eightwothree\n" +
                "abcone2threexyz\n" +
                "xtwone3four\n" +
                "4nineeightseven2\n" +
                "zoneight234\n" +
                "7pqrstsixteen"));
    }

    @Test
    public void solution1() {
        assertEquals(56465, solve(readFile("aoc01.txt")));
    }

    @Test
    public void solution2() {
        assertEquals(55902, solve2(readFile("aoc01.txt")));
    }

    public int solve(String input) {
        return al(input.split("\n"))
                .map(l1 -> stringToCharacters(l1))
                .map(l -> l.filter(c -> DIGITS.contains(c)))
                .map(l -> "" + l.first() + l.last())
                .map(n -> parseInt(n))
                .reduce(INT_ADD);

    }

    public int solve2(String input) {
        return al(input.split("\n"))
                .map(l -> WORD_TO_DIGIT.keySet()
                        .reduce(l, (r, w) -> r.replaceAll(w, WORD_TO_DIGIT.get(w))))
                .map(l1 -> stringToCharacters(l1))
                .map(l -> l.filter(c -> DIGITS.contains(c)))
                .map(l -> "" + l.first() + l.last())
                .map(n -> parseInt(n))
                .reduce(INT_ADD);

    }
}
