package ru.bigcheese.flscanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.bigcheese.flscanner.config.Selector;
import ru.bigcheese.flscanner.config.SettingsOld;
import ru.bigcheese.flscanner.config.SiteConfigOld;
import ru.bigcheese.flscanner.config.SysProps;
import ru.bigcheese.flscanner.model.Post;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by BigCheese on 20.06.16.
 */
@Deprecated
public class ParseTask implements Runnable {

    private final SiteConfigOld config;
    private final PropertyChangeSupport pcs;
    private long currTimestamp;

    private static final Comparator<Post> timeComparator = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            long res = o1.getTimestamp() - o2.getTimestamp();
            if (res > 0) return 1;
            if (res < 0) return -1;
            return 0;
        }
    };

    public ParseTask(SiteConfigOld config) {
        this.config = config;
        this.pcs = new PropertyChangeSupport(SettingsOld.ALL_POSTS);
    }

    @Override
    public void run() {
        runPreFilter();
        try {
            List<Post> posts = new ArrayList<>();
            String page = config.getBasePage();
            Selector selector = config.getSelector();
            currTimestamp = SettingsOld.TIMESTAMPS.get(config.getName());
            boolean next = true;

            for (int i = 1; next; i++) {
                Elements rows = Jsoup.connect(page)
                        .timeout(SysProps.getInstance().getTimeout())
                        .get()
                        .select(selector.getRows());

                for (Element row : rows) {
                    Element link = row.select(selector.getLink()).first();
                    Element time = row.select(selector.getTime()).first();
                    Element descr = row.select(selector.getDescr()).first();

                    String id = link.attr("id");
                    String sLink = config.getBaseUrl() + link.attr("href");
                    String sTitle = link.text();
                    String sDescr = config.getDescrExtractor().getValue(descr);
                    String sTime = config.getTimeExtractor().getValue(time);
                    long timestamp = config.getDateParser().getTimestamp(sTime);

                    Post post = new Post(id, sTitle, sDescr, sLink, sTime, timestamp);
                    posts.add(post);
                }

                next = isContinue(posts);
                if (next) {
                    page = config.getBasePage() + MessageFormat.format(config.getPageSuffix(), i+1);
                }
            }

            List<Post> filtered = filterPosts(posts);
            if (filtered.size() > 0) {
                pcs.firePropertyChange(config.getName(), SettingsOld.ALL_POSTS.get(config.getName()), filtered);
            }
            SettingsOld.ALL_POSTS.put(config.getName(), filtered);
            updateTimestamp(posts);
            System.out.println(getConsoleMessage(filtered));

        } catch (Exception e) {
            e.printStackTrace();
            pcs.firePropertyChange("parse_error", "", e.getMessage());
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)  {
        pcs.addPropertyChangeListener(listener);
    }

    private void runPreFilter() {
        try {
            if (config.getPreFilter() != null) {
                config.getPreFilter().applyFilter();
            }
        } catch (Exception e) {
            e.printStackTrace();
            pcs.firePropertyChange("parse_error", "", "Fail to apply pre-filter.\nCheck configuration.");
        }
    }

    private List<Post> filterPosts(List<Post> posts) {
        String keywordPattern = getPattern(SysProps.getInstance().getKeywords());
        Set<String> ignored = SettingsOld.IGNORED_POSTS.get(config.getName());
        int maxPosts = SysProps.getInstance().getMaxPosts() + 1;
        List<Post> filtered = new ArrayList<>();
        for (int i = 0; i < posts.size() && i < maxPosts; i++) {
            Post post = posts.get(i);
            if (post.getTimestamp() > currTimestamp                 // filter by timestamp
                    && !ignored.contains(post.getId())              // filter by ignore flag
                    && post.getTitle().matches(keywordPattern)) {   // filter by keywords
                filtered.add(post);
            }
        }
        return Collections.unmodifiableList(filtered);
    }

    private void updateTimestamp(List<Post> posts) {
        if (posts.isEmpty()) return;
        Post max = Collections.max(posts, timeComparator);
        if (max.getTimestamp() > 0) {
            SettingsOld.TIMESTAMPS.put(config.getName(), max.getTimestamp());
        }
    }

    private boolean isContinue(List<Post> posts) {
        if (posts.size() > 0) {
            for (int i = posts.size()-1; i >= 0; i--) {
                if (posts.get(i).getTimestamp() > 0) {
                    return posts.get(i).getTimestamp() > currTimestamp;
                }
            }
        }
        return false;
    }

    private String getConsoleMessage(List<Post> posts) {
        String result = new Date() + " " + config.getName() + ": ";
        result += (posts == null || posts.isEmpty()) ? "no updates." : "update " + posts.size() + " posts.";
        return result;
    }

    private String getPattern(String[] keywords) {
        StringBuilder sb = new StringBuilder(".*\\b(?iu)(");
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) sb.append("|");
            sb.append(keywords[i]);
        }
        sb.append(")\\b.*");
        return sb.toString();
    }
}
