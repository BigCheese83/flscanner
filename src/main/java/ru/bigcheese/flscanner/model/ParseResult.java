package ru.bigcheese.flscanner.model;

import java.util.List;

public class ParseResult {

    private final TaskMetaInfo metaInfo;
    private final List<Post> posts;
    private final long timestamp;

    public ParseResult(TaskMetaInfo metaInfo, List<Post> posts, long timestamp) {
        this.metaInfo = metaInfo;
        this.posts = posts;
        this.timestamp = timestamp;
    }

    public TaskMetaInfo getMetaInfo() {
        return metaInfo;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "metaInfo=" + metaInfo +
                ", posts=" + posts +
                ", timestamp=" + timestamp +
                '}';
    }
}
