package com.andrew.game;

public class StringPoint {

    public String x;
    public String y;

    /**
     * Constructs a new 2D grid point.
     */
    public StringPoint() {
    }

    /**
     * Constructs a new 2D grid point.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public StringPoint(String x, String y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor
     *
     * @param point The 2D grid point to make a copy of.
     */
    public StringPoint(StringPoint point) {
        x = point.x;
        y = point.y;
    }

    /**
     * Sets the coordinates of this 2D grid point to that of another.
     *
     * @param point The 2D grid point to copy the coordinates of.
     * @return this 2D grid point for chaining.
     */
    public StringPoint set(StringPoint point) {
        x = point.x;
        y = point.y;
        return this;
    }

    /**
     * Sets the coordinates of this 2D grid point.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return this 2D grid point for chaining.
     */
    public StringPoint set(String x, String y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
