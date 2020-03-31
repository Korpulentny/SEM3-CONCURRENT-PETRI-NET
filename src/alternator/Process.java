package alternator;

import petrinet.*;

import java.util.*;

//Creates corresponding transitions and tries to print its name in infinite loop
public class Process implements Runnable {
    private PetriNet<String> petriNet;
    private String name;
    private Set<Transition<String>> transitions;
    private HashSet<String> names = new HashSet<String>(Arrays.asList("A", "B", "C"));


    Process(PetriNet<String> petriNet, String name) {
        this.name = name;
        this.petriNet = petriNet;
        createTransitions();
    }

    private void createTransitions() {
        HashMap<String, Integer> starterInput = new HashMap<>();
        starterInput.put(name, 1);
        starterInput.put(name + "_DEREPEATER", 1);
        starterInput.put("SEMAPHORE", 1);
        HashMap<String, Integer> starterOutput = new HashMap<>();
        starterOutput.put("CRITICAL_SECTION", 1);
        starterOutput.put(name + "_EXITTER", 1);
        Transition<String> starter = new Transition<String>(starterInput,
                Collections.emptySet(), Collections.emptySet(), starterOutput);

        HashMap<String, Integer> finishInput = new HashMap<>();
        finishInput.put(name + "_EXITTER", 1);
        finishInput.put("CRITICAL_SECTION", 1);

        HashSet<String> finishReset = new HashSet<>();
        HashMap<String, Integer> finishOutput = new HashMap<>();
        finishOutput.put("SEMAPHORE", 1);
        finishOutput.put(name, 1);
        for (String s : names) {
            if (!name.equals(s)) {
                finishReset.add(s + "_DEREPEATER");
                finishOutput.put(s + "_DEREPEATER", 1);
            }
        }
        Transition<String> finisher = new Transition<>(finishInput,
                finishReset, Collections.emptySet(), finishOutput);
        transitions = new HashSet<>();
        transitions.add(starter);
        transitions.add(finisher);

    }

    Set<Transition<String>> getTransitions() {
        return transitions;
    }

    @Override
    public void run() {
        try {
            while (true) {
                petriNet.fire(transitions);
                System.out.print(name);
                System.out.print(".");
                petriNet.fire(transitions);
            }
        } catch (InterruptedException e) {
            System.out.print("Thread " + name + " has been interrupted");
        }
    }
}
