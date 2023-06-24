package org.lr1;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class WordSearchConstraintTest {

    @Test
    public void generateSolution() {
        var solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news"), new WordGrid(5, 5));

        assertEquals(4, solution.size());

        List<WordGrid.GridLocation> allLocations = solution.values().stream()
                .flatMap(Collection::stream).toList();
        Set<WordGrid.GridLocation> allLocationsSet = new HashSet<>(allLocations);

        assertEquals(18, allLocationsSet.size());

        assertNotNull(solution.get("what"));
        assertNotNull(solution.get("which"));
        assertNotNull(solution.get("their"));
        assertNotNull(solution.get("news"));

        assertEquals(4, solution.get("what").size());
        assertEquals(5, solution.get("which").size());
        assertEquals(5, solution.get("their").size());
        assertEquals(4, solution.get("news").size());

        solution = WordSearchConstraint.generateSolution(Arrays.asList("what", "which", "their", "news"), new WordGrid(4, 4));

        assertNull(solution);
    }
}
