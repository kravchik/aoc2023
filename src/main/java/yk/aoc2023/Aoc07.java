package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.utils.IO;
import yk.ycollections.Tuple;
import yk.ycollections.YList;
import yk.ycollections.YMap;

import static java.lang.Integer.compare;
import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.stringToStrings;
import static yk.aoc2023.utils.IndexedBiFunction.indexed;
import static yk.ycollections.YArrayList.al;

public class Aoc07 {
    public static final String TEST_INPUT = "32T3K 765\n" +
            "T55J5 684\n" +
            "KK677 28\n" +
            "KTJJT 220\n" +
            "QQQJA 483";
    public static YList<String> ORDER = al("A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2").reverse();
    public static YList<String> ORDER_WITH_J = al("A", "K", "Q", "T", "9", "8", "7", "6", "5", "4", "3", "2", "J").reverse();

    @Test
    public void test1() {
        assertEquals(6440, calc(true, TEST_INPUT));
    }

    @Test
    public void test2() {
        assertEquals(5905, calc(false, TEST_INPUT));
    }

    @Test
    public void solution1() {
        assertEquals(248179786, calc(true, IO.readFile("src/main/java/yk/aoc2023/aoc7.txt")));
    }

    @Test
    public void solution2() {
        assertEquals(247885995, calc(false, IO.readFile("src/main/java/yk/aoc2023/aoc7.txt")));
    }

    private int calc(boolean part1, String input) {
        return al(input.split("\n"))
                .map(l -> al(l.split(" ")))
                .map(l -> new Tuple<>(
                    stringToStrings(l.first()).mapThis(hand -> new Tuple<>(
                        hand.map(card -> 'A' + (part1 ? ORDER : ORDER_WITH_J)
                                .indexOf(card))
                                .reduce("", (res, c) -> res + (char)(int)c),
                        (part1 ? calcType(hand) : calcType2(hand)))),
                    parseInt(l.last())))
                .sorted((o1, o2) -> o1.a.b == o2.a.b ? o1.a.a.compareTo(o2.a.a) : compare(o1.a.b, o2.a.b))
                .reduce(0, indexed((i, res, cur) -> res + (i + 1) * cur.b));
    }

    private int calcType(YList<String> hand) {
        YMap<String, YList<String>> groups = hand.groupBy(s -> s);
        return groups.size() * -10 + groups.values().map(v -> v.size()).sorted().last();
    }

    private int calcType2(YList<String> hand) {
        return hand.toSet().map(card -> calcType(hand.map(c -> c.equals("J") ? card : c))).max();
    }
}
