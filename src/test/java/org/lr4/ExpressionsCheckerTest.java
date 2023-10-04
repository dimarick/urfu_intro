package org.lr4;

import junit.framework.TestCase;

import java.util.LinkedHashMap;

public class ExpressionsCheckerTest extends TestCase {
    public void testGetVars() {
        assertEquals(createVars(new String[]{"a", "b"}, new Boolean[]{false, false}), ExpressionsChecker.getVars("a + b"));
        assertEquals(createVars(new String[]{"a", "b", "c"}, new Boolean[]{false, false, false}), ExpressionsChecker.getVars("a b c"));
        assertEquals(createVars(new String[]{"a", "b", "c", "d"}, new Boolean[]{false, false, false, false}), ExpressionsChecker.getVars("a ^ b + c d"));
        assertEquals(createVars(new String[]{"a", "b", "c", "d"}, new Boolean[]{false, false, false, false}), ExpressionsChecker.getVars("a ^ b + c d * 1"));
        assertEquals(createVars(new String[]{"a", "b", "c", "d"}, new Boolean[]{false, false, false, false}), ExpressionsChecker.getVars("a ^ b + c d * 1"));
        assertEquals(createVars(new String[]{"a2", "b1"}, new Boolean[]{false, false}), ExpressionsChecker.getVars("a2 ^ b1"));
        assertEquals(createVars(new String[]{"a2", "b1testVar", "b", "c"}, new Boolean[]{false, false, false, false}), ExpressionsChecker.getVars("a2 + (b1testVar b) c"));
    }

    public void testGetTruthTable() {
        assertEquals("01001000", getTruthTable("(x2*x3)^x2^x1^(x1*x3)"));
        assertEquals("01100000", getTruthTable("x2^x1^x2*x3^x1*x3"));
        assertEquals("01", getTruthTable("a"));
        assertEquals("10", getTruthTable("~a"));
        assertEquals("01", getTruthTable("~~a"));
        assertEquals("10", getTruthTable("~~~a"));
        assertEquals("01", getTruthTable("(a)"));
        assertEquals("10", getTruthTable("~(a)"));
        assertEquals("0001", getTruthTable("a b"));
        assertEquals("0001", getTruthTable("(a b)"));
        assertEquals("0001", getTruthTable("a * b"));
        assertEquals("0111", getTruthTable("a + b"));
        assertEquals("0110", getTruthTable("a ^ b"));
        /*
         *  0 + 0 0 = 0
         *  1 + 0 0 = 1
         *  0 + 1 0 = 0
         *  1 + 1 0 = 1
         *  0 + 0 1 = 0
         *  1 + 0 1 = 1
         *  0 + 1 1 = 1
         *  1 + 1 1 = 1
         */
        assertEquals("01010111", getTruthTable("a + b c"));
        /*
         *  0 0 + 0 = 0
         *  1 0 + 0 = 0
         *  0 1 + 0 = 0
         *  1 1 + 0 = 1
         *  0 0 + 1 = 1
         *  1 0 + 1 = 1
         *  0 1 + 1 = 1
         *  1 1 + 1 = 1
         */
        assertEquals("00011111", getTruthTable("a b + c"));
        /*
         *  0 (0 + 0) = 0
         *  1 (0 + 0) = 0
         *  0 (1 + 0) = 0
         *  1 (1 + 0) = 1
         *  0 (0 + 1) = 0
         *  1 (0 + 1) = 1
         *  0 (1 + 1) = 0
         *  1 (1 + 1) = 1
         */
        assertEquals("00010101", getTruthTable("a(b + c)"));
        assertEquals("00010101", getTruthTable("a*(b + c)"));
        assertEquals("00010101", getTruthTable("a*(b+c)"));
        /*
         *  0(0 ^ 0 + 0) = 0
         *  1(0 ^ 0 + 0) = 0
         *  0(1 ^ 0 + 0) = 0
         *  1(1 ^ 0 + 0) = 1
         *  0(0 ^ 1 + 0) = 0
         *  1(0 ^ 1 + 0) = 1
         *  0(1 ^ 1 + 0) = 0
         *  1(1 ^ 1 + 0) = 0
         *  0(0 ^ 0 + 1) = 0
         *  1(0 ^ 0 + 1) = 1
         *  0(1 ^ 0 + 1) = 0
         *  1(1 ^ 0 + 1) = 0
         *  0(0 ^ 1 + 1) = 0
         *  1(0 ^ 1 + 1) = 1
         *  0(1 ^ 1 + 1) = 0
         *  1(1 ^ 1 + 1) = 0
         */
        assertEquals("0001010001000100", getTruthTable("a(b^c+d)"));
        assertEquals("0001010001000100", getTruthTable("a(b^(c+d))"));
        /*
         *  0((0 ^ 0) + 0) = 0
         *  1((0 ^ 0) + 0) = 0
         *  0((1 ^ 0) + 0) = 0
         *  1((1 ^ 0) + 0) = 1
         *  0((0 ^ 1) + 0) = 0
         *  1((0 ^ 1) + 0) = 1
         *  0((1 ^ 1) + 0) = 0
         *  1((1 ^ 1) + 0) = 0
         *  0((0 ^ 0) + 1) = 0
         *  1((0 ^ 0) + 1) = 1
         *  0((1 ^ 0) + 1) = 0
         *  1((1 ^ 0) + 1) = 1
         *  0((0 ^ 1) + 1) = 0
         *  1((0 ^ 1) + 1) = 1
         *  0((1 ^ 1) + 1) = 0
         *  1((1 ^ 1) + 1) = 1
         */
        assertEquals("0001010001010101", getTruthTable("a((b^c)+d)"));
        assertEquals("1110101110101010", getTruthTable("~(a((b^c)+d))"));
    }


    private static String getTruthTable(String expression) {
        return ExpressionsChecker.getTruthTable(ExpressionsChecker.getVars(expression), expression).toString();
    }


    private static LinkedHashMap<String, Boolean> createVars(String[] keys, Boolean[] values) {
        var map = new LinkedHashMap<String, Boolean>();

        for (var i = 0;  i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }
}