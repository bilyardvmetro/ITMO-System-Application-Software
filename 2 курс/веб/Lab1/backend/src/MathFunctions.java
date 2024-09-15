package backend.src;


public class MathFunctions {

    public static boolean hitCheck(int x, float y, float r){
        return (checkCircle(x, y, r) || checkRectangle(x, y, r) || checkTriangle(x, y, r));
    }

    private static boolean checkCircle(int x, float y, float r){
        return (x >= 0) && (x <= r/2) && (y >= -r/2*(Math.sqrt(1-4*Math.pow(x, 2)/Math.pow(r, 2)))) && (y <= 0);
    }

    private static boolean checkRectangle(int x, float y, float r){
        return (x >= -r) && (x <= 0) && (y >= -r/2) && (y <= 0);
    }

    private static boolean checkTriangle(int x, float y, float r){
        return (x >= -r/2) && (x <= 0) && (y >= 0) && (y <= r/2) && (y <= x + r/2);
    }
}
