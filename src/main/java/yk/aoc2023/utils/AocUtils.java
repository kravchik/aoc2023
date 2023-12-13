package yk.aoc2023.utils;

import yk.aoc2023.Aoc08;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.IO;
import yk.ycollections.YArrayList;
import yk.ycollections.YList;
import yk.ycollections.YSet;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static final BiFunction<Integer, Integer, Integer> INT_ADD = (i, j) -> i + j;
    public static final BiFunction<Integer, Integer, Integer> INT_SUB = (i, j) -> i - j;
    public static final BiFunction<Integer, Integer, Integer> INT_MUL = (i, j) -> i * j;
    public static final BiFunction<Float, Float, Float> FLOAT_SUM = (i, j) -> i + j;

    public static final YList<Vec2i> DIRS = al(v2i(1, 0), v2i(0, 1), v2i(-1, 0), v2i(0, -1));
    public static final YList<Character> DIR_SYMBOLS = al('>', 'v', '<', '^');


    public static long cycle(long value, long period) {
        long res = value % period;
        return res < 0 ? res + period : res;
    }

    public static YList<String> findAll(String s, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        YList result = al();
        while (matcher.find()) result.add(matcher.group(1));
        return result;
    }

    public static YList<Character> stringToCharacters(String input) {
        return toYList(input.chars().boxed().map(i -> (char) (int) i).collect(Collectors.toList()));
    }

    public static YList<String> stringToStrings(String input) {
        return toYList(input.chars().boxed().map(i -> "" + (char) (int) i).collect(Collectors.toList()));
    }

    public static YList<Integer> stringToInts(String input) {
        return toYList(input.chars().boxed().collect(Collectors.toList()));
    }

    public static <T> T pop(YList<T> input) {
        if (input.isEmpty()) throw new RuntimeException("Should not be empty");
        T result = input.last();
        input.remove(input.size() - 1);
        return result;
    }

    public static <T> YList<T> popSome(YList<T> input, int count) {
        if (input.isEmpty()) throw new RuntimeException("Should not be empty");
        YList<T> result = YArrayList.allocate(count);
        for (int i = 0; i < count; i++) {
            result.set(count - i - 1, pop(input));
        }
        return result;
    }

    public static <T> YList<YList<T>> transpose(YList<? extends YList<T>> input) {
        if (input.isEmpty()) throw new RuntimeException("Expecting not empty array");
        if (input.first().isEmpty()) throw new RuntimeException("Expecting not empty sub-array");
        int w = input.first().size();
        int h = input.size();
        YList<YList<T>> result = YArrayList.allocate(w, i -> YArrayList.allocate(h));
        for (int i = 0; i < h; i++) {
            input.get(i).assertSize(w);
            for (int j = 0; j < w; j++) result.get(j).set(i, input.get(i).get(j));
        }
        return result;
    }

    public static <T> YList<YList<T>> split(YList<T> input, int groupSize) {
        YList<YList<T>> result = al();
        YList<T> cur = al();
        for (T t : input) {
            cur.add(t);
            if (cur.size() == groupSize) {
                result.add(cur);
                cur = al();
            }
        }
        if (cur.notEmpty()) result.add(cur);
        return result;
    }

    public static <T> YList<YList<T>> split(YList<T> input, BiFunction<YList<T>, T, Integer> callback) {
        YList<YList<T>> result = al();
        YList<T> cur = al();
        for (T l : input) {
            Integer res = callback.apply(cur, l);
            if (res == 1) {
                cur.add(l);
                result.add(cur);
                cur = al();
            } else if (res == 2) {
                result.add(cur);
                cur = al();
                cur.add(l);
            } else if (res == 3) {
                result.add(cur);
                cur = al();
            } else {
                cur.add(l);
            }
        }
        if (cur.notEmpty()) result.add(cur);
        return result;
    }

    public static <T> YList<YList<T>> split(YList<T> input, Function<Accumulator, Accumulator> consumer) {
        YList<T> cur = al();
        Accumulator acc = new Accumulator();
        for (T l : input) {
            acc.element = l;
            consumer.apply(acc);
        }
        if (acc.cur.notEmpty()) acc.result.add(cur);
        return acc.result;
    }

    public static Vec2i abs(Vec2i v) {
        return v2i(Math.abs(v.x), Math.abs(v.y));
    }

    public static int sum(Vec2i v) {
        return v.x + v.y;
    }

    public static int max(Vec2i v) {
        return Math.max(v.x, v.y);
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

    public static YList<YList<String>> parse2d(String input) {
        return al(input.split("\n")).map(s -> AocUtils.stringToStrings(s));
    }

    public static class Accumulator<T> {
        public YList<YList<T>> result = al();
        public YList<T> cur = al();
        public T element;

        public Accumulator finishGroup() {
            result.add(cur);
            cur = al();
            return this;
        }

        public Accumulator add() {
            cur.add(element);
            return this;
        }
    }

    public static String readFile(String name) {
        return IO.readFile(Aoc08.DIR + "/" + name);
    }
}
