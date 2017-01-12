package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;

import model.Point;
import model.AndrewsAlgorithm;
import model.IncrementalAlgorithm;
import model.MelkmansAlgorithm;

import view.Window;

public class Controller extends Observable{
    private Window window;
    private static final int NUMBER_OF_POINTS = 20;
    private Point[] pointSet;
    private boolean computing;
    private boolean ready;
    private Object signal;
    private Thread notifyingThread;
    private Thread algorithmThread;
    private boolean melkmans;

    public Controller() {
        window = new Window(this);
        window.setVisible(true);
        addObserver(window);
        computing = false;
        ready = false;
        melkmans = false;
        signal = new Object();
    }
    
    public void createRandomSet() {
        if(!computing) {
            // Random points generation
            pointSet = new Point[NUMBER_OF_POINTS];
            Random randGenerator = new Random();
            for(int i = 0; i < NUMBER_OF_POINTS; i++) {
                double x = 20 * (randGenerator.nextDouble() - 0.5);
                double y = 20 * (randGenerator.nextDouble() - 0.5);
                pointSet[i] = new Point(x, y);
            }
            // Sort those points
            mergeSort(pointSet);
            startNewAlgorithm();
        }
    }
    
    public void loadSetFromFile(String filename) {
        if(!computing) {
            try {
                // Read file
                FileReader fileReader = new FileReader(filename);
                BufferedReader textReader = new BufferedReader(fileReader);
                String line;
                LinkedList<String[]> coordinatesList = new LinkedList<String[]>();
                while((line = textReader.readLine()) != null) {
                    String[] coordinates = line.split(" ");
                    if(coordinates.length != 2){
                        System.out.println("Wrong file format");
                        return;
                    }
                    coordinatesList.push(coordinates);
                }
                
                // Create the point set
                pointSet = new Point[coordinatesList.size()];
                for(int i = 0; i < pointSet.length; i++) {
                    pointSet[i] = new Point(Double.parseDouble(coordinatesList.get(i)[0]), Double.parseDouble(coordinatesList.get(i)[1]));
                }
                
                mergeSort(pointSet);
                startNewAlgorithm();
            } catch (FileNotFoundException e) {
                // Shouldn't happen
            } catch (IOException e) {
                System.out.println("Wrong file format");
            }
        }
    }
    
    public void execute() {
        // Starts a thread that periodically signals the algorithm thread to step up
        computing = true;
        notifyingThread = new Thread(new NotifyingTask(signal));
        notifyingThread.start();
    }
    
    public void stopExec() {
        // Stop the notifying thread
        computing = false;
        notifyingThread.interrupt();
    }
    
    public void nextStep() {
        // Manually step into the algorithm
        synchronized(signal) {
            signal.notify();
        }
  }
    
    public boolean isReadyToExecute() {
        // Did we already load some points?
        return ready;
    }
    
    public boolean isComputing() {
        // Is the algorith running?
        return computing;
    }
    
    private void startNewAlgorithm(){
        if(ready)
            algorithmThread.interrupt();
        IncrementalAlgorithm task;
        if(melkmans) {
            task = new MelkmansAlgorithm(signal);
        } else {
            task = new AndrewsAlgorithm(signal);
        }

        setChanged();
        notifyObservers(task);
        task.setPoints(pointSet);
        algorithmThread = new Thread(task);
        algorithmThread.start();
        ready = true;
    }
    
    public void setAlgorithmToMelkmans(boolean choice) {
            melkmans = choice;
    }
    
    // Merge sort
    public static void mergeSort(Point[] unsortedArray) {
        Point[] tmp = new Point[unsortedArray.length];
        mergeSort(unsortedArray, tmp, 0, unsortedArray.length - 1);
    }
            
    public static void mergeSort(Point[] unsortedArray, Point[] tmp, int start, int end) {
        if(start < end) {
            int center = (start + end)/2;
            mergeSort(unsortedArray, tmp, start, center);
            mergeSort(unsortedArray, tmp, center+1, end);
            merge(unsortedArray, tmp, start, center+1, end);
        }
    }
    
    public static void merge(Point[] unsortedArray, Point[] tmp, int left, int right, int rightEnd) {
        int tmpIndex = left;
        int leftEnd = right - 1;
        int objectSorted = rightEnd - left + 1;

        while(left <= leftEnd && right <= rightEnd) {
            if(unsortedArray[left].getX() < unsortedArray[right].getX()) {
                tmp[tmpIndex++] = unsortedArray[left++];
            } else if (unsortedArray[left].getX() == unsortedArray[right].getX()){
                if(unsortedArray[left].getY() < unsortedArray[right].getY()) {
                    tmp[tmpIndex++] = unsortedArray[left++];
                } else {
                    tmp[tmpIndex++] = unsortedArray[right++];
                }
            } else {
                tmp[tmpIndex++] = unsortedArray[right++];
            }
        }
        
        while(left <= leftEnd)
            tmp[tmpIndex++] = unsortedArray[left++];
        while(right <= rightEnd)
            tmp[tmpIndex++] = unsortedArray[right++];
        
        for(int i = 0; i < objectSorted; i++)
            unsortedArray[rightEnd-i] = tmp[rightEnd-i]; // reverse copy because we still know rightEnd but not left
    }
    
    
    //--------------------------------------------------------------------------------------------------------------Main
    public static void main(String[] args) {
        Controller controller = new Controller();
    }
}
