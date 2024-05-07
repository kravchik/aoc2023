package yk.aoc2023.utils;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
//TODO ycollections
public class Indexers {

    public static <I, R> Function<I, R> indexed(IndexedFunctionCallback<I, R> f) {
        return new Function<I, R>() {
            private int currentIndex;
            @Override public R apply(I i) {return f.apply(currentIndex++, i);}
        };
    }

    public interface IndexedFunctionCallback<I, R> {
        R apply(int index, I input);
    }

    public static <I> Predicate<I> indexedPredicate(IndexedPredicate<I> f) {
        return new Predicate<I>() {
            private int currentIndex;
            @Override public boolean test(I i) {return f.test(currentIndex++, i);}
        };
    }

    public interface IndexedPredicate<I> {
        boolean test(int index, I input);
    }

    public static <I1, I2, R> BiFunction<I1, I2, R> indexed(IndexedBiFunctionCallback<I1, I2, R> f) {
        return new BiFunction<I1, I2, R>() {
            private int currentIndex;
            @Override public R apply(I1 i1, I2 i2) {return f.apply(currentIndex++, i1, i2);}
        };
    }

    public interface IndexedBiFunctionCallback<I1, I2, R> {
        R apply(int index, I1 i1, I2 i2);
    }
}
