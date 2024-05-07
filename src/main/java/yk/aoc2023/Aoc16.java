package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.fastgeom.Vec2i;
import yk.ycollections.Tuple;
import yk.ycollections.YList;
import yk.ycollections.YSet;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.aoc2023.utils.AocUtils.stringToStrings;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashSet.hs;

@SuppressWarnings({"StringEquality", "SuspiciousNameCombination"})
public class Aoc16 {

    public static final String TEST_INPUT = ">|<<<\\....\n" +
            "|v-.\\^....\n" +
            ".v...|->>>\n" +
            ".v...v^.|.\n" +
            ".v...v^...\n" +
            ".v...v^..\\\n" +
            ".v../2\\\\..\n" +
            "<->-/vv|..\n" +
            ".|<<<2-|.\\\n" +
            ".v//.|.v..";

    @Test
    public void test1() {
        assertEquals(46, calc1(new Tuple<>(v2i(-1, 0), v2i(1, 0)), parseInput(TEST_INPUT)));
    }

    @Test
    public void test2() {
        assertEquals(51, calc2(parseInput(TEST_INPUT)));
    }

    @Test
    public void solution1() {
        assertEquals(6622, calc1(new Tuple<>(v2i(-1, 0), v2i(1, 0)), parseInput(readPuzzle("aoc16.txt"))));
    }

    @Test
    public void solution2() {
        assertEquals(7130, calc2(parseInput(readPuzzle("aoc16.txt"))));
    }

    private static int calc2(YList<YList<String>> data) {
        YList<Integer> numbers = al();
        for (int i = 0; i < data.size(); i++) {
            numbers.add(calc1(new Tuple<>(v2i(-1, i), v2i(1, 0)), data));
            numbers.add(calc1(new Tuple<>(v2i(data.size(), i), v2i(-1, 0)), data));
            numbers.add(calc1(new Tuple<>(v2i(i, -1), v2i(0, 1)), data));
            numbers.add(calc1(new Tuple<>(v2i(i, data.size()), v2i(0, -1)), data));
        }
        return numbers.max();
    }

    private static int calc1(Tuple<Vec2i, Vec2i> entryRay, YList<YList<String>> data) {
        YList<Tuple<Vec2i, Vec2i>> rays = al(entryRay);
        YSet<Tuple<Vec2i, Vec2i>> seenRays = hs();

        while(rays.notEmpty()) {
            rays = rays
                .map(ray -> new Tuple<>(ray.a.add(ray.b), ray.b))
                .filter(ray -> ray.a.x >= 0 && ray.a.y >= 0 && ray.a.x < data.first().size() && ray.a.y < data.size())
                .flatMap(ray -> {
                    switch(data.get(ray.a.y).get(ray.a.x)) {
                        case "\\": return al(new Tuple<>(ray.a, ray.b.x == 0 ? v2i(ray.b.y, 0) : v2i(0, ray.b.x)));
                        case "/": return al(new Tuple<>(ray.a, ray.b.x == 0 ? v2i(-ray.b.y, 0) : v2i(0, -ray.b.x)));
                        case "|": return ray.b.x == 0 ? al(ray)
                                    : al(new Tuple<>(ray.a, v2i(0, 1)), new Tuple<>(ray.a, v2i(0, -1)));
                        case "-": return ray.b.y == 0 ? al(ray)
                                    : al(new Tuple<>(ray.a, v2i(1, 0)), new Tuple<>(ray.a, v2i(-1, 0)));
                        default: return al(ray);
                    }
                })
                .filter(ray -> !seenRays.contains(ray))
                .forThis(rr -> seenRays.addAll(rr));
        }

        return seenRays.map(ray -> ray.a).size();
    }

    private static YList<YList<String>> parseInput(String input) {
        return al(input.split("\n")).map(l -> stringToStrings(l));
    }
}
