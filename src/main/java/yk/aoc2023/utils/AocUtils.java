package yk.aoc2023.utils;

import org.junit.Test;
import yk.aoc2023.Aoc08;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;
import yk.ycollections.YList;
import yk.ycollections.YSet;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YArrayList.toYList;
import static yk.ycollections.YHashSet.hs;

/**
 * Created by yuri at 2022.12.09
 * <p>
 * Candidates on inclusion into the YCollection
 */
public class AocUtils {
    public static final YSet<Character> DIGITS = hs();
    static {
        for (int i = '0'; i <= '9'; i++) DIGITS.add((char)i);
    }

    public static final BiFunction<Integer, Integer, Integer> INT_ADD = (i1, j1) -> i1 + j1;
    public static final BiFunction<Long, Long, Long> LONG_ADD = (i, j) -> i + j;
    public static final BiFunction<Long, Long, Long> LONG_MUL = (i, j) -> i * j;
    public static final BiFunction<Integer, Integer, Integer> INT_SUB = (i, j) -> i - j;
    public static final BiFunction<Integer, Integer, Integer> INT_MUL = (i, j) -> i * j;
    public static final BiFunction<Float, Float, Float> FLOAT_SUM = (i, j) -> i + j;
    public static final BiFunction<Double, Double, Double> DOUBLE_ADD = (i, j) -> i + j;
    public static final BiFunction<Double, Double, Double> DOUBLE_MUL = (i, j) -> i * j;

    //TODO MyMath
    public static int cycleBase(int a, int cycle) {return Math.floorDiv(a, cycle);}
    public static long cycleBase(long a, long cycle) {return Math.floorDiv(a, cycle);}
    public static Vec2i cycleBase(Vec2i a, Vec2i cycle) {return v2i(Math.floorDiv(a.x, cycle.x), Math.floorDiv(a.y, cycle.y));}

    public static YList<Character> stringToCharacters(String input) {
        return toYList(input.chars().boxed().map(i -> (char) (int) i).collect(Collectors.toList()));
    }

    public static YList<String> stringToStrings(String input) {
        return al(input.split("")).map(s -> s.intern());
    }

    public static YList<Integer> stringToInts(String input) {
        return toYList(input.chars().boxed().collect(Collectors.toList()));
    }

    public static <T> YList<T> generate(T initial, Function<T, T> gen) {
        YList<T> result = al();
        while(initial != null) {
            result.add(initial);
            initial = gen.apply(initial);
        }
        return result;
    }

    public static <T> YList<T> generate2(T initial, Function<YList<T>, T> gen) {
        YList<T> result = al();
        while(initial != null) {
            result.add(initial);
            initial = gen.apply(result);
        }
        return result;
    }

    public static String readPuzzle(String name) {
        return IO.readFile(Aoc08.DIR + "/" + name);
    }

    public static class CachedHashed<T> {
        public T value;
        public int hash;

        public CachedHashed(T value) {
            this.value = value;
            hash = value.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CachedHashed<?> that = (CachedHashed<?>) o;
            return hash == that.hash && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class CycleState<T> {
        public YList<CachedHashed<T>> seen = al();
        public int disp;
        public int cycle;

        public int findPosAt(long at) {
            return (int) ((at - disp) % cycle + disp);
        }

        public T findStateAt(long at) {
            return seen.get(findPosAt(at)).value;
        }

        @Override
        public String toString() {
            return "CycleState{" +
                    "disp=" + disp +
                    ", cycle=" + cycle +
                    '}';
        }
    }

    public static <T> CycleState<T> findCycleOnUniqueState(Supplier<T> supplier) {
        CycleState<T> result = new CycleState<>();
        while(true) {
            T t = supplier.get();
            CachedHashed<T> cached = new CachedHashed<>(t);
            if (result.seen.contains(cached)) {
                int i = result.seen.indexOf(cached);
                result.disp = i;
                result.cycle = result.seen.size() - i;
                return result;
            }
            result.seen.add(cached);
        }
    }

    @Test
    public void testCycles() {
        final int[] i = {0};
        int[] ii = new int[]{1, 2, 3, 4, 5, 6, 7, 3, 4, 5, 6, 7};
        CycleState<Integer> cycle = findCycleOnUniqueState(() -> ii[i[0]++]);
        assertEquals(5, cycle.cycle);
        assertEquals(2, cycle.disp);
        assertEquals(al(new CachedHashed<>(1), new CachedHashed<>(2), new CachedHashed<>(3), new CachedHashed<>(4), new CachedHashed<>(5), new CachedHashed<>(6), new CachedHashed<>(7)), cycle.seen);

        assertEquals(2, cycle.findPosAt(7));
        assertEquals(3, cycle.findPosAt(8));
        assertEquals(4, cycle.findPosAt(9));
        assertEquals(5, cycle.findPosAt(10));
        assertEquals(6, cycle.findPosAt(11));
        assertEquals(2, cycle.findPosAt(12));
        assertEquals(3, cycle.findPosAt(13));
        assertEquals(4, cycle.findPosAt(14));
        assertEquals(5, cycle.findPosAt(15));
        assertEquals(6, cycle.findPosAt(16));
        assertEquals(2, cycle.findPosAt(17));
        assertEquals(3, cycle.findPosAt(18));
        assertEquals(5, cycle.findPosAt(1000_000_000));
    }

}
