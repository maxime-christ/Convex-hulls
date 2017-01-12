package model;

import java.util.ArrayDeque;

public class MelkmansAlgorithm extends IncrementalAlgorithm implements Runnable {
    private ArrayDeque<Point> hull;

    public MelkmansAlgorithm(Object aSignal) {
        super();
        signal = aSignal;
        hull = new ArrayDeque<Point>();
        nextPointToConsider = 0;
        hullComputed = false;
    }

    @Override
    public void run() {
        try {
            synchronized (signal) {
                signal.wait();
            }
            while (!hullComputed) {

                Point p3 = pointSet[nextPointToConsider++];
                if (nextPointToConsider == pointSet.length) {
                    nextPointToConsider--;
                    hullComputed = true;
                }
            
                Point p2 = hull.pollLast();
                Point p1 = hull.pollLast();
                while (vectProduct(p1, p2, p3) < 0) {
                    hull.addLast(p1);
                    hull.addLast(p2);
                    hull.addLast(p3);
                    setChanged();
                    notifyObservers();
                    hull.pollLast();
                    hull.pollLast();
                    hull.pollLast();
                    synchronized (signal) {
                        signal.wait();
                    }
                    p2 = p1;
                    p1 = hull.pollLast();
                }
                hull.addLast(p1);
                hull.addLast(p2);
                hull.addLast(p3);

                setChanged();
                notifyObservers();
                synchronized (signal) {
                    signal.wait();
                }

                p1 = p3;
                p2 = hull.pollFirst();
                p3 = hull.pollFirst();
                while (vectProduct(p1, p2, p3) < 0) {
                    hull.addFirst(p3);
                    hull.addFirst(p2);
                    hull.addFirst(p1);
                    setChanged();
                    notifyObservers();
                    hull.pollFirst();
                    hull.pollFirst();
                    hull.pollFirst();
                    synchronized (signal) {
                        signal.wait();
                    }
                    p2 = p3;
                    p3 = hull.pollFirst();
                }
                hull.addFirst(p3);
                hull.addFirst(p2);
                hull.addFirst(p1);
                setChanged();
                notifyObservers();
            }
        } catch (InterruptedException e) {
            // Shouldn't happen
        }
        deleteObservers();
    }

    public void setPoints(Point[] aPointSet) {
        pointSet = aPointSet;

        // init algorithm
        Point p1 = pointSet[0];
        Point p2 = pointSet[1];
        Point p3 = pointSet[2];

        if (vectProduct(p1, p2, p3) > 0) {
            hull.addLast(p1);
            hull.addLast(p2);
        } else {
            hull.addLast(p2);
            hull.addLast(p1);
        }
        hull.addFirst(p3);
        hull.addLast(p3);
        nextPointToConsider = 3;
        setChanged();
        notifyObservers();
    }

    @Override
    public Object[] getHull() {
        return hull.toArray();
    }
}
