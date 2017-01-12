package controller;

public class NotifyingTask implements Runnable {
    private Object signal;
    public NotifyingTask(Object aSignal) {
        super();
        signal = aSignal;
    }

    @Override
    public void run() {
        try {
            for(;;) {
                Thread.sleep(250);
                synchronized (signal) {
                    signal.notify();
                }
            }
        } catch (InterruptedException e) {
            //Do nothing
        }
    }
}
