package ru.bigcheese.flscanner.config;

import ru.bigcheese.flscanner.model.enums.TimeConverterType;
import ru.bigcheese.flscanner.model.enums.TimeParserType;
import ru.bigcheese.flscanner.model.enums.DescriptionParserType;

import ru.bigcheese.flscanner.model.Selector;

import java.io.Serializable;

public class SiteConfig implements Serializable {

    private String name;
    private String baseUrl;
    private String page;
    private String pageSuffix;
    private Selector selector;
    private DescriptionParserType descriptionParserType;
    private TimeParserType timeParserType;
    private TimeConverterType timeConverterType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSuffix() {
        return pageSuffix;
    }

    public void setPageSuffix(String pageSuffix) {
        this.pageSuffix = pageSuffix;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public DescriptionParserType getDescriptionParserType() {
        return descriptionParserType;
    }

    public void setDescriptionParserType(DescriptionParserType descriptionParserType) {
        this.descriptionParserType = descriptionParserType;
    }

    public TimeParserType getTimeParserType() {
        return timeParserType;
    }

    public void setTimeParserType(TimeParserType timeParserType) {
        this.timeParserType = timeParserType;
    }

    public TimeConverterType getTimeConverterType() {
        return timeConverterType;
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
