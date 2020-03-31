package petrinet;

import java.util.*;
import java.util.concurrent.Semaphore;

public class PetriNet<T> {

    private Map<T, Integer> markings;
    private Semaphore mutex = new Semaphore(1);
    private semaphoredTransitions<T> stoppedFirings = new semaphoredTransitions<>();
    private Queue<Map<T, Integer>> BFS = new LinkedList<>();

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        markings = new HashMap<>(initial);
        for (T key : initial.keySet()) {
            if (markings.get(key) == 0) {
                markings.remove(key);
            }
        }
    }

    //Breadth First Search over transition collection
    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {

        HashSet<Map<T, Integer>> reachableMarkings = new HashSet<>();
        Map<T, Integer> curr = new HashMap<>(markings);

        reachableMarkings.add(curr);
        BFS.add(curr);

        while (!BFS.isEmpty()) {
            curr = BFS.poll();
            for (Transition<T> it : transitions) {
                if (it.isEnabled(curr)) {
                    Map<T, Integer> temp = new HashMap<>(curr);
                    Auxiliaries.fireTransition(temp, it);
                    if (!reachableMarkings.contains(temp)) {
                        reachableMarkings.add(temp);
                        BFS.add(temp);
                    }
                }
            }

        }
        return reachableMarkings;
    }

    private Transition<T> chooseFiredTransition(Collection<Transition<T>> transitions) {

        Optional<Transition<T>> result = transitions.stream().filter(t -> t.isEnabled(markings)).findAny();
        return result.orElse(null);
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {

        mutex.acquire();
        Transition<T> fired = chooseFiredTransition(transitions);

        if (fired == null) {

            Semaphore temp = stoppedFirings.addTransitions(transitions);
            mutex.release();
            try {
                temp.acquire();
            } catch (InterruptedException e) {

                Thread reigniter = new Thread(new mutexReigniter<T>(temp, this));
                reigniter.start();
                temp.acquireUninterruptibly();
                stoppedFirings.deleteSemaphore(temp);
                releaseNext();
                throw e;
            }
            fired = chooseFiredTransition(transitions);
        }
        Auxiliaries.fireTransition(markings, fired);
        releaseNext();
        return fired;
    }

    void releaseNext() {

        if (!stoppedFirings.releaseOldest(markings)) {
            mutex.release();
        }
    }

    Semaphore getMutex() {
        return mutex;
    }

    public Integer getTokens(T key) {
        return markings.getOrDefault(key, 0);
    }


    semaphoredTransitions<T> getStoppedFirings() {
        return stoppedFirings;
    }
}