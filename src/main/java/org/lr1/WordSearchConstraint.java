package org.lr1;

import org.lr1.WordGrid.GridLocation;

import java.io.*;
import java.util.*;

public class WordSearchConstraint extends Constraint<String, List<GridLocation>> {
    public WordSearchConstraint(List<String> words) {
        super(words);
    }

    @Override
    public boolean satisfied(Map<String, List<GridLocation>> assignment) {
        // объединение всех GridLocations в один огромный список
        List<GridLocation> allLocations = assignment.values().stream()
                .flatMap(Collection::stream).toList();
        // наличие дубликатов положений сетки означает наличие совпадения
        Set<GridLocation> allLocationsSet = new HashSet<>(allLocations);
        // если какие-либо повторяющиеся местоположения сетки найдены,
        // значит, есть перекрытие
        return allLocations.size() == allLocationsSet.size();
    }

    public static void main(String[] args) {
        WordGrid grid = new WordGrid(50, 50, true);
        List<String> words = getWords();
        // генерация доменов для всех слов
        Map<String, List<List<GridLocation>>> domains = new HashMap<>();
        for (String word : words) {
            domains.put(word, grid.generateDomain(word));
        }
        CSP<String, List<GridLocation>> csp = new CSP<>(words, domains);
        csp.addConstraint(new WordSearchConstraint(words));
        Map<String, List<GridLocation>> solution = csp.backtrackingSearch();
        if (solution == null) {
            System.out.println("No solution found!");
        } else {
            Random random = new Random();
            for (var item : solution.entrySet()) {
                String word = item.getKey();
                List<GridLocation> locations = item.getValue();
                // в половине случаев случайным выбором — задом наперед
                if (random.nextBoolean()) {
                    Collections.reverse(locations);
                }
                grid.mark(word, locations);
            }
            System.out.println(grid);
        }
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

            return result.subList(0, 200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
