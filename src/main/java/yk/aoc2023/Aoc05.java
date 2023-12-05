package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.utils.IO;
import yk.ycollections.YList;

import static java.lang.Long.max;
import static java.lang.Long.min;
import static org.junit.Assert.assertEquals;
import static yk.ycollections.YArrayList.al;

public class Aoc05 {

    public static final String TEST_INPUT = "seeds: 79 14 55 13\n" +
            "\n" +
            "seed-to-soil map:\n" +
            "50 98 2\n" +
            "52 50 48\n" +
            "\n" +
            "soil-to-fertilizer map:\n" +
            "0 15 37\n" +
            "37 52 2\n" +
            "39 0 15\n" +
            "\n" +
            "fertilizer-to-water map:\n" +
            "49 53 8\n" +
            "0 11 42\n" +
            "42 0 7\n" +
            "57 7 4\n" +
            "\n" +
            "water-to-light map:\n" +
            "88 18 7\n" +
            "18 25 70\n" +
            "\n" +
            "light-to-temperature map:\n" +
            "45 77 23\n" +
            "81 45 19\n" +
            "68 64 13\n" +
            "\n" +
            "temperature-to-humidity map:\n" +
            "0 69 1\n" +
            "1 0 69\n" +
            "\n" +
            "humidity-to-location map:\n" +
            "60 56 37\n" +
            "56 93 4";

    @Test
    public void test1() {
        assertEquals(35, solution1(TEST_INPUT));
    }

    @Test
    public void test2() {
        assertEquals(46, solution2(TEST_INPUT));
    }

    @Test
    public void solution1() {
        assertEquals(662197086, solution1(IO.readFile("src/main/java/yk/aoc2023/aoc5.txt")));
    }

    @Test
    public void solution2() {
        assertEquals(52510809, solution2(IO.readFile("src/main/java/yk/aoc2023/aoc5.txt")));
    }

    public long solution1(String input) {
        YList<YList<YList<Long>>> maps = parseMaps(input);
        return parseSeeds(input)
                .map(seed -> maps
                    .reduce(seed, (res, mm) -> mm
                        .reduce(res, (res2, m) -> (res > m.get(1) && res < m.get(1) + m.get(2))
                            ? (res - m.get(1) + m.get(0))
                            : res2)))
                .min();
    }

    public long solution2(String input) {
        YList<YList<YList<Long>>> maps = parseMaps(input);
        return parseSeeds2(input)
                .flatMap(seed -> maps
                    .reduce(al(seed), (res, map) -> res
                        .flatMap(r -> map.map(m -> intersection(r, m)))
                        .filter(r -> r.get(1) >= r.get(0))))
                .flatMap(c -> c).min();
    }

    private static YList<Long> parseSeeds(String input) {
        return al(al(input.split("\n\n")).first().split(":")[1].trim().split(" ")).map(s -> Long.parseLong(s));
    }

    private static YList<YList<Long>> parseSeeds2(String input) {
        YList<Long> ss = parseSeeds(input);
        YList<YList<Long>> seeds = al();
        for (int i = 0; i < ss.size(); i += 2) seeds.add(al(ss.get(i), ss.get(i) + ss.get(i + 1) - 1));
        return seeds;
    }

    private static YList<YList<YList<Long>>> parseMaps(String input) {
        return ((YList<String>) al(input.split("\n\n"))).cdr()
            .map(m -> extend((YList)al(m.split("\n")).cdr()
                .map(line -> al(line.split(" "))
                    .map(s -> Long.parseLong(s)))
                    .sorted(m2 -> m2.get(1))));
    }

    public static YList<YList<Long>> extend(YList<YList<Long>> mm) {
        YList<YList<Long>> result = al();
        long last = 0;
        for (YList<Long> m : mm) {
            result.add(al(last, last, m.get(1) - last));
            result.add(m);
            last = m.get(1) + m.get(2);
        }
        result.add(al(last, last, Long.MAX_VALUE - last));
        return result.filter(r -> r.get(2) > 0);
    }

    public static YList<Long> intersection(YList<Long> range, YList<Long> mapping) {
        return al(max(range.get(0), mapping.get(1)),
                  min(range.get(1), mapping.get(1) + mapping.get(2) - 1))
                .map(r -> r - mapping.get(1) + mapping.get(0));
    }

}
