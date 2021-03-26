package com.toptal.backend.util.helpers;

/**
 * Contains a group of strings related methods
 *
 * @author ehab
 */
public class StringUtil {

    /**
     * Returns true if a string is either null or nothing but white space
     */
    public static boolean isNullOrWhiteSpace(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Returns true if a string is either null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Returns false if a string is either null or empty
     */
    public static boolean isntNullNorEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    /**
     * Returns false if a string is either null or nothing but white space
     */
    public static boolean isntNullNorWhiteSpace(String s) {
        return !isNullOrWhiteSpace(s);
    }

    /**
     * Appends a list of strings
     */
    public static String appendAll(String... args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (isntNullNorEmpty(arg)) {
                sb.append(arg);
            }
        }
        return sb.toString();
    }

    /**
     * Creates a formatted string by inserting values to specific locations in a given string format
     * Insertion in order and locations are marked by %s
     */
    public static String format(final String format, final Object... args) {
        String[] split = format.split("%s");
        StringBuilder msg = new StringBuilder();
        for (int pos = 0; pos < split.length - 1; pos += 1) {
            msg.append(split[pos]);
            msg.append(args[pos]);
        }
        msg.append(split[split.length - 1]);
        if (args.length == split.length) {
            msg.append(args[args.length - 1]);
        }
        return msg.toString();
    }
}