package ru.bigcheese.flscanner.tray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bigcheese.flscanner.event.ParseTaskEventType;

import javax.swing.ImageIcon;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.util.Map;

public class NativeSystemTrayService implements SystemTrayService {

    private static final Logger log = LoggerFactory.getLogger(NativeSystemTrayService.class);
    private static final NativeSystemTrayService instance = new NativeSystemTrayService();

    private volatile boolean isInitialized = false;
    private TrayIcon trayIcon;

    private NativeSystemTrayService() {
    }

    public static NativeSystemTrayService getInstance() {
        return instance;
    }

    @Override
    public synchronized void initSystemTray(Map<String, ActionListener> menuActions) {
        if (isInitialized) {
            log.warn("SystemTrayService already initialized.");
            return;
        }
        // Create a popup menu components
        final PopupMenu popup = new PopupMenu();
        final MenuItem aboutItem = new MenuItem(ABOUT_ITEM);
        final MenuItem scheduledScanItem = new MenuItem(SCHEDULED_SCAN_ITEM);
        final MenuItem scanOnceItem = new MenuItem(SCAN_ONCE_ITEM);
        final MenuItem stopScanItem = new MenuItem(STOP_SCAN_ITEM);
        final MenuItem showUpdatesItem = new MenuItem(SHOW_UPDATES_ITEM);
        final MenuItem showActiveTasksItem = new MenuItem(SHOW_ACTIVE_TASKS_ITEM);
        final MenuItem settingsItem = new MenuItem(SETTINGS_ITEM);
        final MenuItem exitItem = new MenuItem(EXIT_ITEM);

        // Add action listeners
        aboutItem.addActionListener(menuActions.get(ABOUT_ITEM));
        scheduledScanItem.addActionListener(e -> {
            scheduledScanItem.setEnabled(false);
            menuActions.get(SCHEDULED_SCAN_ITEM).actionPerformed(e);
        });
        stopScanItem.addActionListener(e -> {
            menuActions.get(STOP_SCAN_ITEM).actionPerformed(e);
            scheduledScanItem.setEnabled(true);
        });
        scanOnceItem.addActionListener(menuActions.get(SCAN_ONCE_ITEM));
        showUpdatesItem.addActionListener(menuActions.get(SHOW_UPDATES_ITEM));
        showActiveTasksItem.addActionListener(menuActions.get(SHOW_ACTIVE_TASKS_ITEM));
        settingsItem.addActionListener(menuActions.get(SETTINGS_ITEM));
        exitItem.addActionListener(menuActions.get(EXIT_ITEM));

        // Add components to popup menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(scheduledScanItem);
        popup.add(scanOnceItem);
        popup.add(stopScanItem);
        popup.addSeparator();
        popup.add(showUpdatesItem);
        popup.add(showActiveTasksItem);
        popup.add(settingsItem);
        popup.addSeparator();
        popup.add(exitItem);

        SystemTray tray = SystemTray.getSystemTray();
        Image icon = new ImageIcon(getClass().getResource("/images/search.png")).getImage();
        trayIcon = new TrayIcon(icon, "Freelance Tasks Scanner", popup);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            log.error("unable add tray icon", e);
            throw new RuntimeException(e);
        }

        isInitialized = true;
    }

    @Override
    public void displayMessage(String title, String text, ParseTaskEventType eventType) {
        EventQueue.invokeLater(() -> {
            TrayIcon.MessageType type = TrayIcon.MessageType.NONE;
            if (eventType == ParseTaskEventType.ERROR) {
                type = TrayIcon.MessageType.ERROR;
            }
            trayIcon.displayMessage(title, text, type);
        });
    }
}
