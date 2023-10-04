package org.lr2;

import java.util.Arrays;

public class NumberConverter {

    public static final int MAX_BASE = 32;
    // В двоичном представлении чисел с плавающей точкой старший бит мантиссы всегда равен 1 и хранится неявно (IEEE 754)
    public static final int IMPLICIT_BITS = 1;

    private static class Overflow {
        int value = 0;
    }

    public static void main(String[] args) {
        int fromBase = 0;
        int toBase = 0;
        String number = null;

        for (var i = 0; i < args.length; i++) {
            if (args[i].equals("--from-base") && args[i + 1] != null) {
                fromBase = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("--to-base") && args[i + 1] != null) {
                toBase = Integer.parseInt(args[i + 1]);
                i++;
            } else {
                number = args[i].replace(',', '.');
            }
        }

        if (number == null) {
            System.out.println("Usage:");
            System.out.println("command --from-base <2-32> --to-base <2-32> <number>");
            return;
        }

        if (number.contains(".")) {
            System.out.println("Value: " + convertFloatToBase(number, fromBase, toBase));
            System.out.println("Machine float format (4 + 8 bits): " + convertFloatToMachineRepresentation(number, fromBase, 3, 8));
        } else {
            System.out.println("Value: " + convertIntToBase(number, fromBase, toBase));
            System.out.println("Machine integer format (64 bits): " + convertIntToMachineRepresentation(number, fromBase, 64));
        }
    }

    public static String convertFloatToBase(String value, int fromBase, int toBase) {
        var parts = value.split("\\.", 2);
        final var integer = parts[0];
        var fraction = parts.length > 1 ? parts[1] : null;
        String exponent = null;
        if (fraction != null) {
            parts = fraction.split("e", 2);
            fraction = parts[0];
            exponent = parts.length > 1 ? parts[1] : null;
        }

        final var overflow = new Overflow();
        final var convertedFraction = fraction != null ? convertFractionToBase(fraction, fromBase, toBase, overflow) : null;
        final var convertedInteger = convertIntToBase(integer, fromBase, toBase, overflow);

        final var convertedExponent = exponent != null ? convertIntToBase(exponent, fromBase, toBase) : null;

        return convertedInteger
                + (fraction != null ? "." + convertedFraction : "")
                + (exponent != null ? "e" + convertedExponent : "");
    }

    private static String convertFractionToBase(String value, int fromBase, int toBase, Overflow overflowResult) {
        final var input = fromChar(value.toCharArray());

        return new String(toChar(convertFractionToBase(input, fromBase, toBase, overflowResult)));
    }

    private static int[] convertFractionToBase(int[] input, int fromBase, int toBase, Overflow overflowResult) {
        final var output = new int[getNewLength(input.length, fromBase, toBase) + 1];

        var overflow = 0;
        for (var i = 0; i < output.length; i++) {
            overflow = multiple(input, fromBase, toBase);

            output[i] = overflow;
        }

        if (overflow >= toBase / 2) {
            overflowResult.value = add(output, new int[]{1, 0}, toBase);
        }

        return Arrays.stream(output).limit(output.length - 1).toArray();
    }

    private static int add(int[] op1, int[] op2, int base) {
        var overflow = 0;
        for (var i = 0; i < op1.length; i++) {
            var op2Pos = op2.length - 1 - i;
            var op1Pos = op1.length - 1 - i;
            var op2Item = (op2Pos >= 0 ? op2[op2Pos] : 0) + overflow;
            op1[op1Pos] += op2Item % base + base;
            overflow = op2Item / base + op1[op1Pos] / base - 1;
            op1[op1Pos] %= base;
        }

        return overflow;
    }

    public static String convertFloatToMachineRepresentation(String value, int fromBase, int expBits, int mantissaBits) {
        var sign = "0";

        var negative = value.charAt(0) == '-';
        if (negative) {
            value = value.substring(1);
            sign = "1";
        }

        // Разделяем введенное значение на целую часть, дробную и экспоненту
        var parts = value.split("\\.", 2);
        var integer = parts[0];
        var fraction = parts.length > 1 ? parts[1] : null;
        String exponent = null;
        if (fraction != null) {
            parts = fraction.split("e", 2);
            fraction = parts[0];
            exponent = parts.length > 1 ? parts[1] : null;
        }

        final var overflow = new Overflow();

        // Конвертируем соответствующим образом
        var convertedFraction = fraction != null ? convertFractionToBase(fromChar(fraction), fromBase, 2, overflow) : null;
        var convertedInteger = convertIntToBase(fromChar(integer), fromBase, 2, overflow);
        var convertedExponent = exponent != null ? convertIntToBase(fromChar(exponent), fromBase, 2) : new int[expBits];

        // собираем мантиссу и дополняем нулями слева, на случай, если она короче mantissaBits
        var mantissa = new int[mantissaBits + convertedInteger.length + (convertedFraction != null ? convertedFraction.length : 0) + mantissaBits];
        System.arraycopy(convertedInteger, 0, mantissa, mantissaBits, convertedInteger.length);
        if (convertedFraction != null) {
            System.arraycopy(convertedFraction, 0, mantissa, mantissaBits + convertedInteger.length, convertedFraction.length);
        }

        int expFix = convertedInteger.length - 1;

        // не забываем отсечь старший бит, который всегда равен 1
        var trimmedMantissa = new int[mantissaBits];
        if (expFix == 0) {
            for (var i = mantissaBits; mantissa[i] == 0; i++) {
                if (i >= mantissa.length - mantissaBits) {
                    expFix = 0;
                    break;
                }
                expFix--;
            }

            System.arraycopy(mantissa, mantissaBits - expFix + IMPLICIT_BITS, trimmedMantissa, 0, mantissaBits);
        } else {
            System.arraycopy(mantissa, mantissaBits + IMPLICIT_BITS, trimmedMantissa, 0, mantissaBits);
        }

        // проверка переполнения экспоненты
        var overflowExpfix = add(convertedExponent, new int[]{expFix}, 2);

        // Если экспонента меньше нуля, то вычитаем 1, так как -1 - зарезервировано для +-inf
        if (convertedExponent[0] == 1) {
            add(convertedExponent, new int[]{-1}, 2);
        }

        if (overflowExpfix > 0) {
            Arrays.fill(convertedExponent, 1);
            trimmedMantissa = new int[mantissaBits];
        }

        return String.join(" ", sign, new String(toChar(convertedExponent)), new String(toChar(trimmedMantissa)));
    }

