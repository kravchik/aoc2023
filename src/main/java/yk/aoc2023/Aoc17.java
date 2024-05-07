package yk.aoc2023;

import org.junit.Test;
import yk.aoc2023.utils.AocUtils2D;
import yk.jcommon.fastgeom.Vec2i;
import yk.ycollections.YList;
import yk.ycollections.YMap;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;

public class Aoc17 {

    public static final String TEST_INPUT = "2413432311323\n" +
            "3215453535623\n" +
            "3255245654254\n" +
            "3446585845452\n" +
            "4546657867536\n" +
            "1438598798454\n" +
            "4457876987766\n" +
            "3637877979653\n" +
            "4654967986887\n" +
            "4564679986453\n" +
            "1224686865563\n" +
            "2546548887735\n" +
            "4322674655533";

    @Test
    public void test1() {
        assertEquals(102, calc1(1, 3, TEST_INPUT));
    }

    @Test
    public void test2a() {
        assertEquals(94, calc1(4, 10, TEST_INPUT));
    }

    @Test
    public void test2b() {
        assertEquals(71, calc1(4, 10,
                "111111111111\n" +
                "999999999991\n" +
                "999999999991\n" +
                "999999999991\n" +
                "999999999991"));
    }

    @Test
    public void solution1() {
        assertEquals(635, calc1(1, 3, readPuzzle("aoc17.txt")));
    }

    @Test
    public void solution2() {
        assertEquals(734, calc1(4, 10, readPuzzle("aoc17.txt")));
    }

    private static int calc1(int minStraight, int maxStraight, String input) {
        YList<YList<Integer>> data = AocUtils2D.parse2d("\n", "", Integer::parseInt, input);

        YList<Crucible> edge = al(new Crucible(0, 0, v2i(0, 0), v2i(1, 0)),
                                  new Crucible(0, 0, v2i(0, 0), v2i(0, 1)));
        YMap<YList, Crucible> besties = hm();
        Vec2i target = v2i(data.first().size() - 1, data.size() - 1);
        while(edge.notEmpty()) {
            Crucible cur = edge.remove(edge.size() - 1);
            edge.addAll(al(
                        cur.straight(data),
                        cur.right(minStraight, maxStraight, data),
                        cur.left(minStraight, maxStraight, data))
                    .filter(c -> c.straightLeft >= 0)
                    .filter(c -> AocUtils2D.isInside(c.pos, data))
                    .filter(c -> {
                        Crucible b = besties.get(al(c.pos, c.dir, c.straightLeft));
                        return b == null || c.heatLost < b.heatLost;
                    })
                    .forEachFun(c -> besties.put(al(c.pos, c.dir, c.straightLeft), c))
            );
            edge = edge.sorted(c -> c.pos.manhattanDistance(target));
        }
        YMap<YList, Crucible> bb = besties.filter((k, v) -> k.first().equals(target));
        return bb.values().min(c -> c.heatLost).heatLost;
    }

    public static class Crucible {
        public int straightLeft;
        public Vec2i pos;
        public Vec2i dir;
        public int heatLost;

        public Crucible(int straightLeft, int heatLost, Vec2i pos, Vec2i dir) {
            this.straightLeft = straightLeft;
            this.heatLost = heatLost;
            this.pos = pos;
            this.dir = dir;
        }
        
        public Crucible straight(YList<YList<Integer>> heatMap) {
            Crucible result = new Crucible(straightLeft - 1, heatLost, pos.add(dir), dir);
            result.heatLost += AocUtils2D.getAtOr(heatMap, result.pos, 0);
            return result;
        }
        public Crucible right(int minStraight, int maxStraight, YList<YList<Integer>> heatMap) {
            Crucible result = new Crucible(maxStraight - 1, heatLost, pos.add(dir.rot90()), dir.rot90());
            result.heatLost += AocUtils2D.getAtOr(heatMap, result.pos, 0);
            result = makeSteps(minStraight, heatMap, result);
            return result;
        }

        private static Crucible makeSteps(int minStraight, YList<YList<Integer>> heatMap, Crucible from) {
            for (int i = 1; i < minStraight; i++) from = mekeStep(heatMap, from);
            return from;
        }

        private static Crucible mekeStep(YList<YList<Integer>> heatMap, Crucible result) {
            result = new Crucible(result.straightLeft - 1, result.heatLost, result.pos.add(result.dir), result.dir);
            result.heatLost += AocUtils2D.getAtOr(heatMap, result.pos, 0);
            return result;
        }

        public Crucible left(int minStraight, int maxStraight, YList<YList<Integer>> heatMap) {
            Crucible result = new Crucible(maxStraight - 1, heatLost, pos.add(dir.rot_90()), dir.rot_90());
            result.heatLost += AocUtils2D.getAtOr(heatMap, result.pos, 0);

            result = makeSteps(minStraight, heatMap, result);

            return result;
        }

        @Override
        public String toString() {
            return "Crucible{" +
                    "straightLeft=" + straightLeft +
                    ", pos=" + pos +
                    ", dir=" + dir +
                    ", heatLost=" + heatLost +
                    '}';
        }
    }
}
