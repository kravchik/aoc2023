package yk.aoc2023;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.Aoc18.*;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;

public class Aoc18Tests {

    @Test
    public void testArea() {
        assertEquals(18, calcArea(al(
                al(v2i(0, 0), v2i(5, 0)),
                al(v2i(0, 0), v2i(0, 2)),
                al(v2i(5, 0), v2i(5, 2)),
                al(v2i(0, 2), v2i(5, 2))
        )));

        //xxxxx
        //xxxxx
        //xx
        assertEquals(12, calcArea(al(
                al(v2i(0, 0), v2i(4, 0)),
                al(v2i(0, 2), v2i(1, 2)),
                al(v2i(1, 1), v2i(4, 1))
        )));
        //xx
        //xxxxx
        //xxxxx
        assertEquals(12, calcArea(al(
                al(v2i(0, 0), v2i(1, 0)),
                al(v2i(1, 1), v2i(4, 1)),
                al(v2i(0, 2), v2i(4, 2))
        )));
        //xxxxx
        //xxxxx
        //xx xx
        assertEquals(14, calcArea(al(
                al(v2i(0, 0), v2i(4, 0)),
                al(v2i(0, 2), v2i(1, 2)),
                al(v2i(3, 2), v2i(4, 2)),
                al(v2i(1, 1), v2i(3, 1))
        )));
        //xx xx
        //xxxxx
        //xxxxx
        assertEquals(14, calcArea(al(
                al(v2i(0, 0), v2i(1, 0)),
                al(v2i(3, 0), v2i(4, 0)),
                al(v2i(0, 2), v2i(4, 2)),
                al(v2i(1, 1), v2i(3, 1))
        )));
    }

    @Test
    public void testRangeSub() {
        assertEquals(al(), rangeSub(al(al(0, 5)), al(al(0, 5))));
        assertEquals(al(al(0, 0)), rangeSub(al(al(0, 5)), al(al(1, 5))));
        assertEquals(al(al(5, 5)), rangeSub(al(al(0, 5)), al(al(0, 4))));
        assertEquals(al(al(0, 0), al(5, 5)), rangeSub(al(al(0, 5)), al(al(1, 4))));
        assertEquals(al(al(1, 5)), rangeSub(al(al(0, 5)), al(al(-1, 0))));
        assertEquals(al(al(0, 4)), rangeSub(al(al(0, 5)), al(al(5, 6))));
        assertEquals(al(al(0, 5)), rangeSub(al(al(0, 5)), al(al(6, 7))));
    }

    @Test
    public void testRangeAnd() {
        assertEquals(al(), rangeAnd(al(al(0, 5)), al()));
        assertEquals(al(al(0, 5)), rangeAnd(al(al(0, 5)), al(al(0, 5))));
        assertEquals(al(al(1, 5)), rangeAnd(al(al(0, 5)), al(al(1, 5))));
        assertEquals(al(al(0, 4)), rangeAnd(al(al(0, 5)), al(al(0, 4))));
        assertEquals(al(al(1, 4)), rangeAnd(al(al(0, 5)), al(al(1, 4))));
        assertEquals(al(al(0, 0)), rangeAnd(al(al(0, 5)), al(al(-1, 0))));
        assertEquals(al(al(5, 5)), rangeAnd(al(al(0, 5)), al(al(5, 6))));
        assertEquals(al(), rangeAnd(al(al(0, 5)), al(al(6, 7))));
        assertEquals(al(al(0, 1), al(3, 4)), rangeAnd(al(al(0, 4)), al(al(0, 1), al(3, 4))));
    }

    @Test
    public void testRangeXor() {
        assertEquals(al(), rangeXor(al(al(0, 5)), al(al(0, 5))));
        assertEquals(al(al(0, 1), al(4, 5)), rangeXor(al(al(0, 5)), al(al(1, 4))));
        assertEquals(al(al(4, 5)), rangeXor(al(al(0, 5)), al(al(0, 4))));
        assertEquals(al(al(0, 6)), rangeXor(al(al(0, 5)), al(al(5, 6))));
        assertEquals(al(al(-1, 5)), rangeXor(al(al(0, 5)), al(al(-1, 0))));
        assertEquals(al(al(0, 5), al(6, 7)), rangeXor(al(al(0, 5)), al(al(6, 7))));
    }


}
