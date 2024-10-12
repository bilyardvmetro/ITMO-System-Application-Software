package com.weblab2.weblab2;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerConfig {
    private static Logger logger;

    public static Logger getLogger(String name) {
        logger = Logger.getLogger(name);
        logger.setLevel(Level.INFO);

        return logger;
    }

}
