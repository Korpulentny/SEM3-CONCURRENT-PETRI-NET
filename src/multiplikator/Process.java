package multiplikator;

import petrinet.*;

import java.util.*;

public class Process implements Runnable {
    private PetriNet<String> petriNet;
    private String name;
    private Set<Transition<String>> transitions;
    private Integer counter = 0;


    Process(PetriNet<String> petriNet, String name, Set<Transition<String>> transitions) {
        this.name = name;
        this.petriNet = petriNet;
        this.transitions = transitions;
    }

    @Override
    public void run() {
        try {
            while (true) {

                petriNet.fire(transitions);
                counter++;

            }

        } catch (InterruptedException e) {
            System.out.println("Thread " + name + " has been interrupted with total " + counter + " executions of fire.");
        }
    }
}
