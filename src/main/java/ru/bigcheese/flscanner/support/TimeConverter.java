package ru.bigcheese.flscanner.support;

@FunctionalInterface
public interface TimeConverter {

    long getTimestamp(String time);
}
