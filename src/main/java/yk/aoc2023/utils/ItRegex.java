package yk.aoc2023.utils;

import org.junit.Test;
import yk.ycollections.YList;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static yk.ycollections.YArrayList.al;

public class ItRegex {

    public static Iterable<Matcher> regexIterable(String pattern, String text) {
        final Matcher m = Pattern.compile(pattern).matcher(text);
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

    public static YList<YList<String>> regexGroups(String pattern, String text) {
        YList<YList<String>> result = al();
        for (Matcher group : regexIterable(pattern, text)) result.add(groupsToList(group));
        return result;
    }

    public static YList<String> groupsToList(Matcher group) {
        YList<String> r = al();
        for (int i = 1; i < group.groupCount() + 1; i++) {
            r.add(group.group(i));
        }
        return r;
    }

    @Test
    public void testRegexGroups() {
        assertEquals(al(al("a", "<", "2006", "qkq")), regexGroups("([a-z]+)([<>])([0-9]+):([a-z]+)", "a<2006:qkq"));
        assertEquals(al(al("a", "<", "2006", "qkq"), al("b", ">", "2007", "kqk")),
            regexGroups("([a-z]+)([<>])([0-9]+):([a-z]+)", "a<2006:qkq b>2007:kqk"));
    }

}
