package ru.bigcheese.flscanner.support;

import ru.bigcheese.flscanner.model.enums.DescriptionParserType;
import ru.bigcheese.flscanner.model.enums.TimeParserType;

import java.util.EnumMap;

import static ru.bigcheese.flscanner.model.enums.DescriptionParserType.ATTRIBUTE_TITLE;
import static ru.bigcheese.flscanner.model.enums.TimeParserType.SPAN_TIME_PARENT;

public class ValueParserSupport {

    private static final EnumMap<DescriptionParserType, DescriptionParser> descriptionMap =
            new EnumMap<>(DescriptionParserType.class);
    private static final EnumMap<TimeParserType, TimeParser> timeMap =
            new EnumMap<>(TimeParserType.class);

    public static DescriptionParser getParser(DescriptionParserType type) {
        return descriptionMap.get(type);
    }

    public static TimeParser getParser(TimeParserType type) {
        return timeMap.get(type);
    }

    static {
        descriptionMap.put(ATTRIBUTE_TITLE,
                element -> (element != null ) ? element.attr("title").trim() : "");

        timeMap.put(SPAN_TIME_PARENT,
                element -> {
                    if (element != null) {
                        String text = element.parent().text();
                        int end = text.indexOf(" от ");
                        return text.substring(0, end != -1 ? end : text.length()).trim();
                    }
                    return "";
                });
    }
}
