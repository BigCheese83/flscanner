package ru.bigcheese.flscanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bigcheese.flscanner.config.Settings;
import ru.bigcheese.flscanner.task.ParseTaskService;
import ru.bigcheese.flscanner.ui.AboutDialog;
import ru.bigcheese.flscanner.ui.SettingsDialog;
import ru.bigcheese.flscanner.ui.UpdatesFrame;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static ru.bigcheese.flscanner.tray.SystemTrayService.*;
import static ru.bigcheese.flscanner.util.CommonUtils.getSystemTrayService;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static JFrame updatesFrame;
    private static JDialog aboutDialog;
    private static JDialog settingsDialog;

    public static void main(String[] args) {
        Settings.getInstance().initSettings();
        EventQueue.invokeLater(() -> {
            applyLookAndFeel();
            try {
                createAndShowGUI();
            } catch (Exception e) {
                showErrorDialog(e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(javax.swing.plaf.nimbus.NimbusLookAndFeel.class.getName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            log.error("unable apply look and feel");
        }
    }

    private static void createAndShowGUI() {
        final ParseTaskService taskService = ParseTaskService.getInstance();

        // Create menu actions map
        final Map<String, ActionListener> menuActions = new HashMap<>();

        menuActions.put(ABOUT_ITEM, e -> {
            if (aboutDialog == null) {
                aboutDialog = new AboutDialog();
            }
            aboutDialog.setVisible(true);
        });
        menuActions.put(SETTINGS_ITEM, e -> {
            if (settingsDialog == null) {
                settingsDialog = new SettingsDialog();
            }
            settingsDialog.setVisible(true);
        });
        menuActions.put(SHOW_UPDATES_ITEM, e -> {
            if (updatesFrame == null) {
                updatesFrame = new UpdatesFrame();
            }
            updatesFrame.setVisible(true);
        });

        menuActions.put(SCHEDULED_SCAN_ITEM, e -> taskService.scheduledScan());
        menuActions.put(SCAN_ONCE_ITEM, e -> taskService.scanOnce());
        menuActions.put(STOP_SCAN_ITEM, e -> taskService.stopScan());
        menuActions.put(SHOW_ACTIVE_TASKS_ITEM, e -> taskService.printActiveTasks());

        menuActions.put(EXIT_ITEM, e -> {
            //tray.remove(trayIcon);
//            Runner.shutdownNow();
//            SettingsOld.saveSettings();
            System.exit(0);
        });

        getSystemTrayService().initSystemTray(menuActions);
    }

    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
