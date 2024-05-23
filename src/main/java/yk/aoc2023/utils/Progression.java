package yk.aoc2023.utils;

import org.junit.Test;
import yk.ycollections.YList;

import static org.junit.Assert.assertEquals;
import static yk.ycollections.YArrayList.al;

/**
 * 23.05.2024
 */
public class Progression {

    /**
     * Kind of "double arithmetic progression"
     *
     * Algorithmic sense:
     *     inc = inc0
     *     result = r0
     *     for (steps) {result += inc; inc += incInc}
     *     return result
     *
     */
    public static long doubleProgression(long steps, long r0, long inc0, long incInc) {
        return (((steps - 1) * steps) / 2) * incInc + inc0 * steps + r0;
    }

    @Test
    public void testDoubleProgression() {
        assertEquals(0, doubleProgression(0, 0, 1, 1));
        assertEquals(1, doubleProgression(1, 0, 1, 1));
        assertEquals(5 + 2 + 2+3 + 2+3+3, doubleProgression(3, 5, 2, 3));
    }

    public static class Result {
        public int offset;
        public int period;
        public int order;
        public long dif;

        public Result(int offset, int period, int order, long dif) {
            this.offset = offset;
            this.period = period;
            this.order = order;
            this.dif = dif;
        }

        @Override
        public String toString() {
            return "progression(" + offset + " " + period + " " + order + " " + dif + ')';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;
            return offset == result.offset && period == result.period && order == result.order && dif == result.dif;
        }
    }

    public static YList<Result> results(YList<Long> numbers) {
        YList<Result> results = al();
        for (int offset = 0; offset < numbers.size(); offset++) {
            for (int period = 1; period < numbers.size() - offset; period++) {
                Result res = search(numbers, offset, period);
                if (res != null) results.add(res);
            }
        }
        return results;
    }

    public static Result search(YList<Long> numbers, int offset, int period) {
        YList<Long> candidates = al();
        for (int i = 0; i < numbers.size() - offset; i += period) {
            candidates.add(numbers.get(offset + i));
        }
        int level = 0;
        while(candidates.size() > 1) {
            Long dif = candidates.first();
            if (candidates.isAll(c -> c.equals(dif))) return new Result(offset, period, level, dif);
            candidates = difLine(candidates);
            level++;
        }
        return null;
    }

    public static YList<Long> difLine(YList<Long> numbers) {
        YList<Long> result = al();
        for (int i = 1; i < numbers.size(); i++) result.add(numbers.get(i) - numbers.get(i - 1));
        return result;
    }

    @Test
    public void test1() {
        assertEquals(new Result(0, 1, 0, 0), results(al(0L, 0L, 0L, 0L, 0L, 0L)).get(0));
        assertEquals(new Result(1, 1, 0, 2), results(al(0L, 2L, 2L, 2L, 2L, 2L)).get(0));
        assertEquals(new Result(0, 1, 1, 1), results(al(0L, 1L, 2L, 3L, 4L, 5L)).get(0));
        assertEquals(new Result(2, 1, 1, 1), results(al(0L, 0L, 0L, 1L, 2L, 3L)).get(0));

        assertEquals(al(new Result(3, 2, 1, 2)), results(al(0L, 0L, 0L, 1L, 10L, 3L, 15L, 5L)));
        assertEquals(al(new Result(1, 2, 2, 1)), results(al(0L, 0L, 0L, 3L, 10L, 7L, 15L, 12L)));
    }
}
