package yk.aoc2023;

import org.junit.Test;
import yk.ycollections.Tuple;
import yk.ycollections.YArrayList;
import yk.ycollections.YList;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.*;
import static yk.ycollections.YArrayList.al;

public class Aoc15 {
    @Test
    public void test1() {
        assertEquals(30, hash("rn=1"));
        assertEquals(253, hash("cm-"));
        assertEquals(97, hash("qp=3"));
        assertEquals(47, hash("cm=2"));
        assertEquals(14, hash("qp-"));
        assertEquals(180, hash("pc=4"));
        assertEquals(9, hash("ot=9"));
        assertEquals(197, hash("ab=5"));
        assertEquals(48, hash("pc-"));
        assertEquals(214, hash("pc=6"));
        assertEquals(231, hash("ot=7"));
    }

    @Test
    public void solution1() {
        assertEquals(508552, calc1(readPuzzle("aoc15.txt")));
    }

    @Test
    public void test2() {
        assertEquals(145, calc2("rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"));
    }

    @Test
    public void solution2() {
        assertEquals(265462, calc2(readPuzzle("aoc15.txt")));
    }

    private static long calc1(String input) {
        return al(input.split(",")).map(s -> (long)hash(s)).reduce(LONG_ADD);
    }

    private static long calc2(String input) {
        ReinMap m = new ReinMap();
        al(input.split(",")).forEach(s -> {
            if (s.endsWith("-")) m.remove(s.substring(0, s.length() - 1));
            else m.put(s.split("=")[0], parseInt(s.split("=")[1]));
        });
        return m.boxes.mapWithIndex(
                (i, box) -> box.mapWithIndex((j, l) -> (1 + i) * (j + 1) * l.b)
                        .reduce(0, (i1, j1) -> i1 + j1))
                .reduce(INT_ADD);
    }

    private static int hash(String s) {
        return stringToStrings(s).reduce(0, (res, c) -> ((res + c.charAt(0)) * 17) % 256);
    }

    private static class ReinMap {
        public YList<YList<Tuple<String, Integer>>> boxes = YArrayList.allocate(256, i -> al());

        public void put(String label, int value) {
            YList<Tuple<String, Integer>> box = boxes.get(hash(label));
            int i = box.indexOf(t -> t.a.equals(label));
            Tuple<String, Integer> t = new Tuple<>(label, value);
            if (i == -1) box.add(t);
            else box.set(i, t);
        }

        public void remove(String label) {
            boxes.get(hash(label)).removeIf(t -> t.a.equals(label));
        }
    }
}
