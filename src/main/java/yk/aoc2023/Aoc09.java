package yk.aoc2023;

import org.junit.Test;
import yk.ycollections.YList;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.*;
import static yk.ycollections.YArrayList.al;

public class Aoc09 {

    public static final String TEST_INPUT =
            "0 3 6 9 12 15\n" +
            "1 3 6 10 15 21\n" +
            "10 13 16 21 30 45";

    @Test
    public void test1() {
        assertEquals(114, calc(TEST_INPUT));
    }

    @Test
    public void test2() {
        assertEquals(2, calc2(TEST_INPUT));
    }

    @Test
    public void solution1() {
        assertEquals(1921197370, calc(readFile("aoc09.txt")));
    }

    @Test
    public void solution2() {
        assertEquals(1124, calc2(readFile("aoc09.txt")));
    }

    public int calc(String input) {
        return al(input.split("\n"))
                .map(l3 -> (YList<Integer>)al(l3.split(" ")).map(s -> parseInt(s)))
                .map(line -> generate(line, l11 -> l11.size() < 2 ? null
                    : l11.zipWith(l11.cdr(), (l1, l2) -> l2 - l1)).map(l -> l.last()).reduce(INT_ADD))
                .reduce(INT_ADD);

    }

    public int calc2(String input) {
        return al(input.split("\n"))
                .map(l3 -> (YList<Integer>)al(l3.split(" ")).map(s -> parseInt(s)))
                .map(line -> generate(line, l11 -> l11.size() < 2 ? null
                                : l11.zipWith(l11.cdr(), (l1, l2) -> l2 - l1))
                        .map(l -> l.first())
                        .reverse().reduce((i, j) -> j - i))
                .reduce(INT_ADD);
    }
}
