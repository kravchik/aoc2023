package yk.aoc2023;

import org.junit.Test;
import yk.ycollections.YList;
import yk.ycollections.YMap;
import yk.ycollections.YSet;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.Aoc08.lcm;
import static yk.aoc2023.Aoc20.Pulse.pulse;
import static yk.aoc2023.utils.AocUtils.readPuzzle;
import static yk.ycollections.YArrayList.al;
import static yk.ycollections.YHashMap.hm;
import static yk.ycollections.YHashSet.hs;

public class Aoc20 {

    @Test
    public void test1() {
        assertEquals(32000000, calc1(parseModules(
                "broadcaster -> a, b, c\n" +
                "%a -> b\n" +
                "%b -> c\n" +
                "%c -> inv\n" +
                "&inv -> a")));
    }

    @Test
    public void test1b() {
        assertEquals(11687500, calc1(parseModules(
                "broadcaster -> a\n" +
                "%a -> inv, con\n" +
                "&inv -> b\n" +
                "%b -> con\n" +
                "&con -> output\n"
                )));
    }

    @Test
    public void solution1() {
        assertEquals(747304011, calc1(parseModules(readPuzzle("aoc20.txt"))));
    }

    @Test
    public void solution2() {
        assertEquals(220366255099387L, calc2(parseModules(readPuzzle("aoc20.txt")), hs("ds", "bd", "dt", "cs")));
    }

    private static long calc1(YMap<String, Module> modules) {
        long[] counts = new long[]{0, 0};
        for (int i = 0; i < 1000; i++) {
            pressButton(modules, pulses -> {
                int highs = pulses.filter(p -> p.isHigh).size();
                counts[0] += highs;
                counts[1] += pulses.size() - highs;
            });
        }
        return counts[0] * counts[1];
    }

    /**
     * The schema underlying, is, actually, some kind of a counter. It doesn't make sense / it is impossible, to get an answer by honestly executing the scheme.
     * The better approach is to analyze the final part of the scheme, and find out which local conditions should match, and then try to solve those conditions separately.
     * By analysis of the scheme, it looks like all four '&' modules (ds, bd, dt, cs) should activate so that the final machine will activate. Also, it looks like those gates are activated independently and periodically. So, when we know 4 different periods, we can calculate when they will intersect.
     */
    private static long calc2(YMap<String, Module> modules, YSet<String> targets) {
        int presses = 0;
        YMap<String, Long> result = hm();

        while(result.size() < targets.size()) {
            presses++;
            long finalPresses = presses;
            pressButton(modules, (pulses) -> {
                for (String t : targets) {
                    if (result.get(t) == null && modules.get(t).inputs.values().isAll(v -> v)) result.put(t, finalPresses);
                }
            });
        }
        return result.values().reduce((a, b) -> lcm(a, b));
    }

    private static YMap<String, Module> parseModules(String input) {
        YMap<String, Module> modules = al(input.replaceAll("\n\n", "\n").split("\n"))
                .map(l1 -> al(l1.split(" -> ")))
                .map(l -> new Module(l.first(), al(l.last().split(", "))))
                .toMap(o -> o.name, o -> o);
        modules.forEach((k, v) -> {
            v.dests.forEach(d -> {if (modules.containsKey(d)) modules.get(d).inputs.put(k, false);});
        });
        return modules;
    }

    private static void pressButton(YMap<String, Module> modules, Consumer<YList<Pulse>> onEachStep) {
        YList<Pulse> pulses = al(pulse("button", "broadcaster", false));
        while (pulses.notEmpty()) {
            if (onEachStep != null) onEachStep.accept(pulses);
            YList<Pulse> newP = al();
            for (Pulse p : pulses) {
                Module target = modules.get(p.to);
                if (target == null) continue;
                if (target.name.equals("broadcaster")) {
                    newP.addAll(target.dests.map(d -> pulse(target.name, d, p.isHigh)));
                } else if ("%".equals(target.op)) {
                    if (!p.isHigh) {
                        target.isActive = !target.isActive;
                        newP.addAll(target.dests.map(d -> pulse(target.name, d, target.isActive)));
                    }
                } else if ("&".equals(target.op)) {
                    target.inputs.put(p.from, p.isHigh);
                    boolean send = !target.inputs.values().isAll(b -> b);
                    newP.addAll(target.dests.map(d -> pulse(target.name, d, send)));
                }
            }
            pulses = newP;
        }
    }

    public static class Pulse {
        public String from;
        public String to;
        public boolean isHigh;

        public static Pulse pulse(String from, String to, boolean high) {
            Pulse result = new Pulse();
            result.from = from;
            result.to = to;
            result.isHigh = high;
            return result;
        }

        @Override
        public String toString() {
            return "pulse(" + from + " " + (isHigh ? "HIGH" : "low") + " " + to + ")";
        }
    }

    public static class Module {
        public String name;
        public String op;
        public YList<String> dests;
        public boolean isActive;
        public YMap<String, Boolean> inputs = hm();

        public Module(String op, YList<String> dests) {
            this.name = op.equals("broadcaster") ? op : op.substring(1);
            this.op = op.equals("broadcaster") ? op : op.substring(0, 1);
            this.dests = dests;
        }

        @Override
        public String toString() {
            return "Module{" +
                    "name='" + name + '\'' +
                    ", op='" + op + '\'' +
                    ", dests=" + dests +
                    ", isActive=" + isActive +
                    ", inputs=" + inputs +
                    '}';
        }
    }
}
