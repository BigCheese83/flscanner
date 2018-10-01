package ru.bigcheese.flscanner.support;

import org.jsoup.nodes.Element;

@FunctionalInterface
public interface DescriptionParser {

    String getDescription(Element element);
}
