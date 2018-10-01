package ru.bigcheese.flscanner.task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.bigcheese.flscanner.config.SiteConfig;
import ru.bigcheese.flscanner.model.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static ru.bigcheese.flscanner.support.ValueConverterSupport.getConverter;
import static ru.bigcheese.flscanner.support.ValueParserSupport.getParser;

public class ParseTask implements Callable<ParseResult> {

    private final SiteConfig config;
    private final AppProps appProps;
    private final long timestamp;
    private final TaskMetaInfo metaInfo;
    private final String startPage;

    private static final Comparator<Post> timestampComparator = (p1, p2) -> {
        long res = p1.getTimestamp() - p2.getTimestamp();
        if (res > 0) return 1;
        if (res < 0) return -1;
        return 0;
    };

    public ParseTask(SiteConfig config, AppProps appProps, long timestamp, boolean isScheduled) {
        this.config = config;
        this.appProps = appProps;
        this.timestamp = timestamp;
        this.metaInfo = new TaskMetaInfo(config.getName(), isScheduled);
        this.startPage = normalizeUrl(config.getBaseUrl()) + normalizeUrl(config.getPage());
    }

    @Override
    public ParseResult call() throws Exception {
        List<Post> posts = new ArrayList<>();
        String page = startPage;
        boolean hasNext = true;
        int i = 1;

        while (hasNext) {
            Elements rows = Jsoup.connect(page)
                    .timeout(appProps.getTimeout())
                    .get()
                    .select(config.getSelector().getRows());

            for (Element row : rows) {
                posts.add(parsePost(row, config.getSelector()));
            }

            hasNext = checkNext(posts);
            if (hasNext) {
                page = startPage + MessageFormat.format(config.getPageSuffix(), i+1);
                i++;
            }
        }

        posts = filterPosts(posts);
        long maxTimestamp = getMaxTimestamp(posts);
        return new ParseResult(metaInfo, posts, maxTimestamp);
    }

    public synchronized TaskMetaInfo getMetaInfo() {
        return metaInfo;
    }

    private Post parsePost(Element root, Selector selector) {
        Element eLink = root.select(selector.getLink()).first();
        Element eTime = root.select(selector.getTime()).first();
        Element eDescription = root.select(selector.getDescription()).first();

        String id = eLink.attr("id");
        String link = normalizeUrl(config.getBaseUrl()) + eLink.attr("href");
        String title = eLink.text();
        String description = getParser(config.getDescriptionParserType()).getDescription(eDescription);
        String time = getParser(config.getTimeParserType()).getTime(eTime);
        long timestamp = getConverter(config.getTimeConverterType()).getTimestamp(time);
        return new Post(id, title, description, link, time, timestamp);
    }

    private boolean checkNext(List<Post> posts) {
        if (!posts.isEmpty()) {
            for (int i = posts.size() - 1; i >= 0; i--) {
                if (posts.get(i).getTimestamp() > 0) {
                    return posts.get(i).getTimestamp() > timestamp;
                }
            }
        }
        return false;
    }

    private List<Post> filterPosts(List<Post> posts) {
        final String pattern = buildPattern(appProps.getKeywords());
        return posts.stream()
                .filter(p -> p.getTimestamp() > timestamp
                        && (pattern.isEmpty() || p.getTitle().matches(pattern)))
                .limit(appProps.getMaxPosts())
                .collect(Collectors.toList());
    }

    private String buildPattern(Set<String> keywords) {
        if (keywords.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(".*\\b(?iu)(");
        Iterator<String> iterator = keywords.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append("|");
            }
        }
        sb.append(")\\b.*");
        return sb.toString();
    }

    private long getMaxTimestamp(List<Post> posts) {
        long result = 0;
        if (!posts.isEmpty()) {
            result = Collections.max(posts, timestampComparator).getTimestamp();
        }
        return result;
    }

    private String normalizeUrl(String url) {
        return url.charAt(url.length()-1) == '/' ? url : url + "/";
    }
}
