package model;


import java.util.Observable;
import java.util.Stack;

public class AndrewsAlgorithm extends IncrementalAlgorithm implements Runnable {
    private Stack<Point> hull;

    public AndrewsAlgorithm(Object aSignal) {
        super();
        signal = aSignal;
        nextPointToConsider = 0;
        hull = new Stack<Point>();
        hullComputed = false;
    }

    public void setPoints(Point[] aPointSet) {
        pointSet = aPointSet;

        hull.push(pointSet[nextPointToConsider++]); // First point
        hull.push(pointSet[nextPointToConsider++]); // Second point
        setChanged();
        notifyObservers();
    }

    @Override
    public void run() {
        boolean halfDone = false;
        try {
            while(!hullComputed) {
                Point p1, p2, p3;
                p3 = pointSet[nextPointToConsider]; // next point to consider
                if (halfDone)
                    nextPointToConsider--;
                else
                    nextPointToConsider++;

                if (nextPointToConsider == pointSet.length) {
                    halfDone = true;
                    nextPointToConsider = pointSet.length - 1;
                }
                if (halfDone && nextPointToConsider == -1) {
                    nextPointToConsider = 0;
                    hullComputed = true;
                }

                do {
                    setChanged();
                    notifyObservers();
                    synchronized (signal) {
                        signal.wait();
                    }
                    p2 = hull.pop(); // last point in the partial hull
                    if (hull.isEmpty())
                        break;
                    p1 = hull.peek(); // before last point int the partial hull
                } while (vectProduct(p1, p2, p3) < 0);
                hull.push(p2);
                hull.push(p3);

                setChanged();
                notifyObservers();

            }
        } catch (InterruptedException e) {
            //Shouldn't happen
        }
        deleteObservers();
    }
    
    public double vectProduct(Point p1, Point p2, Point p3) {
        // Projection on z of the vectorial product of P1P2 with P1P3
        // Right turn = Positive z value
        return 
            (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) -
            (p3.getX() - p1.getX()) * (p2.getY() - p1.getY());
    }


    public Point[] getPointSet() {
        return pointSet;
    }

    public Object[] getHull() {
        return hull.toArray();
    }

    public Point getNextConsidered() {
        return pointSet[nextPointToConsider];
    }
}
