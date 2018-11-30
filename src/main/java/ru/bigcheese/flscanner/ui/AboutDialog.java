package ru.bigcheese.flscanner.ui;

import dorkbox.util.Desktop;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

import static java.awt.Cursor.HAND_CURSOR;

public class AboutDialog extends JDialog {

    private static final Logger log = LoggerFactory.getLogger(AboutDialog.class);
    private static final String GITHUB_LINK = "https://github.com/BigCheese83";

    public AboutDialog() {
        super((JFrame) null, "About");
        setLayout(new MigLayout("insets 15 30 15 30", "[center]", ""));

        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/images/search.png")));

        JLabel label1 = new JLabel("Freelance Task Scanner (flscanner) by BigCheese");

        JLabel label2 = new JLabel("<html>see more <a href='" + GITHUB_LINK + "'>" + GITHUB_LINK + "</a></html>");
        label2.setCursor(new Cursor(HAND_CURSOR));
        label2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.browseURL(new URI(GITHUB_LINK));
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        });

        JButton button = new JButton("OK");
        button.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        add(iconLabel, "spany 2");
        add(label1, "wrap 0");
        add(label2, "wrap");
        add(button, "spanx");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }
}
