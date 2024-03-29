package util;

import java.util.Scanner;

public class PromptScan {
    private static Scanner userScanner;

    public static Scanner getUserScanner() {
        return userScanner;
    }

    public static void setUserScanner(Scanner userScanner) {
        PromptScan.userScanner = userScanner;
    }
}
