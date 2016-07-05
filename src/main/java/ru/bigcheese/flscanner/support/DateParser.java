package ru.bigcheese.flscanner.support;

/**
 * Date parsing.
 * @author  BigCheese
 */
public interface DateParser {
    /**
     * Парсинг времени по ее строковому представлению
     * @param time строковое представление даты и/или времени
     * @return дата в формате unix-timestamp
     */
    long getTimestamp(String time);
}
