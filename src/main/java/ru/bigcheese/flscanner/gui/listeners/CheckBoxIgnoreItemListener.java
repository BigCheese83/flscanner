package ru.bigcheese.flscanner.gui.listeners;

import ru.bigcheese.flscanner.config.Settings;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

/**
 * Created by BigCheese on 28.06.16.
 */
public class CheckBoxIgnoreItemListener implements ItemListener {

    private final JLabel label;
    private final String postId;
    private final Set<String> ignores;

    public CheckBoxIgnoreItemListener(JLabel label, String siteName, String postId) {
        this.label = label;
        this.postId = postId;
        this.ignores = Settings.IGNORED_POSTS.get(siteName);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            label.setText("<html><p color=\"gray\"><s>" + label.getText() + "</s></p></html>");
            if (ignores != null) {
                synchronized (ignores) {
                    ignores.add(postId);
                }
            }
        } else {
            label.setText(label.getText().replaceAll("<[^>]*>", ""));
            if (ignores != null) {
                synchronized (ignores) {
                    ignores.remove(postId);
                }
            }
        }
    }
}

