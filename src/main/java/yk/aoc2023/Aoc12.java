package yk.aoc2023;

import org.junit.Test;
import yk.ycollections.Tuple;
import yk.ycollections.YList;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.aoc2023.utils.AocUtils.stringToStrings;
import static yk.ycollections.YArrayList.*;

public class Aoc12 {

    public static final String TEST_INPUT = "???.### 1,1,3\n" +
            ".??..??...?##. 1,1,3\n" +
            "?#?#?#?#?#?#?#? 1,3,1,6\n" +
            "????.#...#... 4,1,1\n" +
            "????.######..#####. 1,6,5\n" +
            "?###???????? 3,2,1";

    @Test
    public void test1() {
        assertEquals(21, calc(TEST_INPUT));
    }

    @Test
    public void test2() {
        assertEquals(525152, calc2(TEST_INPUT));
    }

    @Test
    public void test1a() {
        assertEquals(0, calcCount2(stringToStrings("###??"), al(3, 2)));
        assertEquals(0, calcCount2(stringToStrings("??"), al(1, 1)));
        assertEquals(1, calcCount2(stringToStrings("#"), al(1)));
        assertEquals(1, calcCount2(stringToStrings("?"), al(1)));
        assertEquals(1, calcCount2(stringToStrings(".#"), al(1)));
        assertEquals(1, calcCount2(stringToStrings("#."), al(1)));
        assertEquals(1, calcCount2(stringToStrings("?."), al(1)));
        assertEquals(1, calcCount2(stringToStrings(".?"), al(1)));
        assertEquals(2, calcCount2(stringToStrings("??"), al(1)));
        assertEquals(1, calcCount2(stringToStrings("???.###"), al(1, 1, 3)));
        assertEquals(1, calcCount2(stringToStrings("?#."), al(2)));
        assertEquals(4, calcCount2(stringToStrings(".??..??...?##."), al(1, 1, 3)));
        assertEquals(1, calcCount2(stringToStrings("?#?#?#?#?#?#?#?"), al(1, 3, 1, 6)));
        assertEquals(1, calcCount2(stringToStrings("????.#...#..."), al(4, 1, 1)));
        assertEquals(1, calcCount2(stringToStrings("###???"), al(3, 2)));
        assertEquals(1, calcCount2(stringToStrings("?###???"), al(3, 2)));
        assertEquals(0, calcCount2(stringToStrings("###???"), al(3, 1, 1)));
        assertEquals(10, calcCount2(stringToStrings("?###????????"), al(3, 2, 1)));
    }

    @Test
    public void solution1() {
        assertEquals(8419, calc(readPuzzle("aoc12.txt")));
    }

    @Test
    public void solution2() {
        for (int i = 0; i < 100; i++)
            calc2(readPuzzle("aoc12.txt"));
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) calc2(readPuzzle("aoc12.txt"));
        System.out.println(System.currentTimeMillis() - start);
    }

    private static long calc(String input) {
        return al(input.split("\n"))
                .map(l -> al(l.split(" ")).mapThis(ll -> new Tuple<>(
                        stringToStrings(ll.first()),
                        al(ll.last().split(",")).map(s -> parseInt(s)))))
                .map(l -> calcCount2(l.a, l.b)).reduce((l1, l2) -> l1 + l2);
    }

    private static long calc2(String input) {
        return toYList(al(input.split("\n"))
                .map(l -> al(l.split(" ")).mapThis(ll -> new Tuple<>(
                        stringToStrings(allocate(5, i -> ll.first()).toString("?")),
                        al(allocate(5, i -> ll.last()).toString(",").split(",")).map(s -> parseInt(s)))))
                .map(l -> calcCount2(l.a, l.b)))
                .reduce((i1, i2) -> i1 + i2);
    }

    private static long calcCount2(YList<String> ss, YList<Integer> nn) {
        long[][] counters = new long[ss.size() + 1][nn.size() + 1];
        counters[0][0] = 1;

        for (int s = 0; s < ss.size(); s++) {
            String symbol = ss.get(s);

            a:for (int n = 0; n <= nn.size(); n++) {
                long thisVariants = counters[s][n];

                if (symbol.equals(".") || symbol.equals("?")) counters[s+1][n] += counters[s][n];
                if (symbol.equals("#") || symbol.equals("?")) if (n < nn.size()) {
                    int afterStone = s + nn.get(n);
                    if (afterStone > ss.size()) continue;
                    for (int iii = s; iii < afterStone; iii++) if (ss.get(iii).equals(".")) continue a;

                    if (n == nn.size() - 1) {
                        if (afterStone <= ss.size()) counters[afterStone][n + 1] += thisVariants;
                    } else {
                        if (afterStone <= ss.size() - 1 && !ss.get(afterStone).equals("#")) {
                            counters[afterStone + 1][n + 1] += thisVariants;
                        }
                    }
                }
            }
        }
        return counters[ss.size()][nn.size()];
    }


    private static int[] getInts(YList<Integer> ii) {
        int[] cache = new int[ii.size()];
        for (int i = 0; i < ii.size(); i++) {
            int sum = 0;
            for (int jj = i; jj < ii.size(); jj++) sum += ii.get(jj) + 1;
            cache[i] = sum - 1;
        }
        return cache;
    }

    private static long calcCountNaive(int ns, int ni, YList<String> ss, YList<Integer> ii, int[] cache) {
        if (ni < ii.size() && ns + cache[ni] > ss.size()) return 0;

        if (ns >= ss.size()) return ni == ii.size() ? 1 : 0;
        String s = ss.get(ns);
        if (s.equals(".")) return calcCountNaive(ns + 1, ni, ss, ii, cache);
        if (s.equals("#")) return ni == ii.size() ? 0 : calcCountNaive(ns, ni + 1, ss, ii.get(ni), ii, cache);
        if (s.equals("?")) return (ni < ii.size() ? calcCountNaive(ns, ni + 1, ss, ii.get(ni), ii, cache) : 0)
                + calcCountNaive(ns + 1, ni, ss, ii, cache);
        throw new RuntimeException("Unexpected symbol");
    }

    private static long calcCountNaive(int ns, int ni, YList<String> ss, int i, YList<Integer> ii, int[] cache) {
        if (ns == ss.size()) return i == 0 && ni == ii.size() ? 1 : 0;
        if (i == 0) {
            if (ss.get(ns).equals("#")) return 0;
            return calcCountNaive(ns + 1, ni, ss, ii, cache);
        }
        if (ss.get(ns).equals(".")) return 0;
        return calcCountNaive(ns + 1, ni, ss, i - 1, ii, cache);
    }
}
