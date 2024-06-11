package yk.aoc2023;

import org.junit.Test;
import yk.aoc2023.utils.Search;
import yk.jcommon.fastgeom.Vec3d;
import yk.jcommon.fastgeom.Vec3l;
import yk.jcommon.utils.Ptr;
import yk.ycollections.YList;

import static java.lang.Long.parseLong;
import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.*;
import static yk.jcommon.fastgeom.Vec3d.v3d;
import static yk.jcommon.fastgeom.Vec3l.v3l;
import static yk.ycollections.YArrayList.al;

public class Aoc24 {

    @Test
    public void test1() {
        assertEquals(2, calc1(
            7, 27, toDouble(parseInput("19, 13, 30 @ -2,  1, -2\n" +
                "18, 19, 22 @ -1, -1, -2\n" +
                "20, 25, 34 @ -2, -2, -4\n" +
                "12, 31, 28 @ -1, -2, -1\n" +
                "20, 19, 15 @  1, -5, -3"))));
    }

    @Test
    public void solution1() {
        assertEquals(31921, calc1(200_000_000_000_000D, 400_000_000_000_000D, toDouble(parseInput(readPuzzle("aoc24.txt")))));
    }

    private static int calc1(double min, double max, YList<YList<Vec3d>> hailStones) {
        YList<Boolean> ints = hailStones.mapUniquePares((v1, v2) -> {
            Vec3d as = v1.first();
            Vec3d ad = v1.last();
            Vec3d bs = v2.first();
            Vec3d bd = v2.last();
            double u = (as.y * bd.x + bd.y * bs.x - bs.y * bd.x - bd.y * as.x) / (ad.x * bd.y - ad.y * bd.x);
            double v = (as.x + ad.x * u - bs.x) / bd.x;
            if (u > 0 && v > 0) {
                Vec3d intersection = as.add(ad.mul(u));
                if (intersection.x >= min && intersection.x <= max
                    && intersection.y >= min && intersection.y <= max
                ) return true;
            }
            return false;
        });

        return ints.filter(i -> i).size();
    }

    @Test
    public void solution2() {
        YList<YList<Vec3d>> stonesD = toDouble(parseInput(readPuzzle("aoc24.txt")));

        YList<Vec3d> rawRay = al(null, null);

        Ptr<Double> res2 = new Ptr<>(0D);
        Search.binarySearch(0, 1_000_000_000_000L, t1 -> {
            Search.binarySearch(0, 1_000_000_000_000L, t2 -> {
                Vec3d pos1 = stonesD.get(0).first().add(stonesD.get(0).last().mul(t1));
                Vec3d pos2 = stonesD.get(30).first().add(stonesD.get(30).last().mul(t2));

                Vec3d start = pos2.sub(pos1).div(t2 - t1).mul(-t1).add(pos1);
                Vec3d dir = pos2.sub(pos1).div(t2 - t1);

                rawRay.set(0, start);
                rawRay.set(1, dir);

                res2.value = 0D;
                for (int i = 2; i < 7; i++)
                    res2.value += getDistance(start, dir, stonesD.get(i).first(), stonesD.get(i).last());
                return res2.value;
            });
            return res2.value;
        });

        Vec3d rawPos = rawRay.get(0).toLong().toDouble();
        Vec3d rawDir = rawRay.get(1).toLong().toDouble();

        System.out.println("Raw pos: " + rawPos);
        System.out.println("Raw dir: " + rawDir);

        System.out.println("Difs with raw solution:     " + timeDif(stonesD, rawPos, rawDir));
        System.out.println("Sum for raw solution: " +       timeDif(stonesD, rawPos, rawDir).reduce(DOUBLE_ADD));
        System.out.println();

        //optimize Dir
        YList<Long> optimizedDir = Search.minimize(
                al((long)rawDir.x, (long)rawDir.y, (long)rawDir.z),
                cur -> timeDif(stonesD, rawPos, v3d(cur.get(0), cur.get(1), cur.get(2))).reduce(DOUBLE_ADD)
        );
        Vec3d oDir = v3d(optimizedDir.get(0), optimizedDir.get(1), optimizedDir.get(2));

        System.out.println("WTF???");
        System.out.println("Optimized dir: " + oDir);
        System.out.println("Difs with optimized dir    :" + timeDif(stonesD, rawPos, oDir));
        System.out.println("Sum for optimized dir: "      + timeDif(stonesD, rawPos, oDir).reduce(DOUBLE_ADD));
        System.out.println();

        //optimize Pos
        YList<Long> optimizedPos = Search.minimize(
                al((long)rawPos.x, (long)rawPos.y, (long)rawPos.z),
                cur -> timeDif(stonesD, v3d(cur.get(0), cur.get(1), cur.get(2)), oDir).reduce(DOUBLE_ADD)
        );

        Vec3d oPos = v3d(optimizedPos.get(0), optimizedPos.get(1), optimizedPos.get(2));
        System.out.println("Optimized pos: " + oPos);
        System.out.println("Difs with optimized dir    :" + timeDif(stonesD, oPos, oDir));
        System.out.println("Sum for optimized dir: " + timeDif(stonesD, oPos, oDir).reduce(DOUBLE_ADD));
        System.out.println();

        long answer = optimizedPos.reduce(LONG_ADD);
        System.out.println("ANSWER: " + answer);
        assertEquals(761691907059631L, answer);

    }

    private YList<Double> timeDif(YList<YList<Vec3d>> stonesD, Vec3d rawPos, Vec3d rawDir) {
        return stonesD.map(s -> timeDif(rawPos, rawDir, s.first(), s.last()));
    }

    public static double timeDif(Vec3d p1, Vec3d v1, Vec3d p2, Vec3d v2) {
        Vec3d n = v1.crossProduct(v2);
        Vec3d p1p2 = p2.sub(p1);
        double nn = n.dot(n);
        double t1 = v2.crossProduct(n).dot(p1p2) / nn;
        double t2 = v1.crossProduct(n).dot(p1p2) / nn;
        return abs(t1 - t2);
    }

    public static double getDistance(Vec3d p1, Vec3d v1, Vec3d p2, Vec3d v2) {
        Vec3d n = v1.crossProduct(v2);
        Vec3d p1p2 = p2.sub(p1);
        double nn = n.dot(n);
        double t1 = v2.crossProduct(n).dot(p1p2) / nn;
        double t2 = v1.crossProduct(n).dot(p1p2) / nn;
        return p1.add(v1.mul(t1)).distance(p2.add(v2.mul(t2)));
    }

    private static YList<YList<Vec3l>> parseInput(String input) {
        return al(input.split("\n"))
            .map(l -> al(l.split(" @ "))
                .map(ll -> al(ll.split(","))
                    .map(n -> parseLong(n.trim()))
                    .mapThis(nn -> v3l(nn.get(0), nn.get(1), nn.get(2)))));
    }

    private static YList<YList<Vec3d>> toDouble(YList<YList<Vec3l>> stones) {
        return stones.map(l -> l.map(v -> v.toDouble()));
    }
}
