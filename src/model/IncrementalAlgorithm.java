package model;

import java.util.Observable;

public abstract class IncrementalAlgorithm extends Observable implements Runnable {
    protected Object signal;
    protected Point[] pointSet;
    protected int nextPointToConsider;
    protected boolean hullComputed;
    
    public double vectProduct(Point p1, Point p2, Point p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p3.getX() - p1.getX()) * (p2.getY() - p1.getY());
    }
    
    public abstract void setPoints(Point[] aPointSet);


    public Point[] getPointSet() {
        return pointSet;
    }

    public abstract Object[] getHull();

    public Point getNextConsidered() {
        return pointSet[nextPointToConsider];
    }
}
