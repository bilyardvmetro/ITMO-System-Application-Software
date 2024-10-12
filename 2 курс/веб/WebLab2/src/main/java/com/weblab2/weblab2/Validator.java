package com.weblab2.weblab2;

public class Validator {

    public static boolean checkX(int x){
        return x < -3 || x > 5;
    }

    public static boolean checkY(float y){
        return y < -5 || y > 3;
    }

    public static boolean checkR(float r){
        return r < 1 || r > 5;
    }
}
