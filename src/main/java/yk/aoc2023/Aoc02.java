package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.utils.IO;
import yk.ycollections.YList;
import yk.ycollections.YMap;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.INT_ADD;
import static yk.aoc2023.utils.AocUtils.INT_MUL;
import static yk.aoc2023.utils.IndexedFunction.indexed;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;

public class Aoc02 {

    public static final String TEST_DATA = "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green\n" +
            "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue\n" +
            "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red\n" +
            "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red\n" +
            "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green";

    @Test
    public void test1() {
        assertEquals(8, solve(hm("red", 12, "green", 13, "blue", 14), parseInput(TEST_DATA)));
    }

    @Test
    public void test2() {
        assertEquals(2286, solve2(parseInput(TEST_DATA)));
    }

    @Test
    public void solution1() {
        assertEquals(2237, solve(hm("red", 12, "green", 13, "blue", 14),
                parseInput(IO.readFile("src/main/java/yk/aoc2023/aoc2.txt"))));
    }

    public static int solve(YMap<String, Integer> max, YList<YList<YMap<String, Integer>>> input) {
        return input
                .map(indexed((i, round) -> round.isAll(s -> s.isAll((k, v) -> max.get(k) >= v)) ? i + 1 : 0))
                .reduce(INT_ADD);
    }

    @Test
    public void solution2() {
        assertEquals(66681, solve2(parseInput(IO.readFile("src/main/java/yk/aoc2023/aoc2.txt"))));
    }

    public static int solve2(YList<YList<YMap<String, Integer>>> input) {
        return input.map(game -> game
                        .reduce((res, cur) -> res.keySet().withAll(cur.keySet())
                            .toMapKeys(k -> max(res.getOr(k, 0), cur.getOr(k, 0))))
                        .values()
                        .reduce(INT_MUL))
                    .reduce(INT_ADD);
    }

    private static YList<YList<YMap<String, Integer>>> parseInput(String data) {
        return al(data.split("\n"))
                .map(game -> game.split(":")[1])
                .map(game -> al(game.split(";")))
                .map(round -> round
                        .map(step -> al(step.split(","))
                                .map(cube -> cube.trim().split(" "))
                                .toMap(kv -> kv[1], kv -> parseInt(kv[0])))
                );
    }
}
