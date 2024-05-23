package yk.aoc2023;

import org.junit.Test;
import yk.aoc2023.utils.AocUtils2D;
import yk.aoc2023.utils.Progression;
import yk.jcommon.fastgeom.Vec2i;
import yk.ycollections.YArrayList;
import yk.ycollections.YList;
import yk.ycollections.YSet;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.aoc2023.utils.AocUtils2D.*;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashSet.hs;

public class Aoc21 {

    public static final String TEST_INPUT = "...........\n" +
        ".....###.#.\n" +
        ".###.##..#.\n" +
        "..#.#...#..\n" +
        "....#.#....\n" +
        ".##..S####.\n" +
        ".##..#...#.\n" +
        ".......##..\n" +
        ".##.#.####.\n" +
        ".##..##.##.\n" +
        "...........";
    public static final YArrayList<String> PASSABLE = al(".", "S");

    @Test
    public void test1() {
        assertEquals(16, calc2(6, TEST_INPUT, true));
    }

    @Test
    public void test2() {
        assertEquals(1594, calc2(50, TEST_INPUT, true));
        assertEquals(6536, calc2(100, TEST_INPUT, true));
        assertEquals(167004, calc2(500, TEST_INPUT, true));
    }

    @Test
    public void solution1() {
        String input = readPuzzle("aoc21.txt");
        assertEquals(3758, calc2(64, input, false));
    }

    @Test
    public void solution2() {
        //how many garden plots could the Elf reach in exactly steps steps?
        long steps = 26501365L;
        YList<YList<String>> map = parse2d(readPuzzle("aoc21.txt"));
        YList<Long> numbers = calc2b(500, true, map);
        Progression.Result progression = Progression.results(numbers)
            .filter(r -> (steps - r.offset) % r.period == 0)
            .first();
        assertEquals(new Progression.Result(map.first().size() / 2, map.first().size(), al(3848L, 30462L, 30372L)),
                     progression);
        assertEquals(621494544278648L, Progression.doubleProgression(
                (steps - progression.offset) / progression.period,
                progression.difs.get(0),
                progression.difs.get(1),
                progression.difs.get(2)));
    }

    /**
     * How many garden plots could the Elf reach in exactly 'steps' steps?
     *
     * Naive approach. Simple water spilling.
     */
    private static int calc1(int steps, String input) {
        YList<YList<String>> map = AocUtils2D.parse2d(input);
        YSet<Vec2i> edge = hs(AocUtils2D.find(map, "S"));
        for (int i = 0; i < steps; i++) {
            edge = edge.flatMap(e -> DIRS
                .map(d -> e.add(d))
                .filter(p -> PASSABLE.contains(getAtOr(map, p, "#"))));
        }
        return edge.size();
    }

    private static long calc2(int steps, String input, boolean mirrorEdge) {
        return calc2b(steps, mirrorEdge, AocUtils2D.parse2d(input)).last();
    }

    /**
     * How many garden plots could the Elf reach in exactly 'steps' steps?
     *
     * Optimized water spilling utilizing two properties:
     * 1. chessboard coloring
     * 2. thin seen
     *
     * Chessboard coloring is the fact that when only up/down/left/right movements are available, and if you paint the surface in a chess-board pattern, you will always end up on the same color on even/odd steps.
     *
     * Thin seen, is that you don't have to keep all seen places to avoid repeating. It is enough to keep only the previous step as 'seen'. It will block you from going back.
     *
     * Both those properties are used to calculate total reachable points by defined step. There are two separate counters - one for even steps, and one for odd.
     */
    private static YList<Long> calc2b(int steps, boolean mirrorEdge, YList<YList<String>> map) {
        YList<Long> result = al(1L);

        Vec2i wh = v2i(map.first().size(), map.size());
        YSet<Vec2i> edge = hs(AocUtils2D.find(map, "S"));

        long[] counter = new long[]{0L, 1L};
        YList<YSet<Vec2i>> terminators = al(hs(), hs(edge.first()));

        for (int i = 0; i < steps; i++) {
            int phase = i % 2;
            YSet<Vec2i> terminator = terminators.get(phase);

            edge = edge.flatMap(e -> DIRS
                .map(d -> e.add(d))
                .filter(p -> PASSABLE.contains(mirrorEdge ? getAt(map, p.cycle(wh)) : getAtOr(map, p, "#")))
                .filter(p -> !terminator.contains(p))
            );
            counter[phase] += edge.size();
            terminators.set(phase, edge);

            result.add(counter[phase]);
        }
        return result;
    }

}
