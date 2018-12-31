package com.example.andre.mobilemouse;

/**
 * Coordinates for a swipe.
 * Used by the scroll and swipe areas
 */
public class SwipeCoordinate {
    private double x;
    private double y;
    private boolean isSwipe;

    /**
     * Constructor
     */
    public SwipeCoordinate() {
        x = 0;
        y = 0;
        this.isSwipe = false;
    }

    /**
     * Checks if it was a swipe or a touch
     * @return the boolean for whether it was a swipe
     */
    public boolean isSwipe() {
        return isSwipe;
    }

    /**
     * Calculates differnece in x and updates coordinate
     * @param x the double representing the new x
     * @return the double representing the difference in x
     */
    public double getXChange(double x) {
        double diff = x - this.x;
        this.x = x;
        if (diff >= 1)
            this.isSwipe = true;
        return diff;
    }

    /**
     * Calculates the difference in y and updates coordinate
     * @param y the double representing the new y
     * @return the double representing the difference in y
     */
    public double getYChange(double y) {
        double diff = x - this.y;
        this.y = y;
        if (diff >= 1)
            this.isSwipe = true;
        return diff;
    }

}
