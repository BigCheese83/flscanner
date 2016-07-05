package ru.bigcheese.flscanner.config;

import ru.bigcheese.flscanner.support.DateParser;
import ru.bigcheese.flscanner.support.PreFilterAction;
import ru.bigcheese.flscanner.support.ValueExtractor;

/**
 * Содержит все конфигурационные параметры для парсинга сайта.
 * Этот класс является <tt>immutable</tt>, для создания экземпляров используется
 * шаблон проектирования <tt>Builder</tt>.
 *
 * @see     ru.bigcheese.flscanner.config.Selector
 * @see     ru.bigcheese.flscanner.support.ValueExtractor
 * @see     ru.bigcheese.flscanner.support.DateParser
 * @see     ru.bigcheese.flscanner.support.PreFilterAction
 * @author  BigCheese
 */
public class SiteConfig {

    private final String name;
    private final String baseUrl;
    private final String page;
    private final String pageSuffix;
    private final Selector selector;
    private final ValueExtractor descrExtractor;
    private final ValueExtractor timeExtractor;
    private final DateParser dateParser;
    private final PreFilterAction preFilter;

    private SiteConfig(Builder builder) {
        this.name = builder.name;
        this.baseUrl = builder.baseUrl;
        this.page = builder.page;
        this.pageSuffix = builder.pageSuffix;
        this.selector = builder.selector;
        this.timeExtractor = builder.timeExtractor;
        this.descrExtractor = builder.descrExtractor;
        this.dateParser = builder.dateParser;
        this.preFilter = builder.preFilter;
    }

    /**
     * Название сайта
     */
    public String getName() {
        return name;
    }

    /**
     * Базовый URL сайта
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Путь к странице с фрилансом относительно base URL
     */
    public String getPage() {
        return page;
    }

    /**
     * Шаблон для перехода на следующую страницу
     */
    public String getPageSuffix() {
        return pageSuffix;
    }

    /**
     * Информация для парсинга
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * Метод извлечения описания поста
     */
    public ValueExtractor getDescrExtractor() {
        return descrExtractor;
    }

    /**
     * Метод извлечения времени поста
     */
    public ValueExtractor getTimeExtractor() {
        return timeExtractor;
    }

    /**
     * Метод извлечения unix-timestamp времени по его строковому представлению на сайте
     */
    public DateParser getDateParser() {
        return dateParser;
    }

    /**
     * Предварительный фильтр постов (если используется на сайте)
     */
    public PreFilterAction getPreFilter() {
        return preFilter;
    }

    /**
     * Полный путь к начальной странице с фрилансом
     */
    public String getBasePage() {
        return baseUrl + page;
    }

    /**
     * Build instance of class <code>SiteConfig</code>
     */
    public static class Builder {
        private String name;
        private String baseUrl;
        private String page;
        private String pageSuffix;
        private Selector selector;
        private ValueExtractor descrExtractor;
        private ValueExtractor timeExtractor;
        private DateParser dateParser;
        private PreFilterAction preFilter;

        Builder name(String name) {
            this.name = name;
            return this;
        }
        Builder baseUrl(String url) {
            this.baseUrl = url;
            return this;
        }
        Builder page(String page) {
            this.page = page;
            return this;
        }
        Builder pageSuffix(String suffix) {
            this.pageSuffix = suffix;
            return this;
        }
        Builder selector(Selector selector) {
            this.selector = selector;
            return this;
        }
        Builder descrExtractor(ValueExtractor extractor) {
            this.descrExtractor = extractor;
            return this;
        }
        Builder timeExtractor(ValueExtractor extractor) {
            this.timeExtractor = extractor;
            return this;
        }
        Builder dateParser(DateParser parser) {
            this.dateParser = parser;
            return this;
        }
        Builder preFilter(PreFilterAction preFilter) {
            this.preFilter = preFilter;
            return this;
        }

        SiteConfig build() {
            return new SiteConfig(this);
        }
    }
}
