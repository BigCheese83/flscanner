package ru.bigcheese.flscanner.support;

import org.jsoup.nodes.Element;

@FunctionalInterface
public interface TimeParser {

    String getTime(Element element);
}
