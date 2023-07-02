package org.lr4;


import java.util.*;
import java.util.regex.Pattern;

public class ExpressionsChecker {
    final private static List<String> operators = Arrays.asList("~", "^", "+", "*");

    static class TruthTable {
        boolean[] table;

        public TruthTable(int varCount) {
            this.table = new boolean[(int)Math.pow(2, varCount)];
        }

        public boolean equals(Object obj) {
            if (obj instanceof TruthTable) {
                return Arrays.equals(table, ((TruthTable) obj).table);
            }

            return false;
        }

        public String toString() {
            var builder = new StringBuilder();

            for (var i : table) {
                builder.append(i ? '1' : '0');
            }

            return builder.toString();
        }
    }

    static class Tokenizer extends StringTokenizer {

        List<String> tokens = new ArrayList<>();
        int position = 0;

        public Tokenizer(String str) {
            super(str, " ()~^+*", true);

            while (super.hasMoreTokens()) {
                var token = super.nextToken();
                if (token.equals(" ")) {
                    continue;
                }
                tokens.add(token);
            }
        }

        public String nextToken() {
            var token = tokens.get(position);
            position++;
            return token;
        }

        public boolean hasMoreTokens() {
            return position < tokens.size();
        }

        public String currentToken() {
            return tokens.get(position);
        }
    }

    public static void main(String[] args) {
        LinkedHashMap<String, Boolean> allowedVariables = null;
        TruthTable prevTruthTable = null;

        for (var arg : args) {
            LinkedHashMap<String, Boolean> variables = getVars(arg);

            if (allowedVariables == null) {
                allowedVariables = variables;
            }

            if (!allowedVariables.equals(variables)) {
                throw new RuntimeException("Переменные в выражении " + arg + " должно совпадать с переменными в остальных выражениях (" + allowedVariables + ")");
            }

            var truthTable = getTruthTable(variables, arg);

            if (prevTruthTable == null) {
                prevTruthTable = truthTable;
            }

            if (!truthTable.equals(prevTruthTable)) {
                throw new RuntimeException("Таблица истинности выражения " + arg + " " + truthTable + " не совпадает с таблицей истинности " + prevTruthTable);
            }
        }

        if (prevTruthTable != null && args.length > 1) {
            System.out.println("Все выражения, содержат переменные " + allowedVariables + " и имеют таблицу истинности " + prevTruthTable);
        } else {
            System.out.println("Необходимо указать хотя бы два аргумента");
        }
    }

    public static TruthTable getTruthTable(LinkedHashMap<String, Boolean> variables, String expression) {
        var truthTable = new TruthTable(variables.size());

        for (var i = 0; i < truthTable.table.length; i++) {
            var tokenizer = new Tokenizer(expression);

            var mask = 1;
            for (var variable : variables.entrySet()) {
                variables.put(variable.getKey(), (i & mask) > 0);
                mask <<= 1;
            }

            truthTable.table[i] = eval(variables, tokenizer.nextToken(), tokenizer, 0);
        }
        return truthTable;
    }

    public static boolean eval(Map<String, Boolean> variables, String currentToken, Tokenizer tokenizer, int minPriority) {
        var left = false;

        if (currentToken.equals("~")) {
            left = unaryOperator(eval(variables, tokenizer.nextToken(), tokenizer, minPriority), currentToken);
        }

        if (variables.containsKey(currentToken)) {
            left = variables.get(currentToken);
        }

        if (currentToken.equals("1")) {
            left = true;
        }

        if (currentToken.equals("0")) {
            left = false;
        }

        if (currentToken.equals(")")) {
            throw new RuntimeException("Unexpected token )");
        }

        if (currentToken.equals("(")) {
            if (!tokenizer.hasMoreTokens()) {
                throw new RuntimeException("Unexpected end of string");
            }

            var token = tokenizer.nextToken();

            left = eval(variables, token, tokenizer, 0);

            if (!tokenizer.hasMoreTokens()) {
                throw new RuntimeException("Unexpected end of string");
            }
            tokenizer.nextToken();
        }

        while (tokenizer.hasMoreTokens()) {
            var token = tokenizer.currentToken();

            var operator = "";
            var implicitOperator = false;

            if (variables.containsKey(token) || token.equals("~") || token.equals("(")) {
                operator = "*";
                implicitOperator = true;
            } else if (operators.contains(token)) {
                operator = token;
            } else if (token.equals(")"))  {
                break;
            }

            int priority;

            // Если скобка открылась - внутри нее свои приоритеты
            if (token.equals("(")) {
                priority = 0;
            } else {
                // Иначе берем из таблицы операторов
                priority = operators.indexOf(operator);
                if (priority < minPriority) {
                    // Если приоритет операции ниже текущей (например сложение следом за умножением), то поднимаемся вверх по стеку вызовов
                    break;
                }

                if (!tokenizer.hasMoreTokens()) {
                    throw new RuntimeException("Unexpected end of string: right operator expected");
                }

                if (!implicitOperator) {
                    if (!tokenizer.hasMoreTokens()) {
                        throw new RuntimeException("Unexpected end of string: right operator expected");
                    }

                    tokenizer.nextToken();
                }
            }

            // выполняем подвыражение до тех пор пока не закроется скобка или не наткнемся на оператор более низкого приоритета
            var right = eval(variables, tokenizer.nextToken(), tokenizer, priority);

            left = binaryOperator(left, right, operator);
        }

        return left;
    }

    private static boolean unaryOperator(boolean value, String operator) {
        switch (operator) {
            case "~" -> value = !value;
            default -> throw new RuntimeException("Unknown operator " + operator);
        }

        return value;
    }

    private static boolean binaryOperator(boolean left, boolean right, String operator) {
        switch (operator) {
            case "*" -> left = left && right;
            case "+" -> left = left || right;
            case "^" -> left = left ^ right;
            default -> throw new RuntimeException("Unknown operator " + operator);
        }

        return left;
    }

    public static LinkedHashMap<String, Boolean> getVars(String expression) {
        var validVar = Pattern.compile("[a-z][a-z0-9]*", Pattern.CASE_INSENSITIVE);
        var variables = new LinkedHashMap<String, Boolean>();
        var tokenizer = new StringTokenizer(expression, " ()~^+*", true);
        while (tokenizer.hasMoreTokens()) {
            var token = tokenizer.nextToken();
            if (validVar.matcher(token).matches()) {
                variables.put(token, false);
            }
        }
        return variables;
    }
}


