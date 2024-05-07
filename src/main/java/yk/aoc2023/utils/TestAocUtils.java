package yk.aoc2023.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static yk.aoc2023.utils.AocUtils.cycleBase;
import static yk.jcommon.fastgeom.Vec2i.v2i;
import static yk.ycollections.YArrayList.al;

/**
 * Created by yuri at 2022.12.13
 */
public class TestAocUtils {
    @Test
    public void testTranspose() {
        assertEquals(al(al(0)), al(al(0)).forThis(AocUtils2D::transpose));
        assertEquals(al(al(0, 1, 2)), al(al(0), al(1), al(2)).mapThis(AocUtils2D::transpose));
        assertEquals(al(al(0), al(1), al(2)), al(al(0, 1, 2)).mapThis(AocUtils2D::transpose));
        assertEquals(al(al(0, 1), al(1, 2), al(2, 3)), al(al(0, 1, 2), al(1, 2, 3)).mapThis(AocUtils2D::transpose));
    }

    @Test
    public void testBaseCycle() {
        assertEquals(2, cycleBase(10, 5));
        assertEquals(1, cycleBase(5, 5));
        assertEquals(0, cycleBase(4, 5));
        assertEquals(0, cycleBase(1, 5));

        assertEquals(0, cycleBase(0, 5));

        assertEquals(-1, cycleBase(-1, 5));
        assertEquals(-1, cycleBase(-4, 5));
        assertEquals(-1, cycleBase(-5, 5));
        assertEquals(-2, cycleBase(-6, 5));
        assertEquals(-2, cycleBase(-10, 5));
        assertEquals(-3, cycleBase(-11, 5));

        assertEquals(v2i(-3, 2), cycleBase(v2i(-11, 12), v2i(5, 6)));

    }

}
