package yk.aoc2023.utils;

import java.util.function.Function;

public class IndexedFunction<I, R> implements Function<I, R> {
    private IndexedFunctionCallback<I, R> f;
    private int currentIndex;

    public IndexedFunction(IndexedFunctionCallback<I, R> f) {
        this.f = f;
    }

    public static <I, R> IndexedFunction<I, R> indexed(IndexedFunctionCallback<I, R> f) {
        return new IndexedFunction(f);
    }

    @Override
    public R apply(I i) {
        return f.apply(currentIndex++, i);
    }

    public interface IndexedFunctionCallback<I, R> {
        R apply(int index, I input);
    }
}
