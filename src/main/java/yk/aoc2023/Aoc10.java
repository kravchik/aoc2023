package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.XYit;
import yk.ycollections.YList;
import yk.ycollections.YMap;
import yk.ycollections.YSet;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.*;
import static yk.aoc2023.utils.Indexers.indexed;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;
import static yk.ycollections.YHashSet.hs;

public class Aoc10 {

    public static final YMap<String, String> BETTER_SYMBOLS = hm(
            "|", "|",
            "-", "-",
            "L", "╰",
            "J", "╯",
            "7", "╮",
            "F", "╭",
            ".", ".",
            "S", "S"
    );

    public static final YMap<String, YList<Vec2i>> LAYOUT = hm(
            "|", al(v2i(0, -1), v2i(0, 1)),
            "-", al(v2i(-1, 0), v2i(1, 0)),
            "╰", al(v2i(0, -1), v2i(1, 0)),
            "╯", al(v2i(-1, 0), v2i(0, -1)),
            "╮", al(v2i(-1, 0), v2i(0, 1)),
            "╭", al(v2i(1, 0), v2i(0, 1)),
            "S", al(v2i(1, 0), v2i(-1, 0), v2i(0, 1), v2i(0, -1))
    );

    public static final YList<Vec2i> STEPS = al(v2i(1, 0), v2i(-1, 0), v2i(0, 1), v2i(0, -1));

    @Test
    public void test1() {
        assertEquals(8, calc(
                "7-F7-\n" +
                ".FJ|7\n" +
                "SJLL7\n" +
                "|F--J\n" +
                "LJ.LJ"));
    }

    @Test
    public void test2a() {
        assertEquals(4, calc2(
                "...........\n" +
                ".S-------7.\n" +
                ".|F-----7|.\n" +
                ".||.....||.\n" +
                ".||.....||.\n" +
                ".|L-7.F-J|.\n" +
                ".|..|.|..|.\n" +
                ".L--J.L--J.\n" +
                "..........."
        ));
    }

    @Test
    public void test2b() {
        assertEquals(4, calc2(
                "..........\n" +
                ".S------7.\n" +
                ".|F----7|.\n" +
                ".||OOOO||.\n" +
                ".||OOOO||.\n" +
                ".|L-7F-J|.\n" +
                ".|II||II|.\n" +
                ".L--JL--J.\n" +
                ".........."
        ));
    }

    @Test
    public void test2c() {
        assertEquals(8, calc2(
                ".F----7F7F7F7F-7....\n" +
                ".|F--7||||||||FJ....\n" +
                ".||.FJ||||||||L7....\n" +
                "FJL7L7LJLJ||LJ.L-7..\n" +
                "L--J.L7...LJS7F-7L7.\n" +
                "....F-J..F7FJ|L7L7L7\n" +
                "....L7.F7||L7|.L7L7|\n" +
                ".....|FJLJ|FJ|F7|.LJ\n" +
                "....FJL-7.||.||||...\n" +
                "....L---J.LJ.LJLJ..."
        ));
    }

    @Test
    public void test2d() {
        assertEquals(10, calc2(
                "FF7FSF7F7F7F7F7F---7\n" +
                "L|LJ||||||||||||F--J\n" +
                "FL-7LJLJ||||||LJL-77\n" +
                "F--JF--7||LJLJ7F7FJ-\n" +
                "L---JF-JLJ.||-FJLJJ7\n" +
                "|F|F-JF---7F7-L7L|7|\n" +
                "|FFJF7L7F-JF7|JL---7\n" +
                "7-L-JL7||F7|L7F-7F7|\n" +
                "L.L7LFJ|||||FJL7||LJ\n" +
                "L7JLJL-JLJLJL--JLJ.L"
        ));
    }

    @Test
    public void solution1() {
        assertEquals(7173, calc(readPuzzle("aoc10.txt")));
    }

    @Test
    public void solution2() {
        assertEquals(291, calc2(readPuzzle("aoc10.txt")));
    }

    public int calc(String input) {
        YList<YList<String>> map = parseMap(input);
        Vec2i start = map.map(l -> l.indexOf("S")).reduce(null, indexed((y, res, x) -> x > -1 ? v2i(x, y) : res));
        YList<Vec2i> cycle = calcCycle(start, map);
        return cycle.size() / 2;
    }

    public int calc2(String input) {
        YList<YList<String>> map = parseMap(input);
        Vec2i start = map.map(l -> l.indexOf("S")).reduce(null, indexed((y, res, x) -> x > -1 ? v2i(x, y) : res));
        YSet<Vec2i> allPositions = hs();
        for (XYit xy : XYit.wh(map.first().size()*2+2, map.size()*2+2)) allPositions.add(v2i(xy.x-1, xy.y-1));
        allPositions.removeAll(growCycle(map, calcCycle(start, map)).toSet());

        YList<Vec2i> edge = al(v2i(-1, 0));
        while(edge.notEmpty()) {
            edge = edge.mapThis(e -> e.flatMap(cur -> STEPS
                .map(s -> cur.add(s))
                .filter(p -> !e.contains(p))
                .filter(p -> allPositions.contains(p))
                .forThis(newEdge -> allPositions.removeAll(newEdge))));
        }
        return allPositions.filter(p -> p.div(2).mul(2).equals(p)).size();
    }

    private YList<Vec2i> calcCycle(Vec2i start, YList<YList<String>> map) {
        return generate2(start, soFar ->
                LAYOUT.get(getAt(map, soFar.last()))
                .map(d -> soFar.last().add(d))
                .filter(p -> !soFar.contains(p))
                .find(p -> LAYOUT.get(getAt(map, p)).isAny(ld -> ld.equals(soFar.last().sub(p)))));
    }

    public YList<Vec2i> growCycle(YList<YList<String>> map, YList<Vec2i> cycle) {
        return cycle.mapThis(result -> result.map(p -> p.mul(2))
                .withAll(result.filter(p -> LAYOUT.get(getAt(map, p)).containsAny(v2i(1, 0))).map(p -> p.mul(2).add(1, 0)))
                .withAll(result.filter(p -> LAYOUT.get(getAt(map, p)).containsAny(v2i(0, 1))).map(p -> p.mul(2).add(0, 1))));
    }

    public String getAt(YList<YList<String>> map, Vec2i pos) {
        return map.get(pos.y).get(pos.x);
    }

    private static YList<YList<String>> parseMap(String input) {
        for (Map.Entry<String, String> e : BETTER_SYMBOLS.entrySet()) {
            input = input.replace(e.getKey(), e.getValue());
        }
        return al(input.split("\n")).map(l -> stringToStrings(l));
    }
}
