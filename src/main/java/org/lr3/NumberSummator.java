package org.lr3;

import org.lr2.NumberConverter;

import java.util.Arrays;

public class NumberSummator {
    public static void main(String[] args) {
        String number1 = null;
        String number2 = null;

        for (String arg : args) {
            if (number1 == null) {
                number1 = arg;
            } else if (number2 == null) {
                number2 = arg;
            }
        }

        if (number1 == null) {
            System.out.println("Usage:");
            System.out.println("command <number1> <number2>");
            return;
        }

        final var result = add(number1, number2);

        System.out.println(number1 + " + (" + number2 + ") = " + result);
    }

    private static String add(String number1, String number2) {
        final var sign1 = new NumberConverter.Sign();
        final var sign2 = new NumberConverter.Sign();
        final var sign = new NumberConverter.Sign();
        final var dest = NumberConverter.fromChar(number1, sign1);
        final var src = NumberConverter.fromChar(number2, sign2);
        int size = Math.max(dest.length, src.length) + 1;
        final var signedDest = makeSigned(dest, sign1, size);
        final var signedSrc = makeSigned(src, sign2, size);

        NumberConverter.add(signedDest, signedSrc, 2);

        final var unsignedResult = makeUnsigned(signedDest, sign);

        return (!sign.positive ? "-" : "") + new String(NumberConverter.toChar(unsignedResult));
    }

    /**
     * Преобразуем отрицательные числа в дополнительный код
     */
    private static int[] makeSigned(int[] unsigned, NumberConverter.Sign sign, int size) {
        final var result = new int[size + 1];
        final var offset = result.length - unsigned.length;

        if (!sign.positive) {
            Arrays.fill(result, 1);

            for (var i = 0; i < unsigned.length; i++) {
                var v = unsigned[i];
                if (v == 0) {
                    v = 1;
                } else {
                    v = 0;
                }

                result[i + offset] = v;
            }

            final var one = new int[result.length];
            one[one.length - 1] = 1;

            NumberConverter.add(result, one, 2);
        } else {
            System.arraycopy(unsigned, 0, result, offset, unsigned.length);
        }

        return result;
    }

    /**
     * Преобразуем отрицательные числа из дополнительного кода
     */
    private static int[] makeUnsigned(int[] signed, NumberConverter.Sign sign) {
        final var result = new int[signed.length - 1];

        if (signed[0] == 0) {
            sign.positive = true;
            System.arraycopy(signed, 1, result, 0, result.length);
        } else {
            sign.positive = false;
            final var minusOne = new int[result.length];
            Arrays.fill(minusOne, 1);

            var signedCopy = signed.clone();
            NumberConverter.add(signedCopy, minusOne, 2);

            for (var i = 1; i < signedCopy.length; i++) {
                var v = signedCopy[i];
                if (v == 0) {
                    v = 1;
                } else {
                    v = 0;
                }

                result[i - 1] = v;
            }
        }

        return result;
    }
}
