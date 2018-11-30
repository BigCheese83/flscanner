package ru.bigcheese.flscanner.ui;

import dorkbox.util.Desktop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LinkLabel extends JLabel {

    private static final MouseAdapter mouseAdapter = new LinkLabelMouseAdapter();

    private final String link;

    public LinkLabel(String text, String link) {
        super(text);
        this.link = link;
        addMouseListener(mouseAdapter);
    }

    public String getLink() {
        return link;
    }

    private static class LinkLabelMouseAdapter extends MouseAdapter {

        private static final Logger log = LoggerFactory.getLogger(LinkLabelMouseAdapter.class);

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof LinkLabel) {
                LinkLabel label = (LinkLabel) e.getSource();
                try {
                    Desktop.browseURL(new URI(label.getLink()));
                } catch (IOException | URISyntaxException ex) {
                    log.warn(ex.getMessage());
                }
                Font font = label.getFont();
                label.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (e.getSource() instanceof LinkLabel) {
                LinkLabel source = (LinkLabel) e.getSource();
                source.setForeground(Color.blue);
                source.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (e.getSource() instanceof LinkLabel) {
                LinkLabel source = (LinkLabel) e.getSource();
                source.setForeground(Color.black);
            }
        }
    }
}
