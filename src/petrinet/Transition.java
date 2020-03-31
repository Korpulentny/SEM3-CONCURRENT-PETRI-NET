package petrinet;

import java.util.Collection;
import java.util.Map;


public class Transition<T> {
    private Map<T, Integer> inputArcs, outputArcs;
    private Collection<T> resetArcs, inhibitorArcs;


    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        inputArcs = input;
        outputArcs = output;
        resetArcs = reset;
        inhibitorArcs = inhibitor;
    }

    boolean isEnabled(Map<T, Integer> markings) {
        for (T it : inhibitorArcs) {
            if (markings.get(it) != null) {
                return false;
            }
        }
        for (T it : inputArcs.keySet()) {
            Integer val = markings.get(it);
            if (val == null || val < inputArcs.get(it)) {
                return false;
            }
        }
        return true;

    }

    Map<T, Integer> getInputArcs() {
        return inputArcs;
    }

    Map<T, Integer> getOutputArcs() {
        return outputArcs;
    }

    Collection<T> getResetArcs() {
        return resetArcs;
    }
}