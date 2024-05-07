package yk.aoc2023;

import org.junit.Test;
import yk.ycollections.YList;

import static java.lang.Long.parseLong;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.ycollections.YArrayList.al;

public class Aoc06 {

    @Test
    public void test1() {
        assertEquals(288, calc(parse1(
                "Time:      7  15   30\n" +
                "Distance:  9  40  200")));
    }

    @Test
    public void test2() {
        assertEquals(71503, calc(parse2(
                "Time:      7  15   30\n" +
                "Distance:  9  40  200")));
    }

    @Test
    public void solution1() {
        assertEquals(4403592, calc(parse1(readPuzzle("aoc06.txt"))));
    }

    @Test
    public void solution2() {
        assertEquals(38017587, calc(parse2(readPuzzle("aoc06.txt"))));
    }

    public static long calc(YList<YList<Long>> races) {
        long result = 1;

        for (YList<Long> entry : races) {
            long time = entry.first();
            long distance = entry.last();

            long marginSteps = 0;
            for (long chargeTime = 1; chargeTime < time; chargeTime++) {
                long curDistance = chargeTime * (time - chargeTime);
                if (curDistance > distance) marginSteps++;
            }
            result *= marginSteps;
        }
        return result;
    }

    private static YList<YList<Long>> parse1(String input) {
        return al(input.split("\n"))
                .map(l -> al(l.split(":")[1].trim().split("\\s+")).map(n -> parseLong(n)))
                .mapThis(nn -> nn.get(0).zipWith(nn.get(1), (n1, n2) -> al(n1, n2)));
    }

    private static YList<YList<Long>> parse2(String input) {
        return al(input.split("\n"))
                .map(l -> al(l.split(":")[1].trim().replaceAll("\\s+", "")).map(n -> parseLong(n)))
                .mapThis(nn -> nn.get(0).zipWith(nn.get(1), (n1, n2) -> al(n1, n2)));
    }
}
