package ru.bigcheese.flscanner.config;

import ru.bigcheese.flscanner.model.Selector;
import ru.bigcheese.flscanner.model.enums.DescriptionParserType;
import ru.bigcheese.flscanner.model.enums.TimeConverterType;
import ru.bigcheese.flscanner.model.enums.TimeParserType;

/**
 * Содержит конфигурационные параметры для парсинга сайта.
 *
 * @see     ru.bigcheese.flscanner.model.Selector
 * @see     ru.bigcheese.flscanner.model.enums.DescriptionParserType
 * @see     ru.bigcheese.flscanner.model.enums.TimeParserType
 * @see     ru.bigcheese.flscanner.model.enums.TimeConverterType
 * @author  BigCheese
 */
public class SiteConfig {

    private String name;
    private String baseUrl;
    private String page;
    private String pageSuffix;
    private Selector selector;
    private DescriptionParserType descriptionParserType;
    private TimeParserType timeParserType;
    private TimeConverterType timeConverterType;

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
     * Тип метода извлечения описания поста
     */
    public DescriptionParserType getDescriptionParserType() {
        return descriptionParserType;
    }

    /**
     * Тип метода извлечения времени поста
     */
    public TimeParserType getTimeParserType() {
        return timeParserType;
    }

    /**
     * Тип метода извлечения unix-timestamp времени по его строковому представлению на сайте
     */
    public TimeConverterType getTimeConverterType() {
        return timeConverterType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setPageSuffix(String pageSuffix) {
        this.pageSuffix = pageSuffix;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public void setDescriptionParserType(DescriptionParserType descriptionParserType) {
        this.descriptionParserType = descriptionParserType;
    }

    public void setTimeParserType(TimeParserType timeParserType) {
        this.timeParserType = timeParserType;
    }

    public void setTimeConverterType(TimeConverterType timeConverterType) {
        this.timeConverterType = timeConverterType;
    }

    @Override
    public String toString() {
        return "SiteConfig{" +
                "name='" + name + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", page='" + page + '\'' +
                ", pageSuffix='" + pageSuffix + '\'' +
                ", selector=" + selector +
                ", descriptionParserType=" + descriptionParserType +
                ", timeParserType=" + timeParserType +
                ", timeConverterType=" + timeConverterType +
                '}';
    }
}
