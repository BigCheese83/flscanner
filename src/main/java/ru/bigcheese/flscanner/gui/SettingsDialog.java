package ru.bigcheese.flscanner.gui;

import ru.bigcheese.flscanner.config.Settings;
import ru.bigcheese.flscanner.config.SiteConfig;
import ru.bigcheese.flscanner.config.SysProps;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by BigCheese on 22.06.16.
 */
public class SettingsDialog extends JDialog {

    private SysProps props = SysProps.getInstance();
    private JTextField timeoutField = new JTextField();
    private JTextField pullIntervalField = new JTextField();
    private JTextField maxPostsField = new JTextField();
    private JComboBox<String> combo;
    private final IgnoredSiteListener ignoreSiteListener = new IgnoredSiteListener();

    private static final WindowListener closeWindowListener = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose();
        }
    };

    public SettingsDialog() {
        super((JFrame) null, "Settings");
        addWindowListener(closeWindowListener);

        JPanel contentPanel = GUITools.createVerticalBoxPanel();
        contentPanel.add(getConnectPanel());
        contentPanel.add(getKeywordsPanel());

        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(createBorder());
        main.add(contentPanel, BorderLayout.CENTER);
        main.add(getParsingPanel(), BorderLayout.EAST);
        main.add(getFooterPanel(), BorderLayout.SOUTH);

        getContentPane().add(main, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setResizable(false);
        pack();
    }

    private Border createTitleBorder(String title) {
        EmptyBorder empty = new EmptyBorder(7, 7, 0, 7);
        Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(etchedBorder, title);
        return BorderFactory.createCompoundBorder(empty, titledBorder);
    }

    private Border createBorder() {
        EmptyBorder empty = new EmptyBorder(7, 7, 7, 7);
        Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        return BorderFactory.createCompoundBorder(empty, etchedBorder);
    }

    private JPanel getConnectPanel() {
        final String[] labels = new String[] {"Timeout", "Pull Interval", "Max posts"};
        final JTextField[] fields = new JTextField[]{timeoutField, pullIntervalField, maxPostsField};
        JPanel labelPanel = new JPanel(new GridLayout(labels.length,1));
        JPanel fieldPanel = new JPanel(new GridLayout(labels.length,1));

        for (int i = 0; i < labels.length; i++) {
            JLabel jLabel = new JLabel(labels[i]);
            jLabel.setLabelFor(fields[i]);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fields[i].setColumns(10);
            p.add(fields[i]);
            labelPanel.add(jLabel);
            fieldPanel.add(p);
        }

        timeoutField.setText(Integer.toString(props.getTimeout()));
        pullIntervalField.setText(Integer.toString(props.getPullInterval()));
        maxPostsField.setText(Integer.toString(props.getMaxPosts()));

        JPanel panel = new JPanel(new BorderLayout(10,0));
        panel.add(labelPanel, BorderLayout.WEST);
        panel.add(fieldPanel, BorderLayout.CENTER);
        panel.setBorder(createTitleBorder("Connect"));
        return panel;
    }

    private JPanel getKeywordsPanel() {
        final JTextField field = new JTextField(10);
        combo = new JComboBox<>(props.getKeywords());
        JButton removeButton = new JButton("Remove");
        JButton addButton = new JButton("Add");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (combo.getModel().getSize() > 0) {
                            combo.removeItemAt(combo.getSelectedIndex());
                        }
                    }
                });
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (field.getText().trim().length() > 0) {
                            combo.addItem(field.getText());
                            field.setText("");
                        }
                    }
                });
            }
        });

        JPanel p1 = GUITools.createVerticalBoxPanel();
        JPanel p2 = GUITools.createVerticalBoxPanel();
        p1.add(removeButton);
        p1.add(Box.createRigidArea(new Dimension(0, 5)));
        p1.add(addButton);
        p2.add(combo);
        p2.add(Box.createRigidArea(new Dimension(0, 5)));
        p2.add(field);
        JPanel panel = new JPanel(new BorderLayout(10,0));
        panel.add(p2, BorderLayout.WEST);
        panel.add(p1, BorderLayout.CENTER);
        panel.setBorder(createTitleBorder("Keywords"));
        return panel;
    }

    private JPanel getParsingPanel() {
        JPanel panel = GUITools.createVerticalBoxPanel();
        for (SiteConfig sc : Settings.getAllConfigs()) {
            JCheckBox checkBox = new JCheckBox(sc.getName());
            checkBox.setSelected(!props.getIgnored().contains(sc.getName()));
            checkBox.addItemListener(ignoreSiteListener);
            panel.add(checkBox);
        }
        panel.setBorder(createTitleBorder("Parsing"));
        return panel;
    }

    private JPanel getFooterPanel() {
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
                setVisible(false);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        JPanel footPanel = new JPanel();
        footPanel.add(okButton);
        footPanel.add(cancelButton);
        return footPanel;
    }

    private void saveSettings() {
        props.setTimeout(Integer.parseInt(timeoutField.getText()));
        props.setPullInterval(Integer.parseInt(pullIntervalField.getText()));
        props.setMaxPosts(Integer.parseInt(maxPostsField.getText()));
        List<String> comboBoxItems = GUITools.getComboBoxItems(combo);
        props.setKeywords(comboBoxItems.toArray(new String[comboBoxItems.size()]));
        props.setIgnored(ignoreSiteListener.ignored);
    }

    private static class IgnoredSiteListener implements ItemListener {
        private final Set<String> ignored = new HashSet<>(SysProps.getInstance().getIgnored());

        @Override
        public void itemStateChanged(ItemEvent e) {
            JCheckBox checkBox = (JCheckBox)e.getItem();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                synchronized (ignored) {
                    ignored.remove(checkBox.getText());
                }
            } else {
                synchronized (ignored) {
                    ignored.add(checkBox.getText());
                }
            }
        }
    }
}
