package ru.bigcheese.flscanner;

import ru.bigcheese.flscanner.config.Settings;
import ru.bigcheese.flscanner.model.ParseError;
import ru.bigcheese.flscanner.task.ParseTaskService;
import ru.bigcheese.flscanner.ui.AboutDialog;
import ru.bigcheese.flscanner.ui.SettingsDialog;

import javax.swing.*;
import java.awt.*;

public class Main {

    //private static JFrame updatesFrame;
    private static JDialog aboutDialog;
    private static JDialog settingsDialog;

    public static void main(String[] args) {
        Settings.getInstance().initSettings();
        EventQueue.invokeLater(() -> {
            applyLookAndFeel();
            try {
                createAndShowGUI();
            } catch (AWTException e) {
                showErrorDialog(e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    private static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(javax.swing.plaf.nimbus.NimbusLookAndFeel.class.getName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private static void createAndShowGUI() throws AWTException {
        // Create a popup menu components
        final PopupMenu popup = new PopupMenu();
        final MenuItem aboutItem = new MenuItem("About...");
        final MenuItem scheduledScanItem = new MenuItem("Scheduled scan");
        final MenuItem scanOnceItem = new MenuItem("Scan once");
        final MenuItem stopScanItem = new MenuItem("Stop scan");
        final MenuItem showUpdatesItem = new MenuItem("Show updates...");
        final MenuItem showActiveTasksItem = new MenuItem("Show active tasks...");
        final MenuItem settingsItem = new MenuItem("Settings...");
        final MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
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

        final SystemTray tray = SystemTray.getSystemTray();
        Image icon = new ImageIcon(Main.class.getResource("/images/search.png")).getImage();
        final TrayIcon trayIcon = new TrayIcon(icon, "Freelance Tasks Scanner", popup);
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);

        final ParseTaskService taskService = ParseTaskService.getInstance();
        taskService.addListener(event -> {
            switch (event.getType()) {
//                case START_SCHEDULED:
//                    scheduledScanItem.setEnabled(false);
//                    break;
//                case STOPPED_ALL:
//                    scheduledScanItem.setEnabled(true);
//                    break;
                case ERROR:
                    trayIcon.displayMessage("Error!", ((ParseError) event.getPayload()).getMessage(), TrayIcon.MessageType.ERROR);
                    break;
            }
        });

        aboutItem.addActionListener(e -> {
            if (aboutDialog == null) {
                aboutDialog = new AboutDialog();
            }
            aboutDialog.setVisible(true);
        });

        scheduledScanItem.addActionListener(e -> {
            scheduledScanItem.setEnabled(false);
            taskService.scheduledScan();
        });

        scanOnceItem.addActionListener(e -> taskService.scanOnce());

        stopScanItem.addActionListener(e -> {
            taskService.stopScan();
            scheduledScanItem.setEnabled(true);
        });

        settingsItem.addActionListener(e -> {
            if (settingsDialog == null) {
                settingsDialog = new SettingsDialog();
            }
            settingsDialog.setVisible(true);
        });

        //showUpdatesItem.addActionListener(e -> new ru.bigcheese.flscanner.gui.SettingsDialog().setVisible(true));

        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
//            Runner.shutdownNow();
//            SettingsOld.saveSettings();
            System.exit(0);
        });

        showActiveTasksItem.addActionListener(e -> taskService.printActiveTasks());

//        Runner.addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if ("scan".equals(evt.getPropertyName())) {
//                    boolean scan = (boolean) evt.getNewValue();
//                    runScan.setEnabled(!scan);
//                    stopScan.setEnabled(scan);
//                } else if ("parse_error".equals(evt.getPropertyName())) {
//                    trayIcon.displayMessage("Error!", (String)evt.getNewValue(),
//                            TrayIcon.MessageType.ERROR);
//                } else if (SettingsOld.ALL_POSTS.containsKey(evt.getPropertyName())) {
//                    trayIcon.displayMessage("Updates found for",
//                            evt.getPropertyName() + " (" + ((List)evt.getNewValue()).size() + ")",
//                            TrayIcon.MessageType.NONE);
//                }
//            }
//        });

//        showUpdates.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (updatesFrame == null) {
//                    updatesFrame = new UpdatesFrame();
//                }
//                updatesFrame.setVisible(true);
//            }
//        });
    }

    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
