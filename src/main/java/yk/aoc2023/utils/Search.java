package yk.aoc2023.utils;

import yk.ycollections.YList;

import java.util.function.Function;

import static yk.aoc2023.utils.AocUtils.LONG_ADD;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YArrayList.allocate;

public class Search {
    public interface Function_L_D { double apply(long i);}

    public static long minimizeBinary(long from, long to, Function_L_D calc) {
        while(true) {
            long mid = (from + to) / 2;
            if (mid == from || mid == to) return calc.apply(from) < calc.apply(to) ? from : to;

            long l = mid - 100;
            double leftRes = calc.apply(mid - 1);
            while(Double.isNaN(leftRes)) {
                l--;
                leftRes = calc.apply(l);
            }
            long r = mid + 100;
            double rightRes = calc.apply(r);
            while(Double.isNaN(rightRes) || rightRes == leftRes) {
                r++;
                rightRes = calc.apply(r);
            }
            if (leftRes < rightRes) to = mid;
            else if (leftRes > rightRes) from = mid;
            else return from;
        }
    }

    public static YList<Long> minimizeNet(YList<Long> input, Function<YList<Long>, Double> calc) {
        YList<Long> finalInput = input;
        YList<YList<Long>> mm = input.mapWithIndex(
            (i, c) -> allocate(finalInput.size(), ii -> (long) (ii == i ? 1 : 0)));
        mm = mm.withAll(mm.map(mask -> mask.map(m -> -m)));
        YList<YList<Long>> masks = (YList<YList<Long>>)(YList)al()
            //.withAll(mm.map(mask -> mask.map(m -> m * 1000_000L)))
            //.withAll(mm.map(mask -> mask.map(m -> m * 1000L)))
            .withAll(mm.map(mask -> mask.map(m -> m * 100L)))
            .withAll(mm.map(mask -> mask.map(m -> m * 10L)))
            .withAll(mm.map(mask -> mask.map(m -> m * 2L)))
            .withAll(mm.map(mask -> mask.map(m -> m)));

        for (int i = 0; i < 1000; i++) {
            double curResult = calc.apply(input);
            YList<Long> finalInput1 = input;
            YList<YList<Long>> newInputs = masks.map(mask -> finalInput1.zipWith(mask, LONG_ADD));
            YList<Double> newResults = newInputs.map(calc);

            int j = newResults.indexOfMin();{
            if (newResults.get(j) >= curResult) return input;
            input = newInputs.get(j);
            }
        }

        return input;
    }

}
