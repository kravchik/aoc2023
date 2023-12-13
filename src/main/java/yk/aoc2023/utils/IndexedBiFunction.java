package yk.aoc2023.utils;

import java.util.function.BiFunction;

public class IndexedBiFunction<I1, I2, R> implements BiFunction<I1, I2, R> {
    private IndexedBiFunctionCallback<I1, I2, R> f;
    private int currentIndex;

    public IndexedBiFunction(IndexedBiFunctionCallback<I1, I2, R> f) {
        this.f = f;
    }

    public static <I1, I2, R> IndexedBiFunction<I1, I2, R> indexed2(IndexedBiFunctionCallback<I1, I2, R> f) {
        return new IndexedBiFunction(f);
    }

    @Override
    public R apply(I1 i1, I2 i2) {
        return f.apply(currentIndex++, i1, i2);
    }

    public interface IndexedBiFunctionCallback<I1, I2, R> {
        R apply(int index, I1 i1, I2 i2);
    }
}
