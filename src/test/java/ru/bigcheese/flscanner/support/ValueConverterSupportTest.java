package ru.bigcheese.flscanner.support;

import org.junit.Test;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static ru.bigcheese.flscanner.model.enums.TimeConverterType.PROGRAMMERSFORUM_RU;
import static ru.bigcheese.flscanner.support.ValueConverterSupport.getConverter;

public class ValueConverterSupportTest {

    @Test
    public void testProgrammersforumTimeConverter() {
        TimeConverter converter = getConverter(PROGRAMMERSFORUM_RU);
        assertEquals(getTimestamp(1, 56), converter.getTimestamp("Сегодня 01:56"));
        assertEquals(getTimestamp(14, 0) - TimeUnit.DAYS.toMillis(1), converter.getTimestamp("Вчера 14:00"));
        assertEquals(getTimestamp(2018, 8, 10, 1, 42), converter.getTimestamp("10.08.2018 01:42"));
    }

    private long getTimestamp(int hour, int minute) {
        return getTimestamp(null, null, null, hour, minute);
    }

    private long getTimestamp(Integer year, Integer month, Integer day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        if (year != null) {
            c.set(Calendar.YEAR, year);
        }
        if (month != null) {
            c.set(Calendar.MONTH, month-1);
        }
        if (day != null) {
            c.set(Calendar.DAY_OF_MONTH, day);
        }
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime().getTime();
    }
}