package org.lr1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordGrid {

    public static class GridLocation {
        public final int row, column;

        public GridLocation(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public int hashCode() {
            final var prime = 31;
            int result = 1;
            result = prime * result + column;
            result = prime * result + row;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            GridLocation other = (GridLocation) obj;
            if (column != other.column) {
                return false;
            }
            if (row != other.row) {
                return false;
            }

            return true;
        }
    }

    private final char ALPHABET_LENGTH = 26;
    private final char FIRST_LETTER = 'a';
    private final char FIRST_LETTER_UPPER = 'A';
    private final int rows, columns;
    private final boolean colors;
    private final char[][] grid;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";

    public WordGrid(int rows, int columns, boolean colors) {
        this.rows = rows;
        this.columns = columns;
        this.colors = colors;
        grid = new char[rows][columns];
        // инициализируем сетку случайными буквами
        var random = new Random();
        for (var row = 0; row < rows; row++) {
            for (var column = 0; column < columns; column++) {
                var randomLetter = (char) (random.nextInt(ALPHABET_LENGTH)
                        + (colors ? FIRST_LETTER : FIRST_LETTER_UPPER));
                grid[row][column] = randomLetter;
            }
        }
    }

    public WordGrid(int rows, int columns) {
        this(rows, columns, false);
    }

    public void mark(String word, List<GridLocation> locations) {
        for (var i = 0; i < word.length(); i++) {
            var location = locations.get(i);
            grid[location.row][location.column] = word.charAt(i);
        }
    }

    // получаем красивую печатную версию сетки
    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var rowArray : grid) {
            for (var c : rowArray) {
                if (colors) {
                    if (c >= 'A' && c <= 'Z') {
                        sb.append(ANSI_WHITE);
                        sb.append(ANSI_BLACK_BACKGROUND);
                        sb.append(c);
                        sb.append(ANSI_RESET);
                    } else {
                        sb.append(ANSI_BLACK);
                        sb.append(c);
                        sb.append(ANSI_RESET);
                    }
                    sb.append(' ');
                } else {
                    sb.append(c);
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public List<List<GridLocation>> generateDomain(String word) {
        var domain = new ArrayList<List<GridLocation>>();
        var length = word.length();

        for (var row = 0; row < rows; row++) {
            for (var column = 0; column < columns; column++) {
                if (column + length <= columns) {
                    // слева направо
                    fillRight(domain, row, column, length);
                    // по диагонали снизу направо
                    if (row + length <= rows) {
                        fillDiagonalRight(domain, row, column, length);
                    }
                }
                if (row + length <= rows) {
                    // сверху вниз
                    fillDown(domain, row, column, length);
                    // по диагонали снизу налево
                    if (column - length >= 0) {
                        fillDiagonalLeft(domain, row, column, length);
                    }
                }
            }
        }
        return domain;
    }

    private void fillRight(List<List<GridLocation>> domain, int row, int column, int length) {
        var locations = new ArrayList<GridLocation>();
        for (var c = column; c < (column + length); c++) {
            locations.add(new GridLocation(row, c));
        }
        domain.add(locations);
    }

    private void fillDiagonalRight(List<List<GridLocation>> domain, int row, int column, int length) {
        var locations = new ArrayList<GridLocation>();
        int r = row;
        for (var c = column; c < (column + length); c++) {
            locations.add(new GridLocation(r, c));
            r++;
        }

        domain.add(locations);
    }

    private void fillDown(List<List<GridLocation>> domain, int row,
                          int column, int length) {
        var locations = new ArrayList<GridLocation>();
        for (var r = row; r < (row + length); r++) {
            locations.add(new GridLocation(r, column));
        }
        domain.add(locations);
    }

    private void fillDiagonalLeft(List<List<GridLocation>> domain,
                                  int row, int column, int length) {
        var locations = new ArrayList<GridLocation>();
        var c = column;
        for (var r = row; r < (row + length); r++) {
            locations.add(new GridLocation(r, c));
            c--;
        }
        domain.add(locations);
    }
}