package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.utils.IO;
import yk.ycollections.YArrayList;
import yk.ycollections.YList;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.INT_ADD;
import static yk.ycollections.YArrayList.al;

public class Aoc04 {

    public static final String TEST_DATA = "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53\n" +
            "Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19\n" +
            "Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1\n" +
            "Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83\n" +
            "Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36\n" +
            "Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11";

    @Test
    public void test1() {
        assertEquals(13, calc(parseInput(TEST_DATA)));
    }

    @Test
    public void test2() {
        assertEquals(30, calc2(parseInput(TEST_DATA)));
    }

    @Test
    public void solution1() {
        assertEquals(24160, calc(parseInput(IO.readFile("src/main/java/yk/aoc2023/aoc4.txt"))));

    }

    @Test
    public void solution2() {
        assertEquals(5659035, calc2(parseInput(IO.readFile("src/main/java/yk/aoc2023/aoc4.txt"))));

    }

    private int calc(YList<YList<YList<Integer>>> input) {
        return input.map(c -> c.last()
                        .reduce(0, (res, cur) -> c.first().contains(cur) ? (res == 0 ? 1 : (res * 2)) : res))
                    .reduce(INT_ADD);
    }

    private static int calc2(YList<YList<YList<Integer>>> input) {
        YList<Integer> counts = YArrayList.allocate(input.size(), i -> 1);
        input.forWithIndex((i, card) ->  {
            int win = card.last().reduce(0, (res, cur) -> res + (card.first().contains(cur) ? 1 : 0));
            for (int j = i + 1; j < i + win + 1 && j < counts.size(); j++) counts.set(j, counts.get(j) + counts.get(i));
        });
        return counts.reduce(INT_ADD);
    }

    private static YList<YList<YList<Integer>>> parseInput(String s) {
        return al(s.split("\n"))
                .map(l -> al(l.split(":")[1].split("\\|"))
                    .map(nn -> al(nn.trim().split("\\s+"))
                        .map(n -> parseInt(n))));
    }

}
