package ru.bigcheese.flscanner.model;

import java.util.Objects;

/**
 * Представляет собой пост (задачу) с фриланс сайта.
 * @author  BigCheese
 */
public class Post {

    private final String id;
    private final String title;
    private final String description;
    private final String link;
    private final String time;
    private final long timestamp;

    public Post(String id, String title, String description, String link, String time, long timestamp) {
        if (id == null || id.trim().isEmpty()) {
            id = link;
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.time = time;
        this.timestamp = timestamp;
    }

    /**
     * Идентификатор
     */
    public String getId() {
        return id;
    }

    /**
     * Заголовок поста
     */
    public String getTitle() {
        return title;
    }

    /**
     * Описание (сокращенный текст задания)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Ссылка на пост
     */
    public String getLink() {
        return link;
    }

    /**
     * Время (строковое описание, берется с сайта)
     */
    public String getTime() {
        return time;
    }

    /**
     * Временная отметка (unix-timestamp)
     */
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) &&
                Objects.equals(title, post.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "Post{" +
                "title='" + title + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
