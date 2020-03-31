package petrinet;

import java.util.*;
import java.util.concurrent.Semaphore;

class semaphoredTransitions<T> {
    private LinkedList<Semaphore> stoppedSemaphores = new LinkedList<>();
    private LinkedList<Collection<Transition<T>>> transitionsList = new LinkedList<>();
    //private LinkedList<>

    semaphoredTransitions() {
    }


    Semaphore addTransitions(Collection<Transition<T>> t) throws InterruptedException {
        transitionsList.add(t);
        Semaphore sem = new Semaphore(0);
        stoppedSemaphores.add(sem);
        return sem;
    }

    private void deleteTransition(int i) {
        stoppedSemaphores.remove(i);
        transitionsList.remove(i);
    }

    void deleteSemaphore(Semaphore sem) {
        int i = findSemaphore(sem);
        if (i != -1) {
            deleteTransition(i);
        }
    }

    private boolean checkIfEnabled(Collection<Transition<T>> transitions, Map<T, Integer> markings) {
        for (Transition<T> t : transitions) {
            if (t.isEnabled(markings)) {
                return true;
            }
        }
        return false;
    }

    int findSemaphore(Semaphore mutex) {
        int i = 0;
        for (Semaphore sem : stoppedSemaphores) {
            if (sem == mutex) {
                return i;
            }
            i++;
        }
        return -1;
    }

    boolean releaseOldest(Map<T, Integer> markings) {
        int i = 0;
        for (Collection<Transition<T>> t : transitionsList) {
            if (checkIfEnabled(t, markings)) {
                Semaphore temp = stoppedSemaphores.get(i);
                transitionsList.remove(i);
                stoppedSemaphores.remove(i);
                temp.release();
                return true;
            }
            i++;
        }
        return false;
    }
}
