package ru.bigcheese.flscanner.ui;

import net.miginfocom.swing.MigLayout;
import ru.bigcheese.flscanner.config.Settings;
import ru.bigcheese.flscanner.config.SiteConfig;
import ru.bigcheese.flscanner.model.AppProps;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsDialog extends JDialog {

    private final Settings settings = Settings.getInstance();
    private Set<String> ignored = new HashSet<>();

    private JFormattedTextField timeoutField;
    private JFormattedTextField pullIntervalField;
    private JFormattedTextField maxPostsField;
    private JFormattedTextField scanDaysField;
    private JComboBox<String> comboBox;
    private List<JCheckBox> checkBoxes = new ArrayList<>();

    public SettingsDialog() {
        super((JFrame) null, "Settings");
        setLayout(new MigLayout());

        JPanel connectPanel = createConnectPanel();
        JPanel scanPanel = createScanPanel();
        JPanel keywordsPanel = createKeywordsPanel();
        JPanel parsingPanel = createParsingPanel();
        JPanel footerPanel = createFooterPanel();

        JPanel mainPanel = new JPanel(new MigLayout("gapy 0", "", ""));
        mainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        mainPanel.add(connectPanel, "grow");
        mainPanel.add(parsingPanel, "spany 3, grow, wrap");
        mainPanel.add(scanPanel, "grow, wrap");
        mainPanel.add(keywordsPanel, "grow, wrap");
        mainPanel.add(footerPanel, "spanx, grow");

        add(mainPanel, "push, grow");

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                readSettings();
            }
        });

        readSettings();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createConnectPanel() {
        timeoutField = new JFormattedTextField(NumberFormat.getInstance());
        timeoutField.setColumns(8);
        JLabel timeoutLabel = new JLabel("Timeout, ms");
        timeoutLabel.setLabelFor(timeoutField);

        pullIntervalField = new JFormattedTextField(NumberFormat.getInstance());
        pullIntervalField.setColumns(8);
        JLabel pullIntervalLabel = new JLabel("Pull Interval, sec");
        pullIntervalLabel.setLabelFor(pullIntervalField);

        JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(createTitleBorder("Connect"));
        panel.add(timeoutLabel);
        panel.add(timeoutField, "wrap");
        panel.add(pullIntervalLabel);
        panel.add(pullIntervalField);
        return panel;
    }

    private JPanel createScanPanel() {
        maxPostsField = new JFormattedTextField(NumberFormat.getInstance());
        maxPostsField.setColumns(8);
        JLabel maxPostsLabel = new JLabel("Max Posts");
        maxPostsLabel.setLabelFor(maxPostsField);

        scanDaysField = new JFormattedTextField(NumberFormat.getInstance());
        scanDaysField.setColumns(8);
        JLabel scanDaysLabel = new JLabel("Scan days on start");
        scanDaysLabel.setLabelFor(scanDaysField);

        JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(createTitleBorder("Scan"));
        panel.add(maxPostsLabel);
        panel.add(maxPostsField, "wrap");
        panel.add(scanDaysLabel);
        panel.add(scanDaysField);
        return panel;
    }

    private JPanel createKeywordsPanel() {
        comboBox = new JComboBox<>();
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            if (comboBox.getModel().getSize() > 0) {
                comboBox.removeItemAt(comboBox.getSelectedIndex());
            }
        });

        JTextField keywordField = new JTextField(10);
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String keyword = keywordField.getText().trim();
            if (keyword.length() > 0) {
                comboBox.addItem(keyword);
                keywordField.setText("");
                keywordField.requestFocus();
            }
        });

        JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(createTitleBorder("Keywords"));
        panel.add(comboBox, "grow");
        panel.add(removeButton, "wrap");
        panel.add(keywordField);
        panel.add(addButton);
        return panel;
    }

    private JPanel createParsingPanel() {
        JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(createTitleBorder("Parsing"));

        ItemListener listener = e -> {
            JCheckBox item = (JCheckBox) e.getItem();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ignored.remove(item.getText());
            } else {
                ignored.add(item.getText());
            }
        };

        for (SiteConfig sc : settings.getAllSiteConfigs()) {
            JCheckBox checkBox = new JCheckBox(sc.getName());
            checkBox.addItemListener(listener);
            checkBoxes.add(checkBox);
            panel.add(checkBox, "wrap");
        }
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            saveSettings();
            setVisible(false);
            dispose();
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }

    private Border createTitleBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), title);
    }

    private void readSettings() {
        AppProps appProps = settings.getAppProps();
        ignored = new HashSet<>(appProps.getIgnored());
        timeoutField.setValue(appProps.getTimeout());
        pullIntervalField.setValue(appProps.getPullInterval());
        maxPostsField.setValue(appProps.getMaxPosts());
        scanDaysField.setValue(appProps.getScanDaysOnStart());
        comboBox.setModel(new DefaultComboBoxModel<>(
                appProps.getKeywords().toArray(new String[0])));
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setSelected(!ignored.contains(checkBox.getName()));
        }
    }

    private void saveSettings() {
        AppProps appProps = settings.getAppProps();
        appProps.setTimeout(((Number) timeoutField.getValue()).intValue());
        appProps.setPullInterval(((Number) pullIntervalField.getValue()).intValue());
        appProps.setMaxPosts(((Number) maxPostsField.getValue()).intValue());
        appProps.setScanDaysOnStart(((Number) scanDaysField.getValue()).intValue());
        appProps.setKeywords(getComboItems(comboBox));
        appProps.setIgnored(new HashSet<>(ignored));
        settings.saveSettings(appProps);
    }

    private <E> Set<E> getComboItems(JComboBox<E> comboBox) {
        Set<E> set = new HashSet<>();
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            set.add(comboBox.getItemAt(i));
        }
        return set;
    }
}
