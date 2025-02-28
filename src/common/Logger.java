package common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Logger class provides a simple logging utility that outputs messages
 * to the console with a timestamp.
 */
public class Logger {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void log(String message) {
        System.out.println("[" + dateFormat.format(new Date()) + "] " + message);
    }
}