package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.fastgeom.Vec3i;
import yk.ycollections.YList;
import yk.ycollections.YMap;
import yk.ycollections.YSet;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.INT_ADD;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.jcommon.fastgeom.Vec3i.v3i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YArrayList.allocate;
import static yk.ycollections.YHashSet.hs;

public class Aoc22 {

    public static final Vec3i DOWN = v3i(0, 0, -1);
    public static final Vec3i UP = v3i(0, 0, 1);
    public static final String TEST_INPUT =
        "1,0,1~1,2,1\n" +
        "0,0,2~2,0,2\n" +
        "0,2,3~2,2,3\n" +
        "0,0,4~0,2,4\n" +
        "2,0,5~2,2,5\n" +
        "0,1,6~2,1,6\n" +
        "1,1,8~1,1,9";

    @Test
    public void test1() {
        assertEquals(5, calc1(TEST_INPUT));
    }

    @Test
    public void test2() {
        assertEquals(7, calc2(TEST_INPUT));
    }

    @Test
    public void solution1() {
        assertEquals(418, calc1(readPuzzle("aoc22.txt")));
    }

    @Test
    public void solution2() {
        assertEquals(70702, calc2(readPuzzle("aoc22.txt")));
    }

    private static int calc1(String input) {
        YList<Brick> bricks = parseBricks(input);
        YMap<Vec3i, Brick> map = bricks.toMapMultiKeys(b -> b.body, b -> b);
        fallEverything(map);

        YMap<Brick, YSet<Brick>> netUp = genNetUp(bricks, map);
        YMap<Brick, YSet<Brick>> netDown = genNetDown(bricks, map);

        return bricks.filter(brick -> !netUp.get(brick).isAny(b -> netDown.get(b).equals(hs(brick)))).size();
    }

    private static int calc2(String input) {
        YList<Brick> bricks = parseBricks(input);
        YMap<Vec3i, Brick> map = bricks.toMapMultiKeys(b -> b.body, b -> b);
        fallEverything(map);

        return findDesintigratableEffects(bricks, map).values().map(v -> v.size()).reduce(INT_ADD);
    }

    private static void fallEverything(YMap<Vec3i, Brick> map) {
        for (Brick brick : map.values().sorted(b -> b.body.first().z)) {
            while(true) {
                YList<Vec3i> newPoss = brick.body.map(b -> b.add(DOWN));
                if (newPoss.isAny(p -> p.z <= 0 || map.get(p) != null && map.get(p) != brick)) break;
                map.removeAll(brick.body);
                brick.body = newPoss;
                for (Vec3i p : brick.body) map.put(p, brick);
            }
        }
    }

    private static YMap<Brick, YSet<Brick>> findDesintigratableEffects(YList<Brick> bricks, YMap<Vec3i, Brick> map) {
        YMap<Brick, YSet<Brick>> netUp = genNetUp(bricks, map);
        YMap<Brick, YSet<Brick>> netDown = genNetDown(bricks, map);

        return bricks.toMap(b -> b, brick -> {
            YSet<Brick> candidates = netUp.get(brick).copy();

            YSet<Brick> fallen = hs(brick);
            while(true) {
                YSet<Brick> toAdd = candidates.filter(b -> fallen.containsAll(netDown.get(b)));
                if (toAdd.isEmpty()) break;
                candidates.removeAll(toAdd);
                candidates.addAll(netUp.getAll(toAdd).flatMap(fm -> fm));
                fallen.addAll(toAdd);
            };
            return fallen.without(brick);
        });
    }

    private static YMap<Brick, YSet<Brick>> genNetDown(YList<Brick> bricks, YMap<Vec3i, Brick> map) {
        return bricks.toMap(b -> b,
                b -> map.getAll(b.body.map(p -> p.add(DOWN))).filter(b2 -> b2 != b && b2 != null).toSet());
    }

    private static YMap<Brick, YSet<Brick>> genNetUp(YList<Brick> bricks, YMap<Vec3i, Brick> map) {
        return bricks.toMap(b -> b,
                b -> map.getAll(b.body.map(p -> p.add(UP))).filter(b2 -> b2 != b && b2 != null).toSet());
    }

    private static YList<Brick> parseBricks(String input) {
        return al(input.split("\n"))
            .map(l -> al(l.split("~"))
                .map(s -> al(s.split(",")).map(n -> parseInt(n)).mapThis(nn -> v3i(nn.get(0), nn.get(1), nn.get(2)))))
            .map(vv -> new Brick(fillBody(vv.first(), vv.last())));
    }

    private static YList<Vec3i> fillBody(Vec3i first, Vec3i last) {
        Vec3i dif = last.sub(first);
        Vec3i dir = dif.clamp(-1, 1);
        return allocate(dif.abs().max() + 1, i -> first.add(dir.mul(i)));
    }

    private static class Brick {
        public YList<Vec3i> body;

        public Brick(YList<Vec3i> body) {
            this.body = body;
        }
    }
}
