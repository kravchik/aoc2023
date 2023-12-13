package yk.aoc2023;

import org.junit.Test;
import yk.aoc2023.utils.AocUtils;
import yk.ycollections.YArrayList;
import yk.ycollections.YList;
import yk.ycollections.YMap;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.readFile;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YArrayList.allocate;

public class Aoc08 {

    public static final String DIR = "src/main/java/yk/aoc2023";

    @Test
    public void test1() {
        assertEquals(2, calc(
                "RL\n" +
                "\n" +
                "AAA = (BBB, CCC)\n" +
                "BBB = (DDD, EEE)\n" +
                "CCC = (ZZZ, GGG)\n" +
                "DDD = (DDD, DDD)\n" +
                "EEE = (EEE, EEE)\n" +
                "GGG = (GGG, GGG)\n" +
                "ZZZ = (ZZZ, ZZZ)"));
    }

    @Test
    public void test2() {
        //periods are simple, without displacements or multiple solutions
        YList<Long> periods = extractPrimitivePeriods(
                "LR\n" +
                "\n" +
                "11A = (11B, XXX)\n" +
                "11B = (XXX, 11Z)\n" +
                "11Z = (11B, XXX)\n" +
                "22A = (22B, XXX)\n" +
                "22B = (22C, 22C)\n" +
                "22C = (22Z, 22Z)\n" +
                "22Z = (22B, 22B)\n" +
                "XXX = (XXX, XXX)");
        assertEquals(al(2L, 3L), periods);
        assertEquals(6L, (long) periods.reduce((res, cur) -> lcm(res, cur)));
    }

    @Test
    public void solution1() {
        assertEquals(19631, calc(readFile("aoc08.txt")));
    }

    @Test
    public void solution2() {
        YList<Long> periods = extractPrimitivePeriods(readFile("aoc08.txt"));
        assertEquals(al(20803L, 17873L, 23147L, 15529L, 17287L, 19631L), periods);
        assertEquals(21003205388413L, (long) periods.reduce((res, cur) -> lcm(res, cur)));
    }

    public static long lcm(long number1, long number2) {
        return number1 == 0 || number2 == 0 ? 0 : Math.abs(number1 * number2) / gcd(number1, number2);
    }

    public static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static int calc(String input) {
        YArrayList<String> lines = al(input.split("\n"));
        YList<Integer> instruction = AocUtils.stringToCharacters(lines.first()).map(c -> c == 'R' ? 1 : 0);
        YMap<String, String[]> map = lines.cdr().cdr().map(l -> al(l.split(" = ")))
                .toMap(l -> l.first(), l -> l.last().replaceAll("[() ]", "").split(","));
        int step = 0;
        String currentPlace = "AAA";
        while(!currentPlace.equals("ZZZ")) {
            currentPlace = map.get(currentPlace)[instruction.get(step++ % instruction.size())];
        }

        return step;
    }

    //periods are considered simple, without displacements or multiple solutions
    public static YList<Long> extractPrimitivePeriods(String input) {
        YArrayList<String> lines = al(input.split("\n"));
        YList<Integer> instruction = AocUtils.stringToCharacters(lines.first()).map(c -> c == 'R' ? 1 : 0);
        YMap<String, String[]> map = lines.cdr().cdr().map(l -> al(l.split(" = ")))
                .toMap(l -> l.first(), l -> l.last().replaceAll("[() ]", "").split(","));
        long step = 0;
        YList<String> currentPlace = map.keySet().filter(p -> p.endsWith("A")).toList();
        YList<Long> lasts = allocate(currentPlace.size(), i -> 0L);
        YList<Long> periods = allocate(currentPlace.size());

        for (int i = 0; i < 100_000; i++) {
            Integer dir = instruction.get((int) (step++ % instruction.size()));
            currentPlace = currentPlace.map(p -> map.get(p)[dir]);

            for (int j = 0; j < currentPlace.size(); j++) {
                if (currentPlace.get(j).endsWith("Z")) {
                    long diff = step - lasts.get(j);
                    lasts.set(j, step);

                    if (periods.get(j) == null) periods.set(j, diff);
                    if (periods.get(j) != diff) throw new RuntimeException("Not implemented");
                }
            }
        }
        return periods;
    }
}
