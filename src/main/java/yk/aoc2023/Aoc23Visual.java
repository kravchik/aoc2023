package yk.aoc2023;

import yk.jcommon.fastgeom.Vec2i;
import yk.ycollections.YList;
import yk.ycollections.YSet;

import static yk.aoc2023.Aoc23b.*;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.aoc2023.utils.AocUtils2D.parse2d;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;

/**
 * 08.06.2024
 */
public class Aoc23Visual {
    // Generates solution in Graphiz
    // Can be tested online at https://www.devtoolsdaily.com/graphviz/
    public static void main(String[] args) {
        System.out.println("digraph G {");
        YList<YList<String>> map = parse2d(readPuzzle("aoc23.txt"));
        YSet<Aoc23b.Road> net = compact(toNet(map));
        Vec2i start = v2i(map.first().indexOf("."), 0);
        Vec2i end = v2i(map.last().indexOf("."), map.size() - 1);

        Aoc23b.State solution = solve(net.groupByMultiKeys(r -> r.ends), start, end);

        for (Aoc23b.Road r : net) if (!solution.roads.contains(r)){
            System.out.println(r.ends.map(e -> pointLabel(e, start, end)).toString(" -> ")
                + getInfo("none", "black", r.body.size()));
        }

        Vec2i prevPrev = null;
        Vec2i cur = start;
        while(!cur.equals(end)) {
            Vec2i finalCur = cur;
            Vec2i finalPrevPrev = prevPrev;
            Aoc23b.Road road = solution.roads.find(r -> r.ends.contains(finalCur) && !r.ends.contains(finalPrevPrev));
            Vec2i next = road.ends.without(cur).first();
            System.out.println(al(cur, next).map(e -> pointLabel(e, start, end)).toString(" -> ")
                + getInfo("forward", "cyan", road.body.size()));
            prevPrev = cur;
            cur = next;
        }
        System.out.println("}");
    }

    private static String pointLabel(Vec2i e, Vec2i start, Vec2i end) {
        return e.equals(start) ? "start"
            : e.equals(end) ? "end"
            : "p" + e.x + "_" + e.y;
    }

    private static String getInfo(String dir, String color, int label) {
        return "[dir=" + dir + " color=" + color + (" label=\"" + label + "\"") +  "]";
    }
}
