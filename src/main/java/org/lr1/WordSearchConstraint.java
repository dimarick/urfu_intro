package org.lr1;

import org.lr1.WordGrid.GridLocation;

import java.io.*;
import java.util.*;

public class WordSearchConstraint extends Constraint<String, List<GridLocation>> {
    public WordSearchConstraint(List<String> words) {
        super(words);
    }

    HashSet<GridLocation> cache;
    Map<String, List<GridLocation>> cachedAssignment;

    @Override
    public boolean satisfied(Map<String, List<GridLocation>> assignment, List<GridLocation> value) {
        if (cachedAssignment != assignment) {
            cache = null;
            cachedAssignment = assignment;
        }

        if (cache == null) {
            // объединение всех GridLocations в один огромный список
            var allLocations = assignment.values().stream()
                    .flatMap(Collection::stream).toList();
            // наличие дубликатов положений сетки означает наличие совпадения
            cache = new HashSet<>(allLocations);
        }

        for (var item : value) {
            if (cache.contains(item)) {
                return false;
            }

            cache.add(item);
        }

        return true;
    }

    public static void main(String[] args) {
        var words = getWords();
        var grid = new WordGrid(50, 50, true);
        var solution = generateSolution(words, grid);
        if (solution == null) {
            System.out.println("No solution found!");
        } else {
            var random = new Random();
            for (var item : solution.entrySet()) {
                var word = item.getKey();
                var locations = item.getValue();
                // в половине случаев случайным выбором — задом наперед
                if (random.nextBoolean()) {
                    Collections.reverse(locations);
                }
                grid.mark(word, locations);
            }
            System.out.println(grid);
        }
    }

    public static Map<String, List<GridLocation>> generateSolution(List<String> words, WordGrid grid)
    {
        // генерация доменов для всех слов
        var domains = new HashMap<String, List<List<GridLocation>>>();
        for (String word : words) {
            domains.put(word, grid.generateDomain(word));
        }

        var csp = new CSP<>(words, domains);
        csp.addConstraint(new WordSearchConstraint(words));

        return csp.backtrackingSearch();
    }

    private static List<String> getWords() {
        try(
            var fileReader = new FileReader("./google-10000-english.txt");
            var reader = new BufferedReader(fileReader)
        ) {

            var result = new ArrayList<String>();
            for (var line = reader.readLine(); line != null; line = reader.readLine()) {
                result.add(line.toUpperCase());
            }

            Collections.shuffle(result);

            return result.subList(0, 220);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
