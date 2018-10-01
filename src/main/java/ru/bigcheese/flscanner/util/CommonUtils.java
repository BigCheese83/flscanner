package ru.bigcheese.flscanner.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class CommonUtils {

    public static String getStacktrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
