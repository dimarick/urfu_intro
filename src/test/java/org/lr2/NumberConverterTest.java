package org.lr2;

import junit.framework.TestCase;

public class NumberConverterTest extends TestCase {

    public void testConvertIntToBase() {
        assertEquals("35E51C81", NumberConverter.convertIntToBase("QUA741", 32, 16));
        assertEquals("35E51C81392043CCBB8DA3709B845F9A80B0A82BC4926DF7", NumberConverter.convertIntToBase("QUA74174G47J5RHMHN16S4BUD81C585F294RFN", 32, 16));
        assertEquals("QUA74174G47J5RHMHN16S4BUD81C585F294RFN", NumberConverter.convertIntToBase("35E51C81392043CCBB8DA3709B845F9A80B0A82BC4926DF7", 16, 32));
        var value = NumberConverter.convertIntToBase("QUA74174G47J5RHMHN16S4BUD81C585F294RFN", 32, 31);
        value = NumberConverter.convertIntToBase(value, 31, 15);
        value = NumberConverter.convertIntToBase(value, 15, 16);
        value = NumberConverter.convertIntToBase(value, 16, 2);
        value = NumberConverter.convertIntToBase(value, 2, 3);
        value = NumberConverter.convertIntToBase(value, 3, 8);
        value = NumberConverter.convertIntToBase(value, 8, 29);
        value = NumberConverter.convertIntToBase(value, 29, 20);
        value = NumberConverter.convertIntToBase(value, 20, 16);
        value = NumberConverter.convertIntToBase(value, 16, 16);
        value = NumberConverter.convertIntToBase(value, 16, 12);
        value = NumberConverter.convertIntToBase(value, 12, 11);
        value = NumberConverter.convertIntToBase(value, 11, 32);
        assertEquals("QUA74174G47J5RHMHN16S4BUD81C585F294RFN", value);
    }

    public void testConvertFloatToBase() {
        assertEquals("111.11111111", NumberConverter.convertFloatToBase("21.22222", 3, 2));
        assertEquals("6BCA.3902", NumberConverter.convertFloatToBase("QUA.741", 32, 16));
        assertEquals("0.9992", NumberConverter.convertFloatToBase("0.AAA", 11, 10));
        assertEquals("6BCA.3902e1E", NumberConverter.convertFloatToBase("QUA.741eU", 32, 16));
        assertEquals("27594.22269e30", NumberConverter.convertFloatToBase("QUA.741eU", 32, 10));
        var value = NumberConverter.convertFloatToBase("QUA74174G47J5.RHMHN16S4BUD81C585eF294RFN", 32, 31);
        value = NumberConverter.convertFloatToBase(value, 31, 15);
        value = NumberConverter.convertFloatToBase(value, 15, 16);
        value = NumberConverter.convertFloatToBase(value, 16, 2);
        value = NumberConverter.convertFloatToBase(value, 2, 3);
        value = NumberConverter.convertFloatToBase(value, 3, 8);
        value = NumberConverter.convertFloatToBase(value, 8, 29);
        value = NumberConverter.convertFloatToBase(value, 29, 20);
        value = NumberConverter.convertFloatToBase(value, 20, 16);
        value = NumberConverter.convertFloatToBase(value, 16, 16);
        value = NumberConverter.convertFloatToBase(value, 16, 12);
        value = NumberConverter.convertFloatToBase(value, 12, 11);
        value = NumberConverter.convertFloatToBase(value, 11, 32);
        assertEquals("QUA74174G47J5.RHMHN16S4BUD81C585eF294RFN", value.replaceFirst("(.*\\..{18}).*(e.*)", "$1$2"));
    }

    public void testConvertFloatToMachineRepresentation() {
        assertEquals("0 00000 00000000", NumberConverter.convertFloatToMachineRepresentation("0", 32, 5, 8));
        assertEquals("0 010 11000000", NumberConverter.convertFloatToMachineRepresentation("21", 3, 3, 8));
        assertEquals("1 010 11000000", NumberConverter.convertFloatToMachineRepresentation("-21", 3, 3, 8));
        assertEquals("0 010 11111111", NumberConverter.convertFloatToMachineRepresentation("21.22222", 3, 3, 8));
        assertEquals("1 010 11111111", NumberConverter.convertFloatToMachineRepresentation("-21.22222", 3, 3, 8));
        assertEquals("0 111 00000000", NumberConverter.convertFloatToMachineRepresentation("QUA.741", 32, 3, 8));
        assertEquals("0 110 11111111", NumberConverter.convertFloatToMachineRepresentation("0.AAA", 11, 3, 8));
        assertEquals("1 1101101 11001000", NumberConverter.convertFloatToMachineRepresentation("-0.000741", 32, 7, 8));
        assertEquals("0 11111 00000000", NumberConverter.convertFloatToMachineRepresentation("QUA.741eU", 32, 5, 8));
        assertEquals("1 11111 00000000", NumberConverter.convertFloatToMachineRepresentation("-QUA.741eU", 32, 5, 8));
    }

    public void testConvertIntToMachineRepresentation() {
        assertEquals("00000111", NumberConverter.convertIntToMachineRepresentation("21", 3, 8));
        assertEquals("11111001", NumberConverter.convertIntToMachineRepresentation("-21", 3, 8));
        assertEquals("11001010", NumberConverter.convertIntToMachineRepresentation("QUA", 32, 8));
        assertEquals("0110101111001010", NumberConverter.convertIntToMachineRepresentation("QUA", 32, 16));
        assertEquals("11100111010010100101111001010110", NumberConverter.convertIntToMachineRepresentation("-35E51C81392043CCBB8DA", 32, 32));
        assertEquals("11111111111111111111111111111111", NumberConverter.convertIntToMachineRepresentation("-1", 32, 32));
        assertEquals("00000000000000000000000000000001", NumberConverter.convertIntToMachineRepresentation("1", 32, 32));
        assertEquals("00000000000000000000000000000000", NumberConverter.convertIntToMachineRepresentation("-0", 32, 32));
        assertEquals("00000000000000000000000000000000", NumberConverter.convertIntToMachineRepresentation("0", 32, 32));
    }
}
