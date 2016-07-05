package ru.bigcheese.flscanner.support;

import java.text.SimpleDateFormat;

/**
 * Thread-safe version of <code>SimpleDateFormat</code>.
 *
 * @see     java.text.SimpleDateFormat
 * @see     java.lang.ThreadLocal
 * @author  BigCheese
 */
public class SafeSimpleDateFormat extends ThreadLocal<SimpleDateFormat> {

    private final String pattern;

    public SafeSimpleDateFormat(String pattern) {
        this.pattern = pattern;
    }

    @Override
    protected SimpleDateFormat initialValue() {
        return new SimpleDateFormat(pattern);
    }
}
