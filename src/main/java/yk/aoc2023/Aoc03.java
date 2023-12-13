package yk.aoc2023;

import org.junit.Test;
import yk.aoc2023.utils.ItRegex;
import yk.jcommon.fastgeom.Vec2i;
import yk.jcommon.utils.XYit;
import yk.ycollections.YList;
import yk.ycollections.YMap;

import java.util.regex.Matcher;

import static java.lang.Integer.*;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.INT_ADD;
import static yk.aoc2023.utils.AocUtils.readFile;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.jcommon.utils.XYit.lbrt;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;

public class Aoc03 {

    @Test
    public void test1() {
        assertEquals(4361, calc(al((
                "467..114..\n" +
                "...*......\n" +
                "..35..633.\n" +
                "......#...\n" +
                "617*......\n" +
                ".....+.58.\n" +
                "..592.....\n" +
                "......755.\n" +
                "...$.*....\n" +
                ".664.598..").split("\n"))));
    }

    @Test
    public void test2() {
        assertEquals(467835, calc2(al((
                "467..114..\n" +
                "...*......\n" +
                "..35..633.\n" +
                "......#...\n" +
                "617*......\n" +
                ".....+.58.\n" +
                "..592.....\n" +
                "......755.\n" +
                "...$.*....\n" +
                ".664.598..").split("\n"))));
    }

    @Test
    public void solution1() {
        assertEquals(539637, calc(al(readFile("aoc03.txt").split("\n"))));
    }

    @Test
    public void solution2() {
        assertEquals(82818007, calc2(al(readFile("aoc03.txt").split("\n"))));
    }

    public boolean hasSymbol(YList<String> data, int y, int x1, int x2) {
        for (XYit xy : lbrt(
                max(0, x1 - 1),
                max(0, y - 1),
                min(x2, data.first().length() - 1),
                min(y + 1, data.size() - 1))) {
            char c = data.get(xy.y).charAt(xy.x);
            if (c != '.' && (c < '0' || c > '9')) return true;
        }
        return false;
    }

    public int calc(YList<String> input) {
        int sum = 0;
        for (int y = 0; y < input.size(); y++) {
            for (Matcher matcher : new ItRegex("[0-9]+", input.get(y))) {
                if (hasSymbol(input, y, matcher.start(), matcher.end())) sum += parseInt(matcher.group());
            }
        }
        return sum;
    }

    public int calc2(YList<String> input) {
        YMap<Vec2i, String> numbers = parse(input, "[0-9]+");
        YMap<Vec2i, String> symbols = parse(input, "\\*");
        return symbols.mapToList((gearK, gearV) -> numbers
                        .mapToList((k, v) -> intersects(gearK, k, v.length()) ? parseInt(v) : -1)
                        .filter(i -> i > -1))
                .filter(nn -> nn.size() == 2)
                .map(nn -> nn.first() * nn.last())
                .reduce(INT_ADD);
    }

    public boolean intersects(Vec2i gear, Vec2i number, int numberLen) {
        return gear.x >= number.x - 1
                && gear.x <= number.x + numberLen
                && gear.y >= number.y - 1
                && gear.y <= number.y + 1;
    }

    private static YMap<Vec2i, String> parse(YList<String> input, String regex) {
        YMap<Vec2i, String> numbers = hm();
        for (int i = 0; i < input.size(); i++) {
            for (Matcher matcher : new ItRegex(regex, input.get(i))) {
                numbers.put(v2i(matcher.start(), i), matcher.group());
            }
        }
        return numbers;
    }

}

