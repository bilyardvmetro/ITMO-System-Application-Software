package backend.src;

public class Validator {

    public static boolean checkX(int x){
        return x < -4 || x > 4;
    }

    public static boolean checkY(float y){
        return y < -5 || y > 3;
    }

    public static boolean checkR(float r){
        return r < 1 || r > 4;
    }
}
