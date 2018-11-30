package ru.bigcheese.flscanner.util;

import ru.bigcheese.flscanner.tray.DorkBoxSystemTrayService;
import ru.bigcheese.flscanner.tray.NativeSystemTrayService;
import ru.bigcheese.flscanner.tray.SystemTrayService;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class CommonUtils {

    public static String getStacktrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    public static SystemTrayService getSystemTrayService() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        return isWindows ? NativeSystemTrayService.getInstance() : DorkBoxSystemTrayService.getInstance();
    }
}
