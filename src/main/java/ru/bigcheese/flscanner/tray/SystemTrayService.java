package ru.bigcheese.flscanner.tray;

import ru.bigcheese.flscanner.event.ParseTaskEventType;

import java.awt.event.ActionListener;
import java.util.Map;

public interface SystemTrayService {

    void initSystemTray(Map<String, ActionListener> menuActions);

    void displayMessage(String title, String text, ParseTaskEventType eventType);

    String ABOUT_ITEM = "About...";
    String SCHEDULED_SCAN_ITEM = "Scheduled scan";
    String SCAN_ONCE_ITEM = "Scan once";
    String STOP_SCAN_ITEM = "Stop scan";
    String SHOW_UPDATES_ITEM = "Show updates...";
    String SHOW_ACTIVE_TASKS_ITEM = "Show active tasks...";
    String SETTINGS_ITEM = "Settings...";
    String EXIT_ITEM = "Exit";
}
