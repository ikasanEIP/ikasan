package org.ikasan.dashboard.ui.visualisation.model.business.stream;

public class Boundary {
    private int x;
    private int y;
    private int w;
    private int h;
    private String colour;
    private String label;

    public Boundary(int x, int y, int w, int h, String colour, String label) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.colour = colour;
        this.label = label;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public String getColour() {
        return colour;
    }

    public String getLabel() {
        return label;
    }
}
