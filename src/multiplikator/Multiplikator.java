package multiplikator;

import petrinet.*;

import java.util.*;

class Multiplikator {
    private static PetriNet<String> net;
    boolean finished = false;

    Multiplikator(Integer N, Integer M) {
        HashMap<String, Integer> initial = new HashMap<>();
        initial.put("FIRST_NUMBER", N);
        initial.put("SECOND_NUMBER", M);
        initial.put("FIRST_CYCLER", 1);
        net = new PetriNet<>(initial, false);
    }

    void run() {
        //We create our NET
        //There are four Transitions excluding the finishing one
        HashSet<String> terminatorInhibitors = new HashSet<>(Arrays.asList("FIRST_NUMBER", "SECOND_CYCLER", "CENTER"));
        Transition<String> terminator = new Transition<>(Collections.emptyMap(),
                Collections.emptySet(), terminatorInhibitors, Collections.emptyMap());

        HashMap<String, Integer> leftInput = new HashMap<>();
        leftInput.put("FIRST_CYCLER", 1);
        leftInput.put("CENTER", 1);
        HashMap<String, Integer> leftOutput = new HashMap<>();
        leftOutput.put("FIRST_CYCLER", 1);
        leftOutput.put("SECOND_NUMBER", 1);

        Transition<String> left = new Transition<>(leftInput,
                Collections.emptySet(), Collections.emptySet(), leftOutput);

        HashMap<String, Integer> rightInput = new HashMap<>();
        rightInput.put("SECOND_CYCLER", 1);
        rightInput.put("SECOND_NUMBER", 1);
        HashMap<String, Integer> rightOutput = new HashMap<>();
        rightOutput.put("SECOND_CYCLER", 1);
        rightOutput.put("RESULT", 1);
        rightOutput.put("CENTER", 1);

        Transition<String> right = new Transition<>(rightInput,
                Collections.emptySet(), Collections.emptySet(), rightOutput);

        HashMap<String, Integer> topInput = new HashMap<>();
        topInput.put("SECOND_CYCLER", 1);
        HashMap<String, Integer> topOutput = new HashMap<>();
        topOutput.put("FIRST_CYCLER", 1);
        HashSet<String> topInhibitors = new HashSet<>();
        topInhibitors.add("SECOND_NUMBER");

        Transition<String> top = new Transition<>(topInput,
                Collections.emptySet(), topInhibitors, topOutput);

        HashMap<String, Integer> bottomInput = new HashMap<>();
        bottomInput.put("FIRST_NUMBER", 1);
        bottomInput.put("FIRST_CYCLER", 1);
        HashMap<String, Integer> bottomOutput = new HashMap<>();
        bottomOutput.put("SECOND_CYCLER", 1);
        HashSet<String> bottomInhibitors = new HashSet<>();
        bottomInhibitors.add("CENTER");

        Transition<String> bottom = new Transition<>(bottomInput,
                Collections.emptySet(), bottomInhibitors, bottomOutput);

        HashSet<Transition<String>> transitions = new HashSet<>();
        transitions.add(left);
        transitions.add(right);
        transitions.add(top);
        transitions.add(bottom);

        List<Process> processList = new ArrayList<>();
        processList.add(new Process(net, "FIRST", transitions));
        processList.add(new Process(net, "SECOND", transitions));
        processList.add(new Process(net, "THIRD", transitions));
        processList.add(new Process(net, "FOURTH", transitions));

        List<Thread> threads = new ArrayList<>();

        for (Process process : processList)
            threads.add(new Thread(process));

        for (Thread thread : threads) {
            thread.start();
        }
        try {
            net.fire(Collections.singleton(terminator));
        } catch (InterruptedException e) {
            System.out.println("Main thread has been interrupted");
        }
        System.out.println(net.getTokens("RESULT"));

        for (Thread thread : threads) {
            thread.interrupt();
        }
    }
}
