package ru.bigcheese.flscanner.support;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static implementations of interface <code>DateParser</code>.
 *
 * @see     ru.bigcheese.flscanner.support.DateParser
 * @author  BigCheese
 */
public class DateParserHelper {

    /**
     * Parsing date from sites cyberforum.ru, programmersforum.ru
     */
    public static final DateParser CYBERFORUM_PARSER = new CyberforumDateParser();
    /**
     * Parsing date from site fl.ru
     */
    public static final DateParser FL_PARSER = new FLDateParser();


    private static class CyberforumDateParser implements DateParser {

        private final SafeSimpleDateFormat df1 = new SafeSimpleDateFormat("dd.MM.yyyy");
        private final SafeSimpleDateFormat df2 = new SafeSimpleDateFormat("dd.MM.yyyy HH:mm");

        @Override
        public long getTimestamp(String time) {
            if (time != null && time.length() > 0) {
                try {
                    String modified = time;
                    if (time.startsWith("Сегодня")) {
                        modified = df1.get().format(new Date()) + time.substring("Сегодня".length());
                    } else if (time.startsWith("Вчера")){
                        modified = df1.get().format(getDayAgo()) + time.substring("Вчера".length());
                    }
                    Date date = df2.get().parse(modified);
                    return date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }

        private Date getDayAgo() {
            return new Date(System.currentTimeMillis() - 1000*60*60*24);
        }
    }

    private static class FLDateParser implements DateParser {

        private final SafeSimpleDateFormat df = new SafeSimpleDateFormat("dd.MM.yyyy HH:mm");
        private static final Map<String, String> monthMap = getMonthMap();

        @Override
        public long getTimestamp(String time) {
            if (time != null && time.length() > 0) {
                if (time.startsWith("Только что")) {
                    return System.currentTimeMillis();
                } else if (time.endsWith("назад")) {
                    Pattern pattern = Pattern.compile("(\\d+\\s(минут\\S*|час\\S*|день|дней|дня|месяц\\S*))+");
                    Matcher matcher = pattern.matcher(time);
                    Calendar cal = Calendar.getInstance();
                    while (matcher.find()) {
                        String[] group = matcher.group().split(" ");
                        int number = Integer.parseInt(group[0]);
                        if (group[1].startsWith("минут")) {
                            cal.add(Calendar.MINUTE, -number);
                        } else if (group[1].startsWith("час")) {
                            cal.add(Calendar.HOUR_OF_DAY, -number);
                        } else if (group[1].startsWith("день") || group[1].startsWith("дней")) {
                            cal.add(Calendar.DAY_OF_MONTH, -number);
                        } else if (group[1].startsWith("месяц")) {
                            cal.add(Calendar.MONTH, -number);
                        }
                    }
                    return cal.getTime().getTime();
                } else if (time.matches("\\d+\\s\\S+,\\s\\d{2}:\\d{2}")) {
                    String strDate = joinDate(time.split("[ ,]+"));
                    try {
                        Date date = df.get().parse(strDate);
                        return date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            return 0;
        }

        private String joinDate(String[] group) {
            if (group.length != 3) return "";
            String day = (group[0].length() == 1) ? "0" + group[0] : group[0];
            String month = monthMap.get(group[1]);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            return day + "." + month + "." + year + " " + group[2];
        }

        private static Map<String, String> getMonthMap() {
            Map<String, String> map = new HashMap<>();
            map.put("января", "01");
            map.put("февраля", "02");
            map.put("марта", "03");
            map.put("апреля", "04");
            map.put("мая", "05");
            map.put("июня", "06");
            map.put("июля", "07");
            map.put("августа", "08");
            map.put("сентября", "09");
            map.put("октября", "10");
            map.put("ноября", "11");
            map.put("декабря", "12");
            return Collections.unmodifiableMap(map);
        }
    }
}
