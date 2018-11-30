package ru.bigcheese.flscanner.tray;

import dorkbox.notify.Notify;
import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bigcheese.flscanner.event.ParseTaskEventType;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.Map;

public class DorkBoxSystemTrayService implements SystemTrayService {

    private static final Logger log = LoggerFactory.getLogger(DorkBoxSystemTrayService.class);
    private static final DorkBoxSystemTrayService instance = new DorkBoxSystemTrayService();

    private static final long SHOW_MESSAGE_INTERVAL = 5 * 60 * 1000;

    private volatile boolean isInitialized = false;
    private volatile boolean isShow = false;
    private volatile long lastShow;

    private DorkBoxSystemTrayService() {
    }

    public static DorkBoxSystemTrayService getInstance() {
        return instance;
    }

    @Override
    public synchronized void initSystemTray(final Map<String, ActionListener> menuActions) {
        if (isInitialized) {
            log.warn("SystemTrayService already initialized.");
            return;
        }
        SystemTray systemTray = SystemTray.get();
        if (systemTray == null) {
            throw new RuntimeException("Unable to load SystemTray!");
        }

        Image icon = new ImageIcon(getClass().getResource("/images/search.png")).getImage();
        systemTray.setImage(icon);

        // Add components to popup menu
        final Menu menu = systemTray.getMenu();
        final MenuItem aboutItem = new MenuItem(ABOUT_ITEM);
        final MenuItem scheduledScanItem = new MenuItem(SCHEDULED_SCAN_ITEM);
        final MenuItem scanOnceItem = new MenuItem(SCAN_ONCE_ITEM);
        final MenuItem stopScanItem = new MenuItem(STOP_SCAN_ITEM);
        final MenuItem showUpdatesItem = new MenuItem(SHOW_UPDATES_ITEM);
        final MenuItem showActiveTasksItem = new MenuItem(SHOW_ACTIVE_TASKS_ITEM);
        final MenuItem settingsItem = new MenuItem(SETTINGS_ITEM);
        final MenuItem exitItem = new MenuItem(EXIT_ITEM);

        // Add action listeners
        aboutItem.setCallback(menuActions.get(ABOUT_ITEM));
        scheduledScanItem.setCallback(e -> {
            scheduledScanItem.setEnabled(false);
            menuActions.get(SCHEDULED_SCAN_ITEM).actionPerformed(e);
        });
        stopScanItem.setCallback(e -> {
            menuActions.get(STOP_SCAN_ITEM).actionPerformed(e);
            scheduledScanItem.setEnabled(true);
        });
        scanOnceItem.setCallback(menuActions.get(SCAN_ONCE_ITEM));
        showUpdatesItem.setCallback(menuActions.get(SHOW_UPDATES_ITEM));
        showActiveTasksItem.setCallback(menuActions.get(SHOW_ACTIVE_TASKS_ITEM));
        settingsItem.setCallback(menuActions.get(SETTINGS_ITEM));
        exitItem.setCallback(e -> {
            systemTray.shutdown();
            menuActions.get(EXIT_ITEM).actionPerformed(e);
        });

        // Add components to popup menu
        menu.add(aboutItem);
        menu.add(new Separator());
        menu.add(scheduledScanItem);
        menu.add(scanOnceItem);
        menu.add(stopScanItem);
        menu.add(new Separator());
        menu.add(showUpdatesItem);
        menu.add(showActiveTasksItem);
        menu.add(settingsItem);
        menu.add(new Separator());
        menu.add(exitItem);

        isInitialized = true;
    }

    @Override
    public synchronized void displayMessage(String title, String text, ParseTaskEventType eventType) {
        if (isShow || (isShow && System.currentTimeMillis() < (lastShow + SHOW_MESSAGE_INTERVAL))) {
            return;
        }
        Notify notify = Notify.create()
                .darkStyle()
                .title(title)
                .text(text)
                .hideCloseButton()
                .onAction(n -> isShow = false);
        if (eventType == ParseTaskEventType.ERROR) {
            notify.showError();
        } else {
            notify.show();
        }
        lastShow = System.currentTimeMillis();
        isShow = true;
    }
}
