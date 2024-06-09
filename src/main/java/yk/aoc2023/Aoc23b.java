package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.XYit;
import yk.ycollections.YList;
import yk.ycollections.YMap;
import yk.ycollections.YSet;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.aoc2023.utils.AocUtils2D.getAt;
import static yk.aoc2023.utils.AocUtils2D.parse2d;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashSet.hs;

/**
 * 28.05.2024
 */
public class Aoc23b {

    @Test
    public void test2() {
        String input = readPuzzle("aoc23.test.txt");
        YList<YList<String>> map = parse2d(input);
        YSet<Road> net = compact(toNet(map));

        Vec2i start = v2i(map.first().indexOf("."), 0);
        Vec2i end = v2i(map.last().indexOf("."), map.size() - 1);

        assertEquals(154, solve(net.groupByMultiKeys(r -> r.ends), start, end).len - 1);
    }

    @Test
    public void solution2() {
        String input = readPuzzle("aoc23.txt");
        YList<YList<String>> map = parse2d(input);
        YSet<Road> net = compact(toNet(map));

        Vec2i start = v2i(map.first().indexOf("."), 0);
        Vec2i end = v2i(map.last().indexOf("."), map.size() - 1);

        assertEquals(6466, solve(net.groupByMultiKeys(r -> r.ends), start, end).len - 1);
    }

    public static State solve(YMap<Vec2i, YList<Road>> map, Vec2i start, Vec2i end) {
        YList<State> edge = al(new State(start, hs()));
        final State[] result = {null};

        while(edge.notEmpty()) {
            State cur = edge.remove(edge.size() - 1);

            if (cur.head.equals(end)) if (result[0] == null || result[0].len < cur.len) result[0] = cur;

            edge.addAll(map
                .get(cur.head)
                .filter(r -> !r.ends.without(cur.head).isAny(e1 -> cur.roads.containsAny(map.get(e1))))
                .map(r -> new State(r.ends.without(cur.head).assertSize(1).first(), cur.roads.with(r)))
                .filter(s -> result[0] == null || highestOptimisticScore(s, map, end) > result[0].len));
        }

        return result[0];
    }

    private static int highestOptimisticScore(State state, YMap<Vec2i, YList<Road>> map, Vec2i end) {
        YSet<Vec2i> walked = state.roads.flatMap(r -> r.ends);
        walked.remove(state.head);
        YSet<Vec2i> edge = hs(state.head);
        YSet<Road> seen = hs();
        boolean seenEnd = false;

        while(edge.notEmpty()) {
            if (edge.contains(end)) seenEnd = true;
            edge = edge
                .flatMap(e -> map.get(e)
                    .filter(r -> !seen.contains(r) && !walked.containsAny(r.ends)))
                .forThis(rr -> seen.addAll(rr))
                .flatMap(nr -> nr.ends);
        }

        return seenEnd ? seen.reduce(0, (c, r) -> c + r.body.size() - 1) + state.len : 0;
    }

    public static class State {
        public YSet<Road> roads;
        public Vec2i head;
        public int len;

        public State(Vec2i head, YSet<Road> roads) {
            this.head = head;
            this.roads = roads;
            len = roads.reduce(0, (c, r) -> c + r.body.size() - 1) + 1;
        }
    }

    public static YSet<Road> compact(YSet<Road> roads) {
        while(true) {
            YSet<Road> newRoads = hs();
            YSet<Road> removedRoads = hs();

            YMap<Vec2i, YList<Road>> map = roads.groupByMultiKeys(r -> r.ends);

            for (Map.Entry<Vec2i, YList<Road>> c : map.entrySet()) {
                if (c.getValue().size() == 2 && !removedRoads.containsAny(c.getValue())) {

                    Road newRoad = new Road(
                        c.getValue().toSet().flatMap(r -> r.ends).without(c.getKey()),
                        c.getValue().toSet().flatMap(r -> r.body));

                    newRoads.add(newRoad);
                    removedRoads.addAll(c.getValue());
                }
            }
            if (newRoads.isEmpty()) break;
            newRoads.addAll(roads.withoutAll(removedRoads));
            roads = newRoads;
        }
        return roads;
    }

    public static YSet<Road> toNet(YList<YList<String>> map) {
        YSet<Road> roads = hs();
        for (XYit xy : XYit.wh(map.first().size() - 1, map.size() - 1)) {
            roads.addAll(al(Vec2i.AXIS_X, Vec2i.AXIS_Y)
                .map(d -> al(xy.getVec2i(), xy.getVec2i().add(d)))
                .filter(pp -> pp.isAll(p -> !getAt(map, p).equals("#")))
                .map(pp -> new Road(pp.toSet(), pp.toSet())));
        }
        return roads;
    }

    public static class Road {
        public YSet<Vec2i> ends;
        public YSet<Vec2i> body;

        public Road(YSet<Vec2i> ends, YSet<Vec2i> body) {
            if (ends.size() != 2) throw new RuntimeException();
            this.ends = ends;
            this.body = body;
        }

        @Override
        public String toString() {
            return "Road{" +
                "ends=" + ends +
                ", body.size()=" + body.size() +
                '}';
        }
    }
}
