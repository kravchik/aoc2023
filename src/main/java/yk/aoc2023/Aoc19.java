package yk.aoc2023;

import org.junit.Test;
import yk.jcommon.fastgeom.Vec2i;
import yk.ycollections.YList;
import yk.ycollections.YMap;

import static java.lang.Integer.*;
import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.*;
import static yk.aoc2023.utils.ItRegex.regexGroups;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;

public class Aoc19 {
    public static final String TEST_INPUT =
            "px{a<2006:qkq,m>2090:A,rfg}\n" +
            "pv{a>1716:R,A}\n" +
            "lnx{m>1548:A,A}\n" +
            "rfg{s<537:gd,x>2440:R,A}\n" +
            "qs{s>3448:A,lnx}\n" +
            "qkq{x<1416:A,crn}\n" +
            "crn{x>2662:A,R}\n" +
            "in{s<1351:px,qqz}\n" +
            "qqz{s>2770:qs,m<1801:hdj,R}\n" +
            "gd{a>3333:R,R}\n" +
            "hdj{m>838:A,pv}\n" +
            "\n" +
            "{x=787,m=2655,a=1222,s=2876}\n" +
            "{x=1679,m=44,a=2067,s=496}\n" +
            "{x=2036,m=264,a=79,s=2244}\n" +
            "{x=2461,m=1339,a=466,s=291}\n" +
            "{x=2127,m=1623,a=2188,s=1013}";

    @Test
    public void test1() {
        assertEquals(19114, calc1(TEST_INPUT));
    }
    @Test
    public void solution1() {
        assertEquals(386787, calc1(readPuzzle("aoc19.txt")));
    }
    @Test
    public void test2() {
        assertEquals(167409079868000L, calc2(TEST_INPUT));
    }
    @Test
    public void solution2() {
        assertEquals(131029523269531L, calc2(readPuzzle("aoc19.txt")));
    }

    public static class Command {
        public String rating;
        public String op;
        public int count;
        public String then;

        public Command(String rating, String op, int count, String then) {
            this.rating = rating;
            this.op = op;
            this.count = count;
            this.then = then;
        }

        @Override
        public String toString() {
            return "Command{" +
                    "rating='" + rating + '\'' +
                    ", op='" + op + '\'' +
                    ", count=" + count +
                    ", then='" + then + '\'' +
                    '}';
        }
    }

    private static int calc1(String input) {
        return runWorkflows(parseRatings(input), parseWorkflows(input))
                .map(gear -> gear.values().reduce(INT_ADD)).reduce(INT_ADD);
    }

    private static long calc2(String input) {
        YMap<String, YList<Command>> workflows = parseWorkflows(input);

        YMap<String, Vec2i> gear = al("x", "m", "a", "s").toMap(k -> k, k -> v2i(1, 4000));
        YList<YMap<String, Vec2i>> ranges = step("in", workflows, gear);
        System.out.println(ranges.toString("\n"));

        Long result = ranges
                .map(r -> r.values().reduce(1L, (res, cur) -> res * (cur.y - cur.x + 1)))
                .reduce(LONG_ADD);
        System.out.println(result);
        return result;

    }

    private static YList<YMap<String, Vec2i>> step(String commandName, YMap<String, YList<Command>> workflows,
                                                                       YMap<String, Vec2i> gear) {
        if (gear.values().isAny(t -> t.x > t.y)) return al();
        if (commandName.equals("A")) return al(gear);
        if (commandName.equals("R")) return al();

        YList<YMap<String, Vec2i>> result = al();
        YList<Command> cur = workflows.get(commandName);
        System.out.println(commandName);
        for (Command command : cur) {
            Vec2i rating = gear.get(command.rating);

            if ("<".equals(command.op)) {
                result.addAll(step(command.then, workflows,
                       gear.with(command.rating, v2i(rating.x, min(command.count - 1, rating.y)))));
                gear = gear.with(command.rating, v2i(max(command.count, rating.x), rating.y));
            }
            else if (">".equals(command.op)) {
                result.addAll(step(command.then, workflows,
                       gear.with(command.rating, v2i(max(command.count + 1, rating.x), rating.y))));
                gear = gear.with(command.rating, v2i(rating.x, min(command.count, rating.y)));
            } else {
                return result.withAll(step(command.then, workflows, gear));
            }
        }
        throw new RuntimeException("Should never reach here");
    }

    private static YList<YMap<String, Integer>> parseRatings(String input) {
        return al(input.split("\n\n")[1].split("\n"))
                .map(l -> al(l.replaceAll("[{}]", "").split(",")))
                .map(l -> l.toMap(k -> k.split("=")[0], v -> parseInt(v.split("=")[1])));
    }

    private static YMap<String, YList<Command>> parseWorkflows(String input) {
        return al(input.split("\n\n")[0].split("\n"))
                .map(l -> al(l.split("\\{")))
                .toMap(l -> l.first(),
                        l -> al(l.last().replaceAll("}", "")
                                .split(","))
                                .map(c -> c.contains(":")
                                        ? regexGroups("(.+)([<>])(.+):(.+)", c).assertSize(1).get(0).mapThis(
                                            g -> new Command(g.get(0), g.get(1), parseInt(g.get(2)), g.get(3)))
                                        : new Command(null, null, 0, c)));
    }

    private static YList<YMap<String, Integer>> runWorkflows(YList<YMap<String, Integer>> ratings, YMap<String, YList<Command>> workflows) {
        YList<YMap<String, Integer>> accepted = al();
        a:for (YMap<String, Integer> rating : ratings) {
            //System.out.println("rating = " + rating);
            String commandName = "in";
            while (true) {
                System.out.println("commandName = " + commandName);
                if (commandName.equals("A")) {
                    accepted.add(rating);
                    break;
                }
                else if (commandName.equals("R")) break;
                else {
                    YList<Command> cur = workflows.get(commandName);
                    //System.out.println(cur);
                    for (Command command : cur) {
                        if (command.rating == null) {
                            if (command.then.equals("A")) {
                                accepted.add(rating);
                                continue a;
                            }
                            if (command.then.equals("R")) continue a;
                            commandName = command.then;
                        } else {
                            if (command.op.equals("<")) {
                                if (rating.get(command.rating) < command.count) {
                                    commandName = command.then;
                                    break;
                                }
                            }
                            if (command.op.equals(">")) {
                                if (rating.get(command.rating) > command.count) {
                                    commandName = command.then;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return accepted;
    }
}
