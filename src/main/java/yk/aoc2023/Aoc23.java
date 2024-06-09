package yk.aoc2023;

import org.junit.Ignore;
import org.junit.Test;
import yk.jcommon.fastgeom.Vec2i;
import yk.ycollections.YList;
import yk.ycollections.YSet;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.aoc2023.utils.AocUtils2D.*;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;

@SuppressWarnings("StringEquality")
public class Aoc23 {

    @Test
    public void test1() {
        assertEquals(94, calc1(readPuzzle("aoc23.test.txt"), false));
    }

    @Test
    public void test2() {
        assertEquals(154, calc1(readPuzzle("aoc23.test.txt"), true));
    }

    @Test
    public void solution1() {
        assertEquals(2042, calc1(readPuzzle("aoc23.txt"), false));
    }

    @Ignore //takes more than an hour to complete
    @Test
    public void solution2() {
        assertEquals(6466, calc1(readPuzzle("aoc23.txt"), true));
    }

    private static int calc1(String input, boolean ignoreSlopes) {
        YList<YList<String>> map = parse2d(input);
        Vec2i start = v2i(map.first().indexOf("."), 0);
        Vec2i end = v2i(map.last().indexOf("."), map.size() - 1);

        YList<YList<Vec2i>> solutions = solve(map, end, ignoreSlopes, al(start));

        return solutions.map(s -> s.size()).max() - 1;
    }

    private static YList<YList<Vec2i>> solve(YList<YList<String>> map, Vec2i end, boolean ignoreSlopes, YList<Vec2i> variant) {
        YList<Vec2i> solution = null;
        YList<YList<Vec2i>> edge = al(variant);
        while(edge.notEmpty()) {
            YList<Vec2i> cur = edge.remove(edge.size() - 1);

            YList<Vec2i> finalSolution = solution;
            YList<YList<Vec2i>> next =
                (ignoreSlopes || !SYMBOL_TO_DIR.containsKey(getAt(map, cur.last()))
                    ? DIRS.map(d -> cur.last().add(d))
                    : al(cur.last().add(SYMBOL_TO_DIR.get(getAt(map, cur.last())))))
                    .filter(p -> getAtOr(map, p, "#") != "#")
                    .filter(p -> !cur.contains(p))
                    .map(p -> cur.with(p))
                    .mapThis(newTraces -> newTraces.size() > 1
                        ? newTraces
                        .map((YList<Vec2i> v) -> {
                            int optimism = optimism(map, end, ignoreSlopes, v);
                            return optimism == 0 || (finalSolution != null && optimism <= finalSolution.size()) ? null : v;
                        })
                        .filter(v -> v != null)
                        : newTraces);

            for (YList<Vec2i> trace : next) {
                if (trace.last().equals(end)) {
                    if (solution == null || solution.size() < trace.size()) {
                        solution = trace;
                        System.out.println(String.format("Solution: %s, total traces: %s", solution.size(), edge.size()));
                    }
                }
                else edge.add(trace);
            }
            edge = edge.sorted(e -> e.size() + 2 * e.last().manhattanDistance(end));
        }
        return al(solution);
    }

    private static int optimism(YList<YList<String>> map, Vec2i end, boolean ignoreSlopes, YList<Vec2i> trace) {
        int result = 0;
        boolean endAcheved = false;
        YList<Vec2i> edge = al(trace.last());
        YSet<Vec2i> seen = trace.toSet();
        while(edge.notEmpty()) {
            Vec2i cur = edge.remove(edge.size() - 1);
            String curPath = getAt(map, cur);
            YList<Vec2i> next = (ignoreSlopes || !SYMBOL_TO_DIR.containsKey(curPath)
                ? DIRS.map(d -> cur.add(d))
                : al(cur.add(SYMBOL_TO_DIR.get(curPath))))
                .filter(p -> getAtOr(map, p, "#") != "#")
                .filter(p -> !seen.contains(p));
            if (next.isAny(e -> e.equals(end))) endAcheved = true;
            seen.addAll(next);
            edge.addAll(next);
            result += next.size();
            edge = edge.sorted(e -> -e.manhattanDistance(end));
        }
        return endAcheved ? result + trace.size() : 0;
    }

}
