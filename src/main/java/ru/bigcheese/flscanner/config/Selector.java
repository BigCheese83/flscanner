package ru.bigcheese.flscanner.config;

/**
 * Инкапсулирует информацию для парсинга постов.
 * Этот класс является <tt>immutable</tt>, для создания экземпляров используется
 * шаблон проектирования <tt>Builder</tt>.
 *
 * @author  BigCheese
 */
public class Selector {

    private final String rows;
    private final String link;
    private final String descr;
    private final String time;

    private Selector(Builder builder) {
        this.rows = builder.rows;
        this.link = builder.link;
        this.descr = builder.descr;
        this.time = builder.time;
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
    public String getDescr() {
        return descr;
    }

    /**
     * Селектор для поиска времени поста
     */
    public String getTime() {
        return time;
    }

    /**
     * Build instance of class <code>Selector</code>
     */
    public static class Builder {
        private String rows;
        private String link;
        private String descr;
        private String time;

        public Builder rows(String rows) {
            this.rows = rows;
            return this;
        }
        public Builder link(String link) {
            this.link = link;
            return this;
        }
        public Builder descr(String descr) {
            this.descr = descr;
            return this;
        }
        public Builder time(String time) {
            this.time = time;
            return this;
        }

        Selector build() {
            return new Selector(this);
        }
    }
}
