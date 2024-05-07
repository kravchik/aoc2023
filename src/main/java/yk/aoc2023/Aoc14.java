package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.XYit;
import yk.ycollections.YList;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.*;
import static yk.aoc2023.utils.Indexers.indexed;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YArrayList.toYList;

@SuppressWarnings("StringEquality")
public class Aoc14 {
    public static final String TEST_INIT =
            "O....#....\n" +
            "O.OO#....#\n" +
            ".....##...\n" +
            "OO.#O....O\n" +
            ".O.....O#.\n" +
            "O.#..O.#.#\n" +
            "..O..#O..O\n" +
            ".......O..\n" +
            "#....###..\n" +
            "#OO..#....";

    public static final String TEST_SLIDE =
            "OOOO.#.O..\n" +
            "OO..#....#\n" +
            "OO..O##..O\n" +
            "O..#.OO...\n" +
            "........#.\n" +
            "..#....#.#\n" +
            "..O..#.O.O\n" +
            "..O.......\n" +
            "#....###..\n" +
            "#....#....";

    public static final String TEST_CYCLE1 =
            ".....#....\n" +
            "....#...O#\n" +
            "...OO##...\n" +
            ".OO#......\n" +
            ".....OOO#.\n" +
            ".O#...O#.#\n" +
            "....O#....\n" +
            "......OOOO\n" +
            "#...O###..\n" +
            "#..OO#....";

    public static final String TEST_CYCLE2 =
            ".....#....\n" +
            "....#...O#\n" +
            ".....##...\n" +
            "..O#......\n" +
            ".....OOO#.\n" +
            ".O#...O#.#\n" +
            "....O#...O\n" +
            ".......OOO\n" +
            "#..OO###..\n" +
            "#.OOO#...O";

    public static final String TEST_CYCLE3 =
            ".....#....\n" +
            "....#...O#\n" +
            ".....##...\n" +
            "..O#......\n" +
            ".....OOO#.\n" +
            ".O#...O#.#\n" +
            "....O#...O\n" +
            ".......OOO\n" +
            "#...O###.O\n" +
            "#.OOO#...O";


    @Test
    public void test1() {
        assertEquals(parseInput(TEST_SLIDE), slide(parseInput(TEST_INIT)));
        assertEquals(136, calc(parseInput(TEST_SLIDE)));
    }

    private YList<YList<String>> cycle(YList<YList<String>> input) {
        input = slide(v2i(0, 0), v2i(1, 0), input);
        input = slide(v2i(0, 1), v2i(1, 0).rot_90(), input);
        input = slide(v2i(1, 1), v2i(1, 0).rot_90().rot_90(), input);
        input = slide(v2i(1, 0), v2i(1, 0).rot90(), input);
        return input;
    }

    @Test
    public void testSlideRight() {
        slide(v2i(0, 1), v2i(1, 0).rot90().rot90().rot90(), parseInput(TEST_INIT));
    }

    @Test
    public void test2a() {
        YList<YList<String>> c = cycle(parseInput(TEST_INIT));
        assertEquals(parseInput(TEST_CYCLE1), c);
        c = cycle(c);
        assertEquals(parseInput(TEST_CYCLE2), c);
        c = cycle(c);
        assertEquals(parseInput(TEST_CYCLE3), c);
    }

    @Test
    public void test2b() {
        final YList<YList<String>>[] c = new YList[]{parseInput(TEST_INIT)};
        CycleState<YList<YList<String>>> cycles = findCycleOnUniqueState(() -> {
            YList<YList<String>> res = c[0];
            c[0] = cycle(c[0]);
            return res;
        });
        assertEquals(64, calc(cycles.findStateAt(1000000000)));
    }

    @Test
    public void solution2() {
        final YList<YList<String>>[] c = new YList[]{parseInput(readPuzzle("aoc14.txt"))};
        CycleState<YList<YList<String>>> cycles = findCycleOnUniqueState(() -> {
            YList<YList<String>> res = c[0];
            c[0] = cycle(c[0]);
            return res;
        });
        assertEquals(96317, calc(cycles.findStateAt(1000000000)));
    }

    @Test
    public void solution1() {
        assertEquals(107430, calc(slide(parseInput(readPuzzle("aoc14.txt")))));
    }

    private static YList<YList<String>> slide(YList<YList<String>> map) {
        return slide(v2i(0, 0), v2i(1, 0), map);
    }

    private static YList<YList<String>> slide(Vec2i disp, Vec2i rot, YList<YList<String>> map) {
        map = map.map(l -> toYList(l));
        Vec2i dd = disp.mul(v2i(map.first().size() - 1, map.size() - 1));
        for (int i = 0; i < map.size(); i++) {
            for (XYit xy : XYit.wh(map.first().size(), map.size() - 1)) {
                Vec2i curPos = xy.getVec2i().cMul(rot).add(dd);
                Vec2i nextPos = xy.getVec2i().add(0, 1).cMul(rot).add(dd);
                if (map.get(curPos.y).get(curPos.x) == "." && map.get(nextPos.y).get(nextPos.x) == "O") {
                    map.get(curPos.y).set(curPos.x, "O");
                    map.get(nextPos.y).set(nextPos.x, ".");
                }
            }
        }
        return map;
    }

    private static int calc(YList<YList<String>> map) {
        return map.reduce(0, indexed((int i, Integer res, YList<String> line) -> res + line.reduce(0,
                (r, s) -> r + (s == "O" ? (map.size() - i) : 0))));
    }

    private static YList<YList<String>> parseInput(String input) {
        return al(input.split("\n")).map(l -> stringToStrings(l));
    }
}
