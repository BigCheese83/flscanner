package ru.bigcheese.flscanner.support;

import ru.bigcheese.flscanner.model.enums.TimeConverterType;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;

import static ru.bigcheese.flscanner.model.enums.TimeConverterType.PROGRAMMERSFORUM_RU;
import static ru.bigcheese.flscanner.util.DateUtils.DF_RU;
import static ru.bigcheese.flscanner.util.DateUtils.DTF_RU;

public class ValueConverterSupport {

    private static final EnumMap<TimeConverterType, TimeConverter> timeMap =
            new EnumMap<>(TimeConverterType.class);

    public static TimeConverter getConverter(TimeConverterType type) {
        return timeMap.get(type);
    }

    static {
        timeMap.put(PROGRAMMERSFORUM_RU,
                time -> {
                    if (time != null && !time.isEmpty()) {
                        try {
                            String normalized = time;
                            if (time.startsWith("Сегодня")) {
                                normalized = DF_RU.format(LocalDate.now()) + time.substring("Сегодня".length());
                            } else if (time.startsWith("Вчера")){
                                normalized = DF_RU.format(LocalDate.now().minus(1, ChronoUnit.DAYS)) + time.substring("Вчера".length());
                            }
                            return LocalDateTime.parse(normalized, DTF_RU)
                                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        } catch (DateTimeException e) {
                            e.printStackTrace();
                        }
                    }
                    return 0;
                });
    }
}