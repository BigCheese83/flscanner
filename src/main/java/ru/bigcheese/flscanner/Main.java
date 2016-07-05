package ru.bigcheese.flscanner;

import ru.bigcheese.flscanner.config.Settings;
import ru.bigcheese.flscanner.gui.SettingsDialog;
import ru.bigcheese.flscanner.gui.UpdatesFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class Main {

    private static JFrame updatesFrame;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });

        Runner.runScan();
    }

    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        // Create a popup menu components
        final PopupMenu popup = new PopupMenu();
        final MenuItem aboutItem = new MenuItem("About...");
        final MenuItem runScan = new MenuItem("Run Scan");
        final MenuItem stopScan = new MenuItem("Stop Scan");
        final MenuItem scanNow = new MenuItem("Scan Now");
        final MenuItem showUpdates = new MenuItem("Show Updates...");
        final MenuItem settingsItem = new MenuItem("Settings...");
        final MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(runScan);
        popup.add(stopScan);
        popup.add(scanNow);
        popup.add(showUpdates);
        popup.addSeparator();
        popup.add(settingsItem);
        popup.addSeparator();
        popup.add(exitItem);

        runScan.setEnabled(false);

        final SystemTray tray = SystemTray.getSystemTray();
        Image icon = new ImageIcon(Main.class.getResource("/images/search.png")).getImage();
        final TrayIcon trayIcon = new TrayIcon(icon, "Freelance Tasks Scanner", popup);
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        Runner.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("scan".equals(evt.getPropertyName())) {
                    boolean scan = (boolean) evt.getNewValue();
                    runScan.setEnabled(!scan);
                    stopScan.setEnabled(scan);
                } else if ("parse_error".equals(evt.getPropertyName())) {
                    trayIcon.displayMessage("Error!", (String)evt.getNewValue(),
                            TrayIcon.MessageType.ERROR);
                } else if (Settings.ALL_POSTS.containsKey(evt.getPropertyName())) {
                    trayIcon.displayMessage("Updates found for",
                            evt.getPropertyName() + " (" + ((List)evt.getNewValue()).size() + ")",
                            TrayIcon.MessageType.NONE);
                }
            }
        });

        settingsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsDialog().setVisible(true);
            }
        });

        showUpdates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (updatesFrame == null) {
                    updatesFrame = new UpdatesFrame();
                }
                updatesFrame.setVisible(true);
            }
        });

        runScan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Runner.runScan();
            }
        });

        stopScan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Runner.stopScan();
            }
        });

        scanNow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Runner.runOnce();
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Freelance Task Scanner (flscanner) by BigCheese.\n" +
                                "see more https://github.com/BigCheese83",
                        "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                Runner.shutdownNow();
                Settings.saveSettings();
                System.exit(0);
            }
        });
    }
}
