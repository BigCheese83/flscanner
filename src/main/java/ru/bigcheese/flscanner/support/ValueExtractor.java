package ru.bigcheese.flscanner.support;

import org.jsoup.nodes.Element;

/**
 * Extract value.
 * @author  BigCheese
 */
public interface ValueExtractor {
    /**
     * Извлекает строковое значение из html-элемента
     * @param element html-элемент
     * @return строковое значение
     */
    String getValue(Element element);
}
