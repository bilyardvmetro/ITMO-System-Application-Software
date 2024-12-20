package com.weblab2.weblab2;


public class MathFunctions {

    public static boolean hitCheck(float x, float y, float r){
        return (checkCircle(x, y, r) || checkRectangle(x, y, r) || checkTriangle(x, y, r));
    }

    private static boolean checkCircle(float x, float y, float r){
        return (x >= -r/2) && (x <= 0) && (y >= 0) && (y <= r/2*(Math.sqrt(1-4*Math.pow(x, 2)/Math.pow(r, 2))));
    }

    private static boolean checkRectangle(float x, float y, float r){
        return (x >= -r) && (x <= 0) && (y >= -r) && (y <= 0);
    }

    private static boolean checkTriangle(float x, float y, float r){
        return (x >= 0) && (x <= r) && (y >= 0) && (y <= r) && (y <= -x + r);
    }
}
