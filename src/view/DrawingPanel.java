package view;

import java.awt.Color;
import java.awt.Graphics;

import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import javax.swing.JPanel;

import model.AndrewsAlgorithm;
import model.IncrementalAlgorithm;
import model.MelkmansAlgorithm;
import model.Point;

public class DrawingPanel extends JPanel implements Observer {
    private Point pointSet[] = new Point[0];
    private Point[] hull = new Point[0];
    private Point next = new Point();
    private double[] range = new double[] {20,20};
    private double[] average = new double[] {10,10};
    
    public DrawingPanel() {
        super();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Plot the points
        for(int i = 0; i < pointSet.length; i++) {
            g.drawOval((int) pointSet[i].getX() - 5, (int) pointSet[i].getY() - 5, 10, 10);
        }
        
        // Plot the hull
        if(hull.length > 1) {
            for (int i = 0; i < hull.length - 1; i++) {
                Point p1 = convertCoordinates(hull[i]);
                Point p2 = convertCoordinates(hull[i+1]);
                g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
            }
        }
        
        // Plot the considered point
        if(hull.length != 0) {
            g.setColor(Color.RED);
            Point last = convertCoordinates(hull[hull.length - 1]);
            g.drawLine((int) last.getX(), (int) last.getY(), (int) next.getX(), (int) next.getY());
        }
    }
    
    private Point convertCoordinates(Point input) {
        double x = ((input.getX() + average[0])/range[0]) * getWidth();
        double y = ((input.getY() + average[1])/range[1]) * getHeight();
        return new Point(x, y);
    }
    
    private void computeRange(Point[] aPointSet) {
        double[] min = new double[2];
        double[] max = new double[2];
        min[0] = aPointSet[0].getX();
        min[1] = aPointSet[0].getY();
        max[0] = min[0];
        max[1] = min[1];
        for(int i = 0; i < aPointSet.length; i++){
            if(aPointSet[i].getX() < min[0])
                min[0] = aPointSet[i].getX();
            if(aPointSet[i].getY() < min[1])
                min[1] = aPointSet[i].getY();            
            if(aPointSet[i].getX() > max[0])
                max[0] = aPointSet[i].getX();
            if(aPointSet[i].getY() > max[1])
                max[1] = aPointSet[i].getY();
        }
        range[0] = max[0] - min[0];
        range[1] = max[1] - min[1];
        average[0] = -min[0];
        average[1] = -min[1];
    }

    @Override
    public void update(Observable observable, Object object) {
        // Update the point set
        Point[] aPointSet= ((IncrementalAlgorithm)observable).getPointSet();
        computeRange(aPointSet);
        pointSet = new Point[aPointSet.length];
        for(int i = 0; i < pointSet.length; i++) {
            pointSet[i] = convertCoordinates(aPointSet[i]);
        }
        
        // Update hull
        Object[] tmp = ((IncrementalAlgorithm)observable).getHull();
        hull = new Point[tmp.length];
        for(int i = 0; i < tmp.length; i++) {
            hull[i] = (Point)tmp[i];
        }
        
        // Update next considered point
        next = convertCoordinates(((IncrementalAlgorithm)observable).getNextConsidered());
        
        repaint();
    }
}
