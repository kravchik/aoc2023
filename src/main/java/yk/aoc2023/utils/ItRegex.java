package yk.aoc2023.utils;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItRegex implements Iterable<Matcher> {
    private final Matcher m;

    public ItRegex(String p, String s) {
        Pattern p1 = Pattern.compile(p);
        this.m = p1.matcher(s);
    }

    @Override
    public Iterator<Matcher> iterator() {
        return new Iterator() {
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


}
