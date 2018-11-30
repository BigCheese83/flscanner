package ru.bigcheese.flscanner.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by BigCheese on 21.06.16.
 */
@Deprecated
public class SysProps {

    private static final SysProps INSTANCE = new SysProps();

    private AtomicInteger timeout;
    private AtomicInteger pullInterval;
    private AtomicInteger maxPosts;
    private CopyOnWriteArrayList<String> keywords;
    private Set<String> ignored;

    private SysProps() {
        this.timeout = new AtomicInteger(5000);
        this.pullInterval = new AtomicInteger(600);
        this.maxPosts = new AtomicInteger(1000);
        this.keywords = new CopyOnWriteArrayList<>();
        this.ignored = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    }

    public static SysProps getInstance() {
        return INSTANCE;
    }

    public int getTimeout() {
        return timeout.get();
    }

    public void setTimeout(int timeout) {
        this.timeout.set(timeout);
    }

    public int getPullInterval() {
        return pullInterval.get();
    }

    public void setPullInterval(int pullInterval) {
        this.pullInterval.set(pullInterval);
    }

    public int getMaxPosts() {
        return maxPosts.get();
    }

    public void setMaxPosts(int maxPosts) {
        this.maxPosts.set(maxPosts);
    }

    public String[] getKeywords() {
        return keywords.toArray(new String[keywords.size()]);
    }

    public void setKeywords(String[] keywords) {
        this.keywords = new CopyOnWriteArrayList<>(keywords);
    }

    public Set<String> getIgnored() {
        return ignored;
    }

    public void setIgnored(Set<String> ignored) {
        this.ignored.clear();
        this.ignored.addAll(ignored);
    }
}
