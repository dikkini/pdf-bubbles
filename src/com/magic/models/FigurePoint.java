package com.magic.models;

/**
 * Created by dikkini on 29.08.13.
 */
public class FigurePoint {

    private Integer id;
    private float x;
    private float y;

    public FigurePoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public FigurePoint(Integer id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
