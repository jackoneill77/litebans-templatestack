package xyz.jackoneill.litebans.templatestack.util;

import java.util.logging.Logger;

public class Log {

    private static Logger logger;
    private static boolean debug = false;

    public static void set(Logger argLogger) {
        logger = argLogger;
    }

    public static void error(String msg) {
        logger.severe(msg);
    }

    public static void warning(String msg) {
        logger.warning(msg);
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void debug(String msg) {
        if (debug) {
            logger.info("[DEBUG] " + msg);
        }
    }

    public static void setDebug(boolean enabled) {
        debug = enabled;
        if (enabled) {
            debug("Debug mode is enabled");
        }
    }
}
