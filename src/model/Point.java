package model;

public class Point {
    private double x;
    private double y;
    
    public Point() {
        x = 0;
        y = 0;
    }
    
    public Point(double aX, double aY) {
        x = aX;
        y = aY;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    @Override
    public String toString(){
        return "x:" + x + ", y:" + y;
    }
}
