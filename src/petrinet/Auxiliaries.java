package petrinet;

import java.util.Collection;
import java.util.Map;

class Auxiliaries {
    private static <T> void changeMarking(T key, Integer val, Map<T, Integer> map) {
        Integer result = map.get(key);
        if (result == null) {
            result = val;
            map.put(key, result);
        } else {
            result += val;
            if (result == 0) {
                map.remove(key);
            } else {
                map.replace(key, result);
            }
        }
    }

    private static <T> void decreaseMarkings(Map<T, Integer> input, Map<T, Integer> map) {
        for (T key : input.keySet()) {
            changeMarking(key, -input.get(key), map);
        }
    }

    private static <T> void increaseMarkings(Map<T, Integer> output, Map<T, Integer> map) {
        for (T key : output.keySet()) {
            changeMarking(key, output.get(key), map);
        }
    }

    private static <T> void resetMarkings(Collection<T> reset, Map<T, Integer> map) {
        for (T key : reset) {
            map.remove(key);
        }
    }

    static <T> void fireTransition(Map<T, Integer> map, Transition<T> transition) {
        Auxiliaries.decreaseMarkings(transition.getInputArcs(), map);
        Auxiliaries.resetMarkings(transition.getResetArcs(), map);
        Auxiliaries.increaseMarkings(transition.getOutputArcs(), map);
    }

}
