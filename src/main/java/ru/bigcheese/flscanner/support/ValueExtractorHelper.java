package ru.bigcheese.flscanner.support;

import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static implementations of interface <code>ValueExtractor</code>.
 *
 * @see     ru.bigcheese.flscanner.support.ValueExtractor
 * @author  BigCheese
 */
@Deprecated
public class ValueExtractorHelper {

    public static final ValueExtractor DESCR_ATTR_TITLE = new ValueExtractor() {
        @Override
        public String getValue(Element element) {
            return (element != null) ? element.attr("title").trim() : "";
        }
    };

    public static final ValueExtractor DESCR_FL_RU = new ValueExtractor() {
        @Override
        public String getValue(Element element) {
            if (element != null) {
                final String search = "<div class=\"b-post__txt \">";
                final String html = element.html();
                int pos = html.indexOf(search);
                if (pos != -1) {
                    pos += search.length();
                    return html.substring(pos, html.indexOf('<', pos)).trim();
                }
            }
            return "";
        }
    };

    public static final ValueExtractor TIME_STR_COMMON = new ValueExtractor() {
        @Override
        public String getValue(Element element) {
            if (element != null) {
                String text = element.text().trim();
                Matcher matcher = Pattern.compile("\\D*(\\d+.*)").matcher(text);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
            return "";
        }
    };

    public static final ValueExtractor TIME_STR_SPAN_TIME_PARENT = new ValueExtractor() {
        @Override
        public String getValue(Element element) {
            if (element != null) {
                String text = element.parent().text();
                int end = text.indexOf(" от ");
                return text.substring(0, end != -1 ? end : text.length()).trim();
            }
            return "";
        }
    };

    public static final ValueExtractor TIME_STR_FL_RU = new ValueExtractor() {
        @Override
        public String getValue(Element element) {
            if (element != null) {
                final String search = "&nbsp;&nbsp;";
                final String html = element.html();
                int begin = html.indexOf(search);
                int end = html.indexOf(search, begin + search.length());
                if (begin != -1 && end != -1) {
                    return html.substring(begin + search.length(), end).trim();
                }
            }
            return "";
        }
    };
}
