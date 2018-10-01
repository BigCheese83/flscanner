package ru.bigcheese.flscanner.config;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import ru.bigcheese.flscanner.model.Post;
import ru.bigcheese.flscanner.support.DateParserHelper;
import ru.bigcheese.flscanner.support.PreFilterActionHelper;
import ru.bigcheese.flscanner.support.ValueExtractorHelper;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by BigCheese on 03.06.16.
 */
public class SettingsOld {

    public static final Map<String, List<Post>> ALL_POSTS = new ConcurrentHashMap<>();
    public static final Map<String, Set<String>> IGNORED_POSTS = new ConcurrentHashMap<>();
    public static final Map<String, Long> TIMESTAMPS = new ConcurrentHashMap<>();
    private static final SiteConfigOld[] SITE_CONFIGS;
    private static final String JSON_NAME = "settings-old.json";
    private static final long DEFAULT_TIMESTAMP = getTime20DaysAgo();

    static {
        SITE_CONFIGS = initSiteConfigs();
        for (SiteConfigOld config : SITE_CONFIGS) {
            ALL_POSTS.put(config.getName(), new ArrayList<Post>());
            IGNORED_POSTS.put(config.getName(), new HashSet<String>());
            TIMESTAMPS.put(config.getName(), DEFAULT_TIMESTAMP);
        }
        readSettings();
    }

    @SuppressWarnings("unchecked")
    public static void saveSettings() {
        /*
        SysProps props = SysProps.getInstance();
        JSONObject obj = new JSONObject();
        obj.put("timeout", props.getTimeout());
        obj.put("pullInterval", props.getPullInterval());
        obj.put("maxPosts", props.getMaxPosts());
        JSONArray keywords = new JSONArray();
        Collections.addAll(keywords, props.getKeywords());
        obj.put("keywords", keywords);
        JSONObject timestamps = new JSONObject();
        for (Map.Entry<String, Long> entry : TIMESTAMPS.entrySet()) {
            timestamps.put(entry.getKey(), entry.getValue());
        }
        obj.put("timestamps", timestamps);
        JSONArray ignored = new JSONArray();
        ignored.addAll(props.getIgnored());
        obj.put("ignored", ignored);

        // write json to file
        try (FileWriter fileWriter = new FileWriter(JSON_NAME)) {
            fileWriter.write(obj.toJSONString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    @SuppressWarnings("unchecked")
    public static void readSettings() {
        /*
        SysProps props = SysProps.getInstance();
        try (FileReader reader = new FileReader(JSON_NAME)) {
            JSONObject obj = (JSONObject) new JSONParser().parse(reader);
            Long timeout = (Long) obj.get("timeout");
            if (timeout != null) {
                props.setTimeout(timeout.intValue());
            }
            Long pullInterval = (Long) obj.get("pullInterval");
            if (pullInterval != null) {
                props.setPullInterval(pullInterval.intValue());
            }
            Long maxPosts = (Long) obj.get("maxPosts");
            if (maxPosts != null) {
                props.setMaxPosts(maxPosts.intValue());




            }
            JSONArray keywords = (JSONArray) obj.get("keywords");
            if (keywords != null) {
                String[] words = new String[keywords.size()];
                System.arraycopy(keywords.toArray(), 0, words, 0, keywords.size());
                props.setKeywords(words);
            }
            JSONObject timestamps = (JSONObject) obj.get("timestamps");
            if (timestamps != null) {
                for (Object entry : timestamps.entrySet()) {
                    Map.Entry<String, Long> e = (Map.Entry<String, Long>) entry;
                    TIMESTAMPS.put(e.getKey(), e.getValue());
                }
            }
            JSONArray ignored = (JSONArray) obj.get("ignored");
            if (ignored != null) {
                props.setIgnored(new HashSet<String>(ignored));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        */
    }

    public static List<SiteConfigOld> getConfigs() {
        Set<String> ignored = SysProps.getInstance().getIgnored();
        List<SiteConfigOld> configs = new ArrayList<>();
        for (SiteConfigOld sc : SITE_CONFIGS) {
            if (!ignored.contains(sc.getName())) {
                configs.add(sc);
            }
        }
        return configs;
    }

    public static SiteConfigOld[] getAllConfigs() {
        return Arrays.copyOf(SITE_CONFIGS, SITE_CONFIGS.length);
    }

    private static SiteConfigOld[] initSiteConfigs() {

        return new SiteConfigOld[] {

                new SiteConfigOld.Builder()
                        .name("programmersforum.ru")
                        .baseUrl("http://programmersforum.ru/")
                        .page("forumdisplay.php?f=29")
                        .pageSuffix("&order=desc&page={0}")
                        .selector(new Selector.Builder()
                                .rows("#threadslist #threadbits_forum_29 tr")
                                .link("a[id^='thread_title_']")
                                .descr("td[id^='td_threadtitle_']")
                                .time("span.time")
                                .build())
                        .descrExtractor(ValueExtractorHelper.DESCR_ATTR_TITLE)
                        .timeExtractor(ValueExtractorHelper.TIME_STR_SPAN_TIME_PARENT)
                        .dateParser(DateParserHelper.CYBERFORUM_PARSER)
                        .build(),

                new SiteConfigOld.Builder()
                        .name("www.fl.ru")
                        .baseUrl("https://www.fl.ru")
                        .page("/projects")
                        .pageSuffix("/?page={0}&kind=5")
                        .selector(new Selector.Builder()
                                .rows("#projects-list div[id^='project-item']")
                                .link("a[id^='prj_name_']")
                                .descr("script:eq(2)")
                                .time("div.b-post__foot script")
                                .build())
                        .descrExtractor(ValueExtractorHelper.DESCR_FL_RU)
                        .timeExtractor(ValueExtractorHelper.TIME_STR_FL_RU)
                        .dateParser(DateParserHelper.FL_PARSER)
                        .preFilter(PreFilterActionHelper.FL_PRE_FILTER)
                        .build(),

                new SiteConfigOld.Builder()
                        .name("freelansim.ru")
                        .baseUrl("http://freelansim.ru")
                        .page("/tasks")
                        .pageSuffix("?page={0}")
                        .selector(new Selector.Builder()
                                .rows("#tasks_list header.task__header")
                                .link("div.task__title a")
                                .descr("div.task__title")
                                .time("div.task__params span.params__published-at")
                                .build())
                        .descrExtractor(ValueExtractorHelper.DESCR_ATTR_TITLE)
                        .timeExtractor(ValueExtractorHelper.TIME_STR_COMMON)
                        .dateParser(DateParserHelper.FL_PARSER)
                        .build()
        };
    }

    private static long getTime20DaysAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -20);
        return cal.getTimeInMillis();
    }
}
