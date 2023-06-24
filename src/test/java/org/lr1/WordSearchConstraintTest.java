package org.lr1;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class WordSearchConstraintTest {

    @Test
    public void generateSolution() {
        var solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news"), new WordGrid(5, 5));

        assertEquals(4, solution.size());

        // Проверяем что слова не пересекаются
        var allLocations = solution.values().stream()
                .flatMap(Collection::stream).toList();
        var allLocationsSet = new HashSet<>(allLocations);

        assertEquals(18, allLocationsSet.size());

        var what = solution.get("what");
        var which = solution.get("which");
        var their = solution.get("their");
        var news = solution.get("news");

        // что все слова расставлены
        assertNotNull(what);
        assertNotNull(which);
        assertNotNull(their);
        assertNotNull(news);

        // что количество ячеек равно количеству букв
        assertEquals(4, what.size());
        assertEquals(5, which.size());
        assertEquals(5, their.size());
        assertEquals(4, news.size());

        // что буквы в слове не перемешаны и расположены на одной прямой
        var directionH = what.get(0).row - what.get(1).row;
        var directionV = what.get(0).column - what.get(1).column;

        assertEquals(directionH, what.get(1).row - what.get(2).row);
        assertEquals(directionH, what.get(2).row - what.get(3).row);
        assertEquals(directionV, what.get(1).column - what.get(2).column);
        assertEquals(directionV, what.get(2).column - what.get(3).column);

        directionH = which.get(0).row - which.get(1).row;
        directionV = which.get(0).column - which.get(1).column;

        assertEquals(directionH, which.get(1).row - which.get(2).row);
        assertEquals(directionH, which.get(2).row - which.get(3).row);
        assertEquals(directionH, which.get(3).row - which.get(4).row);
        assertEquals(directionV, which.get(1).column - which.get(2).column);
        assertEquals(directionV, which.get(2).column - which.get(3).column);
        assertEquals(directionV, which.get(3).column - which.get(4).column);

        directionH = their.get(0).row - their.get(1).row;
        directionV = their.get(0).column - their.get(1).column;

        assertEquals(directionH, their.get(1).row - their.get(2).row);
        assertEquals(directionH, their.get(2).row - their.get(3).row);
        assertEquals(directionH, their.get(3).row - their.get(4).row);
        assertEquals(directionV, their.get(1).column - their.get(2).column);
        assertEquals(directionV, their.get(2).column - their.get(3).column);
        assertEquals(directionV, their.get(3).column - their.get(4).column);

        directionH = news.get(0).row - news.get(1).row;
        directionV = news.get(0).column - news.get(1).column;

        assertEquals(directionH, news.get(1).row - news.get(2).row);
        assertEquals(directionH, news.get(2).row - news.get(3).row);
        assertEquals(directionV, news.get(1).column - news.get(2).column);
        assertEquals(directionV, news.get(2).column - news.get(3).column);

        // что полное заполнение работает
        solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news", "when", "get"), new WordGrid(5, 5));
        assertEquals(6, solution.size());
        // Проверяем что слова не пересекаются
        allLocations = solution.values().stream()
                .flatMap(Collection::stream).toList();
        allLocationsSet = new HashSet<>(allLocations);
        assertEquals(25, allLocationsSet.size());

        // что отсутствие решения определяется за конечное время
        solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news"), new WordGrid(4, 4));

        assertNull(solution);

        // что отсутствие решения определяется за конечное время
        solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news", "when", "view"), new WordGrid(5, 5));

        assertNull(solution);
    }

    @Test
    public void generateFullfilledSolution() {

        // что полное заполнение работает
        var solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news", "when", "get"), new WordGrid(5, 5));
        assertEquals(6, solution.size());
        // Проверяем что слова не пересекаются
        var allLocations = solution.values().stream()
                .flatMap(Collection::stream).toList();
        var allLocationsSet = new HashSet<>(allLocations);
        assertEquals(25, allLocationsSet.size());

        // что отсутствие решения определяется за конечное время
        solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news"), new WordGrid(4, 4));

        assertNull(solution);

        // что отсутствие решения определяется за конечное время
        solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news", "when", "view"), new WordGrid(5, 5));

        assertNull(solution);
    }

    @Test
    public void generateUnresolvableSolution() {
        var solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news"), new WordGrid(4, 4));

        assertNull(solution);
    }

    @Test
    public void generateUnresolvableSolution2() {
        var solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news", "when", "view"), new WordGrid(5, 5));

        assertNull(solution);
    }

    @Test
    public void generateUnresolvableSolution3() {
        var solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news", "when", "view", "online", "people"), new WordGrid(6, 6));

        assertNull(solution);
    }
}
