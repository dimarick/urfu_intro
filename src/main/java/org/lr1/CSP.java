package org.lr1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSP<V, D> {
    private List<V> variables;
    private Map<V, List<D>> domains;
    private Map<V, List<Constraint<V, D>>> constraints = new HashMap<>();

    public CSP(List<V> variables, Map<V, List<D>> domains) {
        this.variables = variables;
        this.domains = domains;
        for (V variable : variables) {
            constraints.put(variable, new ArrayList<>());
            if (!domains.containsKey(variable)) {
                throw new IllegalArgumentException("Every variable should have a domain assigned to it.");
            }
        }
    }

    public void addConstraint(Constraint<V, D> constraint) {
        for (V variable : constraint.variables) {
            if (!variables.contains(variable)) {
                throw new IllegalArgumentException("Variable in constraint not in CSP");
            }
            constraints.get(variable).add(constraint);
        }
    }

    // Проверяем, соответствует ли присваивание значения,
    // проверяя все ограничения для данной переменной
    public boolean consistent(V variable, Map<V, D> assignment) {
        for (Constraint<V, D> constraint : constraints.get(variable)) {
            if (!constraint.satisfied(assignment)) {
                return false;
            }
        }
        return true;
    }

    public Map<V, D> backtrackingSearch(Map<V, D> assignment) {
        // присваивание завершено, если существует присваивание
        // для каждой переменной (базовый случай)

        if (assignment.size() == variables.size()) {
            return assignment;
        }
        // получить все переменные из CSP, но не из присваивания
        V unassigned = variables.stream().filter(v ->
                !assignment.containsKey(v)).findFirst().get();
        // получить все возможные значения области определения
        // для первой переменной без присваивания
        for (D value : domains.get(unassigned)) {
            // мелкая копия присваивания, которую мы можем изменить
            Map<V, D> localAssignment = new HashMap<>(assignment);
            localAssignment.put(unassigned, value);
            // если нет противоречий, продолжаем рекурсию
            if (consistent(unassigned, localAssignment)) {
                Map<V, D> result = backtrackingSearch(localAssignment);
                // если результат не найден, заканчиваем возвраты
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // вспомогательный класс функции backtrackingSearch
    public Map<V, D> backtrackingSearch() {
        return backtrackingSearch(new HashMap<>());
    }
}
