package yk.aoc2023;

import org.junit.Test;
import yk.aoc2023.utils.AocUtils2D;
import yk.ycollections.YList;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.*;
import static yk.ycollections.YArrayList.al;

public class Aoc13 {
    @Test
    public void test1() {
        assertEquals(405, calc(false,
                "#.##..##.\n" +
                "..#.##.#.\n" +
                "##......#\n" +
                "##......#\n" +
                "..#.##.#.\n" +
                "..##..##.\n" +
                "#.#.##.#.\n" +
                "\n" +
                "#...##..#\n" +
                "#....#..#\n" +
                "..##..###\n" +
                "#####.##.\n" +
                "#####.##.\n" +
                "..##..###\n" +
                "#....#..#"));
    }

    @Test
    public void test2() {
        assertEquals(400, calc(true,
                "#.##..##.\n" +
                "..#.##.#.\n" +
                "##......#\n" +
                "##......#\n" +
                "..#.##.#.\n" +
                "..##..##.\n" +
                "#.#.##.#.\n" +
                "\n" +
                "#...##..#\n" +
                "#....#..#\n" +
                "..##..###\n" +
                "#####.##.\n" +
                "#####.##.\n" +
                "..##..###\n" +
                "#....#..#"));
    }

    @Test
    public void test2b() {
        assertEquals(1000, calc(true,
                ".#.####\n" +
                "##..#.#\n" +
                "##..#.#\n" +
                ".#.####\n" +
                "..#..#.\n" +
                "####.#.\n" +
                "#.#.#.#\n" +
                ".#..#.#\n" +
                "##.##..\n" +
                "#.#..#.\n" +
                "#.#...."));
    }

    @Test
    public void test1b() {
        assertEquals(100, calc(false,
                ".##......##\n" +
                ".##......##\n" +
                ".#.#.##.#.#\n" +
                "#.########.\n" +
                "#.##....##.\n" +
                "#.######.#.\n" +
                "####....###\n" +
                "##.#.##.#.#\n" +
                "###......##\n" +
                "...######..\n" +
                "####.##.###\n"
));
    }

    @Test
    public void solution1() {
        assertEquals(36041, calc(false, readPuzzle("aoc13.txt")));
    }

    @Test
    public void solution2() {
        assertEquals(35915, calc(true, readPuzzle("aoc13.txt")));
    }

    public static int calc(boolean withSmudge, String input) {
        YList<YList<YList<String>>> pp = al(input.split("\n\n")).map(p -> al(p.split("\n")).map(l -> stringToStrings(l)));
        return pp.map(p -> {
            int a1 = getMirror(withSmudge, p);
            int a2 = getMirror(withSmudge, AocUtils2D.transpose(p));
            return Integer.max(a1 * 100, a2);
        }).forThis(tt -> {if (tt.contains(0)) System.out.println("blya " + tt.filter(t -> t == 0).size());}
        ).reduce(INT_ADD);
    }

    private static int getMirror(boolean withSmudge, YList<YList<String>> p) {
        for (int i = 0; i < p.size() - 1; i++) {
            boolean seenSmudge = false;
            boolean foundMirror = true;
            for (int j = 0; j < p.size(); j++) {
                int left = i - j;
                int right = i + j + 1;
                if (left < 0 || right >= p.size()) break;
                YList<String> rightLine = p.get(right);
                YList<String> leftLine = p.get(left);
                if (withSmudge) {
                    int dif = countDifference(rightLine, leftLine);
                    if (dif == 0) continue;
                    if (dif == 1) {
                        if (seenSmudge) foundMirror = false;
                        seenSmudge = true;
                    } else {
                        foundMirror = false;
                    }
                } else if (!rightLine.equals(leftLine)) foundMirror = false;
            }
            if (withSmudge) {
                if (foundMirror && seenSmudge) return i + 1;
            } else if (foundMirror) return i + 1;
        }
        return 0;
    }

    public static int countDifference(YList<String> l1, YList<String> l2) {
        int dif = 0;
        for (int i = 0; i < l1. size(); i++) {
            if (!l1.get(i).equals(l2.get(i))) dif++;
        }
        return dif;
    }
}
