package yk.aoc2023.utils;

import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.XYit;
import yk.ycollections.YArrayList;
import yk.ycollections.YList;
import yk.ycollections.YMap;

import java.util.function.Function;

import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;

public class AocUtils2D {
    public static final YList<Vec2i> DIRS = al(v2i(1, 0), v2i(0, 1), v2i(-1, 0), v2i(0, -1));
    public static final YMap<String, Vec2i> SYMBOL_TO_DIR = hm(">", v2i(1, 0),
                                                               "v", v2i(0, 1),
                                                               "<", v2i(0, -1),
                                                               "^", v2i(0, -1));

    public static Vec2i find(YList<YList<String>> map, String symbol) {
        for (XYit xy : XYit.wh(map.first().size(), map.size())) {
            if (getAt(map, xy.getVec2i()).equals(symbol)) return xy.getVec2i();
        }
        return null;
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

    public static YList<YList<String>> parse2d(String input) {
        return parse2d("\n", "", input);
    }

    public static YList<YList<String>> parse2d(String lineSplitter, String elementSplitter, String input) {
        return al(input.split(lineSplitter)).map(s -> al(s.split(elementSplitter)).map(c -> c.intern()));
    }

    public static <T> YList<YList<T>> parse2d(String lineSplitter, String elementSplitter, Function<String, T> parser,
                                              String input) {
        return al(input.split(lineSplitter)).map(s -> al(s.split(elementSplitter)).map(c -> parser.apply(c.intern())));
    }

    public static <T> boolean isInside(Vec2i pos, YList<YList<T>> data) {
        if (pos.x < 0 || pos.y < 0) return false;
        if (pos.x >= data.first().size() || pos.y >= data.size()) return false;
        return true;
    }

    public static <T> T getAtOr(YList<YList<T>> data, Vec2i pos, T d) {
        return isInside(pos, data) ? getAt(data, pos) : d;
    }

    public static <T> T getAt(YList<YList<T>> data, Vec2i pos) {
        return data.get(pos.y).get(pos.x);
    }
}
