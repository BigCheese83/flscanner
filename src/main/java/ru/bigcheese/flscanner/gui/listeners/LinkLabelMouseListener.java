package ru.bigcheese.flscanner.gui.listeners;

import ru.bigcheese.flscanner.gui.GUITools;
import ru.bigcheese.flscanner.gui.JLinkLabel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by BigCheese on 28.06.16.
 */
public class LinkLabelMouseListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof JLinkLabel && Desktop.isDesktopSupported()) {
            JLinkLabel label = (JLinkLabel)e.getSource();
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(label.getLink()));
                GUITools.setLabelStyle(label, Font.PLAIN);
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() instanceof JLinkLabel) {
            JLinkLabel source = (JLinkLabel)e.getSource();
            source.setForeground(Color.blue);
            source.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() instanceof JLinkLabel) {
            JLinkLabel source = (JLinkLabel)e.getSource();
            source.setForeground(Color.black);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}
}
