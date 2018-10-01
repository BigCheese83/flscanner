package ru.bigcheese.flscanner.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class AppProps implements Serializable {

    private int timeout;
    private int pullInterval;
    private int scanDaysOnStart;
    private int maxPosts;
    private Set<String> keywords;
    private Set<String> ignored;

    public AppProps() {
        // Default values
        timeout = 5000;
        pullInterval = 600;
        scanDaysOnStart = 20;
        maxPosts = 1000;
        keywords = new HashSet<>();
        ignored = new HashSet<>();
    }

    public AppProps(int timeout, int pullInterval, int scanDaysOnStart, int maxPosts,
                    Set<String> keywords, Set<String> ignored) {
        this.timeout = timeout;
        this.pullInterval = pullInterval;
        this.scanDaysOnStart = scanDaysOnStart;
        this.maxPosts = maxPosts;
        this.keywords = keywords;
        this.ignored = ignored;
    }

    public AppProps(AppProps props) {
        this.timeout = props.timeout;
        this.pullInterval = props.pullInterval;
        this.scanDaysOnStart = props.scanDaysOnStart;
        this.maxPosts = props.maxPosts;
        this.keywords = new HashSet<>(props.keywords);
        this.ignored = new HashSet<>(props.ignored);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getPullInterval() {
        return pullInterval;
    }

    public void setPullInterval(int pullInterval) {
        this.pullInterval = pullInterval;
    }

    public int getScanDaysOnStart() {
        return scanDaysOnStart;
    }

    public void setScanDaysOnStart(int scanDaysOnStart) {
        this.scanDaysOnStart = scanDaysOnStart;
    }

    public int getMaxPosts() {
        return maxPosts;
    }

    public void setMaxPosts(int maxPosts) {
        this.maxPosts = maxPosts;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public Set<String> getIgnored() {
        return ignored;
    }

    public void setIgnored(Set<String> ignored) {
        this.ignored = ignored;
    }

    @Override
    public String toString() {
        return "AppProps{" +
                "timeout=" + timeout +
                ", pullInterval=" + pullInterval +
                ", scanDaysOnStart=" + scanDaysOnStart +
                ", maxPosts=" + maxPosts +
                ", keywords=" + keywords +
                ", ignored=" + ignored +
                '}';
    }
}
