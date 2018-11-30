package ru.bigcheese.flscanner.model;

/**
 * Инкапсулирует информацию для парсинга постов.
 *
 * @author  BigCheese
 */
public class Selector {

    private String rows;
    private String link;
    private String description;
    private String time;

    public Selector() {
    }

    public Selector(String rows, String link, String description, String time) {
        this.rows = rows;
        this.link = link;
        this.description = description;
        this.time = time;
    }

    /**
     * Селектор для поиска постов
     */
    public String getRows() {
        return rows;
    }

    /**
     * Селектор для поиска ссылки
     */
    public String getLink() {
        return link;
    }

    /**
     * Селектор для поиска описания
     */
    public String getDescription() {
        return description;
    }

    /**
     * Селектор для поиска времени поста
     */
    public String getTime() {
        return time;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Selector{" +
                "rows='" + rows + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
