package alternator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

class Alternator {
    private static PetriNet<String> net;

    Alternator() {
        HashMap<String, Integer> initial = new HashMap<>();
        initial.put("A", 1);
        initial.put("B", 1);
        initial.put("C", 1);
        initial.put("SEMAPHORE", 1);
        initial.put("A_DEREPEATER", 1);
        initial.put("B_DEREPEATER", 1);
        initial.put("C_DEREPEATER", 1);
        net = new PetriNet<>(initial, false);
    }

    void run() {
        List<Process> processList = new ArrayList<>();
        processList.add(new Process(net, "A"));
        processList.add(new Process(net, "B"));
        processList.add(new Process(net, "C"));
        HashSet<Transition<String>> allTransitions = new HashSet<>();
        for (Process p : processList) {
            allTransitions.addAll(p.getTransitions());
        }
        Set<Map<String, Integer>> reachable = net.reachable(allTransitions);
        System.out.println(reachable.size() + " markings are reachable.");
        boolean safe = true;
        for (Map<String, Integer> m : reachable) {
            if (m.getOrDefault("CRITICAL_SECTION", 0) > 1) {
                safe = false;
            }
        }
        if (safe) {
            System.out.println("Every reachable marking meets safety condition.");
        } else {
            System.out.println("A reachable marking does not meet safety condition.");
        }

        List<Thread> threads = new ArrayList<>();

        for (Process process : processList)
            threads.add(new Thread(process));

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            System.out.println("Main thread has been interrupted");
        }

        for (Thread thread : threads) {
            thread.interrupt();
        }

    }
}
