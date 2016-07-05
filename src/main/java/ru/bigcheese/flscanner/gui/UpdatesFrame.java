package ru.bigcheese.flscanner.gui;

import ru.bigcheese.flscanner.Runner;
import ru.bigcheese.flscanner.config.Settings;
import ru.bigcheese.flscanner.gui.listeners.CheckBoxIgnoreItemListener;
import ru.bigcheese.flscanner.model.Post;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BigCheese on 23.06.16.
 */
public class UpdatesFrame extends JFrame {

    private Map<String, List<Post>> updates = new HashMap<>();
    private UpdatesContentPanel contentPanel = new UpdatesContentPanel();

    public UpdatesFrame() {
        super("Updates");
        updateContentPanel(contentPanel);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        setSize(650, 400);

        Runner.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (Settings.ALL_POSTS.containsKey(evt.getPropertyName())) {
                    new SwingWorker<Object, Object>() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            updateContentPanel(contentPanel);
                            return null;
                        }
                        @Override
                        protected void done() {
                            contentPanel.revalidate();
                        }
                    }.execute();
                }
            }
        });
    }

    private void initUpdates() {
        for (Map.Entry<String, List<Post>> entry : Settings.ALL_POSTS.entrySet()) {
            if (entry.getValue().size() > 0) {
                updates.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void updateContentPanel(UpdatesContentPanel panel) {
        initUpdates();
        if (!updates.isEmpty()) {
            for (Map.Entry<String, List<Post>> entry : updates.entrySet()) {
                String name = entry.getKey();
                List<Post> posts = entry.getValue();
                panel.addBar(name, getSitePanel(name, posts), posts.size());
            }
            panel.setVisibleBar(1);
        }
    }

    private JPanel getSitePanel(String name, List<Post> posts) {
        final Dimension vertSpace = new Dimension(0, 10);
        final Dimension postSpace = new Dimension(0, 3);

        JPanel content = GUITools.createVerticalBoxPanel();
        content.add(Box.createRigidArea(vertSpace));
        for (Post post : posts) {
            content.add(getPostPanel(post, name));
            content.add(Box.createRigidArea(postSpace));
        }
        content.add(Box.createRigidArea(vertSpace));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(content), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getPostPanel(Post post, String siteName) {
        JLinkLabel label = new JLinkLabel(post.getTitle() + " [" + post.getTime() + "]", post.getLink());
        label.setToolTipText("<html><p width=\"350\">" + post.getDescription() + "</p></html>");
        GUITools.setLabelStyle(label, Font.BOLD);

        JCheckBox checkBox = new JCheckBox();
        checkBox.addItemListener(new CheckBoxIgnoreItemListener(label, siteName, post.getId()));

        final Dimension horizSpace = new Dimension(10, 0);
        JPanel panel = GUITools.createHorizontalBoxPanel();
        panel.add(Box.createRigidArea(horizSpace));
        panel.add(checkBox);
        panel.add(Box.createRigidArea(horizSpace));
        panel.add(label);
        panel.add(Box.createRigidArea(horizSpace));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;
    }

}