    private static void not(int[] value) {
        for (var i = 0; i < value.length; i++) {
            value[i] = value[i] == 1 ? 0 : 1;
        }
    }

    public static String convertIntToMachineRepresentation(String value, int fromBase, int bits) {
        var sign = "0";

        boolean negative = value.charAt(0) == '-';
        if (negative) {
            value = value.substring(1);
            sign = "1";
        }

        var convertedInteger = convertIntToBase(fromChar(value), fromBase, 2);

        var result = new int[bits];

        if (convertedInteger.length > bits) {
            convertedInteger = Arrays.stream(convertedInteger).skip(convertedInteger.length - bits).toArray();
        }

        System.arraycopy(convertedInteger, 0, result, result.length - convertedInteger.length, convertedInteger.length);

        if (negative) {
            not(result);
            add(result, new int[]{1}, 2);
        }

        return new String(toChar(result));
    }

    public static String convertIntToBase(String value, int fromBase, int toBase) {
        return convertIntToBase(value, fromBase, toBase, new Overflow());
    }

    public static String convertIntToBase(String value, int fromBase, int toBase, Overflow overflow) {
        final var input = fromChar(value);
        final var output = convertIntToBase(input, fromBase, toBase, overflow);

        return new String(toChar(output));
    }

    public static int[] convertIntToBase(int[] value, int fromBase, int toBase) {
        return convertIntToBase(value, fromBase, toBase, new Overflow());
    }

    public static int[] convertIntToBase(int[] value, int fromBase, int toBase, Overflow overflow) {
        final var zero = new int[value.length];

        final var output = new int[getNewLength(value.length, fromBase, toBase) + 1];

        var i = output.length;
        while (!Arrays.equals(value, zero) || i == output.length) {
            var mod = divide(value, fromBase, toBase);
            output[i - 1] = mod;
            i--;
        }

        add(output, new int[]{overflow.value}, toBase);

        if (output[i - 1] > 0) {
            i--;
        }

        return Arrays.stream(output).skip(i).toArray();
    }

    private static int getNewLength(int length, int fromBase, int toBase) {
        return (int) Math.ceil(Math.log(fromBase) * length / Math.log(toBase));
    }

    private static int divide(int[] input, int fromBase, int div) {
        var mod = 0;

        for (var j = 0; j < input.length; j++) {
            var num = input[j] + mod * fromBase;
            mod = num % div;
            input[j] = num / div;
        }

        return mod;
    }

    private static int multiple(int[] input, int fromBase, int multiplier) {
        var overflow = 0;

        for (var i = input.length - 1; i >= 0; i--) {
            var num = input[i] * multiplier + overflow;
            input[i] = num % fromBase;
            overflow = num / fromBase;
        }

        return overflow;
    }

    private static char toChar(int value) {
        if (value > MAX_BASE) {
            throw new RuntimeException();
        } else if (value > 9) {
            return (char)('A' + value - 10);
        } else {
            return (char)('0' + value);
        }
    }

    private static int[] fromChar(char[] value) {
        var result = new int[value.length];
        for (var i = 0; i < value.length; i++) {
            result[i] = fromChar(value[i]);
        }

        return result;
    }

    private static int[] fromChar(String value) {
        return fromChar(value.toCharArray());
    }

    private static char[] toChar(int[] value, int maxLength) {
        var result = new char[maxLength];
        for (var i = 0; i < maxLength; i++) {
            result[i] = toChar(value[i]);
        }

        return result;
    }

    private static char[] toChar(int[] value) {
        return toChar(value, value.length);
    }

    private static int fromChar(char value) {
        value = Character.toUpperCase(value);
        if (value >= 'A' + MAX_BASE - 10) {
            throw new RuntimeException();
        } else if (value >= 'A') {
            return value - 'A' + 10;
        } else {
            return value - '0';
        }
    }
}


