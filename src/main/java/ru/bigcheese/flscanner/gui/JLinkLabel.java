package ru.bigcheese.flscanner.gui;

import ru.bigcheese.flscanner.gui.listeners.LinkLabelMouseListener;

import javax.swing.*;
import java.awt.event.MouseListener;

/**
 * Created by BigCheese on 28.06.16.
 */
public class JLinkLabel extends JLabel {

    public static final MouseListener linkLabelListener = new LinkLabelMouseListener();
    private final String link;

    public JLinkLabel(String text, String link) {
        super(text);
        this.link = link;
        addMouseListener(linkLabelListener);
    }

    public String getLink() {
        return link;
    }
}
