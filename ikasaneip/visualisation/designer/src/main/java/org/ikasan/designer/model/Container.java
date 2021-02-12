package org.ikasan.designer.model;

import java.util.ArrayList;

public class Container {
    private ArrayList<Figure> figures;
    private int x;
    private int y;
    private int windowx;
    private int windowy;

    public ArrayList<Figure> getFigures() {
        return figures;
    }

    public void setFigures(ArrayList<Figure> figures) {
        this.figures = figures;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWindowx() {
        return windowx;
    }

    public void setWindowx(int windowx) {
        this.windowx = windowx;
    }

    public int getWindowy() {
        return windowy;
    }

    public void setWindowy(int windowy) {
        this.windowy = windowy;
    }
}
