package petrinet;

import java.util.concurrent.Semaphore;

//Helper class that helps with interrupt when waiting for a mutex
public class mutexReigniter<T> implements Runnable {
    private Semaphore threadMutex;
    private PetriNet<T> net;


    mutexReigniter(Semaphore threadMutex, PetriNet<T> net) {
        this.threadMutex = threadMutex;
        this.net = net;
    }

    @Override
    public void run() {
        net.getMutex().acquireUninterruptibly();
        int i = net.getStoppedFirings().findSemaphore(threadMutex);
        if (i != -1) {
            threadMutex.release();
        } else {
            net.releaseNext();
        }
    }
}
