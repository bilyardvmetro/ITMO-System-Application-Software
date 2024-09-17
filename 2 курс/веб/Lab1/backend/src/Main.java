package backend.src;

public class Main {
    public static void main(String[] args) {
        var FcgiServer = new FcgiServer();
        FcgiServer.run();

//        String[] equalities = new String[3];
//        equalities[0] = "x=3";
//        equalities[1] = "y=2";
//        equalities[2] = "z=";
//
//        for (String equality : equalities) {
//            String[] keyValue = equality.split("=");
//            System.out.println(keyValue[0] + "    " + keyValue[1]);
//        }

    }
}
