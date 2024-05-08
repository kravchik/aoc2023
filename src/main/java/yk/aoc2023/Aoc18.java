package yk.aoc2023;

import org.junit.Test;
import yk.aoc2023.utils.AocUtils2D;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;
import yk.jcommon.utils.Ptr;
import yk.ycollections.*;

import java.util.Comparator;
import java.util.Map;

import static java.lang.Integer.*;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.INT_ADD;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.aoc2023.utils.Indexers.indexedPredicate;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;

public class Aoc18 {

    public static final YMap<String, Vec2i> LETTER_TO_DIR = hm(
            "U", v2i(0, -1),
            "D", v2i(0, 1),
            "L", v2i(-1, 0),
            "R", v2i(1, 0)
    );
    public static final YList<String> DIRS = al(
            "R",
            "D",
            "L",
            "U"
    );
    public static final String TEST_INPUT = "R 6 (#70c710)\n" +
            "D 5 (#0dc571)\n" +
            "L 2 (#5713f0)\n" +
            "D 2 (#d2c081)\n" +
            "R 2 (#59c680)\n" +
            "D 2 (#411b91)\n" +
            "L 5 (#8ceee2)\n" +
            "U 2 (#caa173)\n" +
            "L 1 (#1b58a2)\n" +
            "U 2 (#caa171)\n" +
            "R 2 (#7807d2)\n" +
            "U 3 (#a77fa3)\n" +
            "L 2 (#015232)\n" +
            "U 2 (#7a21e3)";

    @Test
    public void test1() {
        assertEquals(62, calcArea(makeSteps(instructions1(TEST_INPUT))));
    }

    @Test
    public void test2() {
        assertEquals(952408144115L, calcArea(makeSteps(instructions2(TEST_INPUT))));
    }

    @Test
    public void solution2() {
        assertEquals(131265059885080L, calcArea(makeSteps(instructions2(readPuzzle("aoc18.txt")))));
    }

    @Test
    public void solution1() {
        assertEquals(70253, calcArea(makeSteps(instructions1(readPuzzle("aoc18.txt")))));
    }

    public static YList<YList<Vec2i>> makeSteps(YList<Tuple<String, Integer>> instructions) {
        Vec2i pos = v2i(0, 0);
        YList<YList<Vec2i>> result = al();
        for (Tuple<String, Integer> d : instructions) {
            result.add(al(pos, pos = pos.add(LETTER_TO_DIR.get(d.a).mul(d.b))));
        }
        return result;
    }

    public static long calcArea(YList<YList<Vec2i>> lines) {
        lines = lines
                .filter(l -> l.first().y == l.last().y)
                .map(l -> l.sorted(Comparator
                    .comparingInt((Vec2i o) -> o.y)
                    .thenComparingInt(o -> o.x)))
                .sorted(l -> l.first().y);
        
        Ptr<Long> sum = new Ptr<>(0L);

        YList<YList<Integer>> ranges = al();
        Ptr<Integer> lastY = new Ptr<>(0);

        YMap<Integer, YList<YList<Integer>>> grouped = lines
                .groupBy(l -> l.first().y)
                .mapValues(ll -> ll.map(l -> al(l.first().x, l.last().x)));
        for (Map.Entry<Integer, YList<YList<Integer>>> entry : grouped.entrySet()) {
            Integer y = entry.getKey();
            YList<YList<Integer>> line = entry.getValue();
            ranges.forEachFun(r -> sum.value += (long) (r.last() - r.first() + 1) * (y - lastY.value + 1));
            YList<YList<Integer>> newRanges = rangeXor(ranges, line);
            YList<YList<Integer>> rangeSubs = rangeAnd(ranges, newRanges);
            sum.value -= rangeSubs.map(r -> r.last() - r.first() + 1).reduce(0, INT_ADD);
            ranges = newRanges;
            lastY.value = y;
        }

        return sum.value;
    }

    public static boolean intersects(YList<Integer> a, YList<Integer> b) {
        return a.first() <= b.last() && a.last() >= b.first();
    }

    public static YList<YList<Integer>> rangeSub(YList<YList<Integer>> aa, YList<YList<Integer>> bb) {
        return aa.flatMap(a -> bb.reduce(al(a), (res, b) -> (YArrayList)res.flatMap(r -> rangeSubOne(r, b))));
    }
    
    public static YList<YList<Integer>> rangeSubOne(YList<Integer> a, YList<Integer> b) {
        if (!intersects(a, b)) return al(a);
        return (YList)al(al(a.first(), b.first() - 1), al(b.last() + 1, a.last())).filter(r -> r.first() <= r.last());
    }

    public static YList<YList<Integer>> rangeAnd(YList<YList<Integer>> aa, YList<YList<Integer>> bb) {
        return aa.flatMap(a -> bb.flatMap(b -> rangeAndOne(a, b)));
    }

    public static YList<YList<Integer>> rangeAndOne(YList<Integer> a, YList<Integer> b) {
        if (!intersects(a, b)) return al();
        return (YList)al(al(max(a.first(), b.first()), min(a.last(), b.last()))).filter(r -> r.first() <= r.last());
    }

    //TODO rename to inclusive
    public static YList<YList<Integer>> rangeXor(YList<YList<Integer>> a, YList<YList<Integer>> b) {
        return a.withAll(b)
                .flatMap(pp -> pp)
                .groupBy(i -> i).filter((k, v) -> {
                    if (v.size() == 0 || v.size() > 2) throw new RuntimeException("Unexpected group size: " + v.size());
                    return v.size() == 1;
                }).values().flatMap(ll -> ll).toList()
                .sorted()
                .mapAdj(false, (left, right) -> (YList<Integer>) al(left, right))
                .filter(indexedPredicate((i, v) -> i % 2 == 0));
    }

    private static YList<Tuple<String, Integer>> instructions1(String input) {
        return AocUtils2D.parse2d("\n", " ", input)
                .map(l -> new Tuple<>(l.get(0), parseInt(l.get(1))));
    }

    private static YList<Tuple<String, Integer>> instructions2(String input) {
        return AocUtils2D.parse2d("\n", " ", input)
                .map(l -> l.get(2))
                .map(l -> l.substring(2, l.length() - 1))
                .map(l -> new Tuple<>(
                        DIRS.get(parseInt(l.substring(5))),
                        Integer.parseInt(l.substring(0, 5), 16)))
                .forThis(ii -> System.out.println(ii.toString()));
    }

    //TODO max by natural order
    public static void printTrace(YMap<Integer, YMap<Integer, YSet<Vec2i>>> traces, YSet<Vec2i> insides) {
        int width = traces.values().map(vv -> vv.keySet().max()).max() + 1;
        int left = -traces.values().map(vv -> vv.keySet().min()).min();
        YMap<Integer, YList<String>> res = traces.sortedBy((k, v) -> k).mapValues(l -> {
            YList<String> ll = YArrayList.allocate(width + left, i -> ".");
            for (Map.Entry<Integer, YSet<Vec2i>> entry : l.entrySet()) {
                ll.set(entry.getKey() + left, "*");
            }
            return ll;
        });
        System.out.println(insides);
        for (Vec2i i : insides) {
            if (res.get(i.y).get(i.x + left).equals(".")) {
                res.get(i.y).set(i.x + left, "+");
            }
        }
        IO.writeFile("aoc18.debug.txt", res.mapValues(l -> l.toString("")).toString("\n"));
        //System.out.println(res.toString("\n"));
    }
}
