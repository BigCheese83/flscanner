package ru.bigcheese.flscanner.ui;

import net.miginfocom.swing.MigLayout;
import ru.bigcheese.flscanner.model.Post;
import ru.bigcheese.flscanner.task.ParseTaskService;

import javax.swing.*;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import static ru.bigcheese.flscanner.event.ParseTaskEventType.UPDATES_FOUND;


public class UpdatesFrame extends JFrame {

    private final ParseTaskService taskService = ParseTaskService.getInstance();
    private final UpdatesContentPanel contentPanel = new UpdatesContentPanel();
    private final Map<String, List<Post>> posts = taskService.getAllPosts();

    public UpdatesFrame() {
        super("Updates");
        setLayout(new MigLayout());
        updateContentPanel(contentPanel);
        add(contentPanel, "push, grow");
        setSize(690, 500);

        taskService.addListener(event -> {
            if (event.getType() == UPDATES_FOUND) {
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
        });
    }

    private void updateContentPanel(UpdatesContentPanel panel) {
        posts.entrySet().stream()
                .filter(e -> e.getValue().size() > 0)
                .forEach(e -> panel.addBar(
                        e.getKey(),
                        createSitePanel(e.getKey(), e.getValue()),
                        e.getValue().size()));
        panel.setVisibleBar(1);
    }

    private JPanel createSitePanel(String name, List<Post> posts) {
        JPanel content = new JPanel(new MigLayout("gapy 0", "", ""));
        for (Post post : posts) {
            content.add(createPostPanel(post, name), "wrap");
        }
        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JScrollPane(content), "push, grow");
        return panel;
    }

    private JPanel createPostPanel(Post post, String siteName) {
        LinkLabel label = new LinkLabel(post.getTitle() + " [" + post.getTime() + "]", post.getLink());
        label.setToolTipText("<html><p width=\"350\">" + post.getDescription() + "</p></html>");
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        JPanel panel = new JPanel(new MigLayout());
        panel.add(label);
        return panel;
    }
}
