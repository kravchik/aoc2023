package yk.aoc2023;

import org.junit.Test;
import yk.aoc2023.utils.AocUtils2D;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.XYit;
import yk.ycollections.YList;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.INT_ADD;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;

public class Aoc11 {

    public static final String TEST_INPUT =
            "...#......\n" +
            ".......#..\n" +
            "#.........\n" +
            "..........\n" +
            "......#...\n" +
            ".#........\n" +
            ".........#\n" +
            "..........\n" +
            ".......#..\n" +
            "#...#.....";

    @Test
    public void test1() {
        assertEquals(374, calc(TEST_INPUT));
    }

    @Test
    public void test2a() {
        assertEquals(1030, calc2(10, TEST_INPUT));
    }

    @Test
    public void test2b() {
        assertEquals(8410, calc2(100, TEST_INPUT));
    }

    @Test
    public void solution1() {
        assertEquals(9639160, calc(readPuzzle("aoc11.txt")));
    }

    @Test
    public void solution2() {
        assertEquals(752936133304L, calc2(1_000_000, readPuzzle("aoc11.txt")));
    }

    private static int calc(String input) {
        YList<YList<String>> data = AocUtils2D.parse2d(input).flatMap(s -> !s.contains("#") ? al(s, s) : al(s));
        data = AocUtils2D.transpose(AocUtils2D.transpose(data).flatMap(s -> !s.contains("#") ? al(s, s) : al(s)));
        YList<Vec2i> stars = data.mapWithIndex((y, l) -> l.mapWithIndex((x, s) -> s.equals("#") ? v2i(x, y) : null)).flatMap(l -> l).filter(p -> p != null);
        return stars.mapUniquePares((s1, s2) -> s1.sub(s2).abs().sum()).reduce(INT_ADD);
    }

    private static long calc2(int hubbleConstant, String input) {
        YList<YList<String>> data = AocUtils2D.parse2d(input);
        YList<Integer> vSpace = data
                .reduce(al(), (res, cur) -> res.with((res.isEmpty() ? 0 : res.last()) + (cur.contains("#") ? 0 : hubbleConstant - 1)));
        YList<Integer> hSpace = AocUtils2D.transpose(data)
                .reduce(al(), (res, cur) -> res.with(res.isEmpty() ? 0 : res.last() + (cur.contains("#") ? 0 : hubbleConstant - 1)));
        YList<Vec2i> stars = al();
        for (XYit xy : XYit.wh(data.first().size(), data.size())) {
            if (data.get(xy.y).get(xy.x).equals("#")) {
                stars.add(v2i(xy.x + hSpace.get(xy.x), xy.y + vSpace.get(xy.y)));
            }
        }
        return stars.mapUniquePares((s1, s2) -> (long)s1.sub(s2).abs().sum()).reduce((i, j) -> i + j);
    }
}
