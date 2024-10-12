package com.weblab2.weblab2;

public class TableRow {
    private int x;
    private float y;
    private int r;

    private boolean result;

    public TableRow(int x, float y, int r, boolean result) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.result = result;
    }

    public int getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public boolean getResult() {
        return result;
    }
}
