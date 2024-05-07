package yk.aoc2023.utils;

import org.junit.Test;
import yk.ycollections.YList;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static yk.ycollections.YArrayList.al;

public class ItRegex {

    public static Iterable<Matcher> regexIterable(String p, String s) {
        final Matcher m = Pattern.compile(p).matcher(s);
        return () -> new Iterator() {
            @Override
            public boolean hasNext() {
                return m.find();
            }

            @Override
            public Matcher next() {
                return m;
            }
        };
    }

    public static YList<String> regexGroups(String p, String s) {
        YList<String> result = al();
        for (Matcher group : regexIterable(p, s)) {
            for (int i = 1; i < group.groupCount() + 1; i++) result.add(group.group(i));
        }
        return result;
    }

    @Test
    public void testRegexGroups() {
        assertEquals(al("a", "<", "2006", "qkq"), regexGroups("([a-z]+)([<>])([0-9]+):([a-z]+)", "a<2006:qkq"));
    }

}
