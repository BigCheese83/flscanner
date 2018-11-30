package ru.bigcheese.flscanner.support;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.bigcheese.flscanner.config.SysProps;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BigCheese on 01.07.16.
 */
@Deprecated
public class PreFilterActionHelper {

    public static final PreFilterAction FL_PRE_FILTER = new FLPreFilterAction();

    private static class FLPreFilterAction implements PreFilterAction {

        //TODO пока фильтр не работает, нужно разбираться
        @Override
        public void applyFilter() throws IOException {
            String[] keywords = SysProps.getInstance().getKeywords();
            if (keywords.length == 0) {
                return;
            }
            Map<String, String> map = getRequestParams();
            if (map.get("token") != null & keywords.length > 0) {
                String params =
                        "action=postfilter&kind=5&pf_category=&pf_subcategory=" +
                        "&comboe_columns%5B1%5D=0&comboe_columns%5B0%5D=0&comboe_column_id=0&comboe_db_id=0" +
                        "&comboe=%D0%92%D1%81%D0%B5+%D1%81%D0%BF%D0%B5%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D0%B8" +
                        "&location_columns%5B1%5D=0&location_columns%5B0%5D=0&location_column_id=0&location_db_id=0" +
                        "&location=%D0%92%D1%81%D0%B5+%D1%81%D1%82%D1%80%D0%B0%D0%BD%D1%8B" +
                        "&pf_cost_from=&currency_text_columns%5B1%5D=0&currency_text_columns%5B0%5D=2&currency_text_column_id=0" +
                        "&currency_text_db_id=2&pf_currency=2&currency_text=%D0%A0%D1%83%D0%B1" +
                        "&pf_keywords=" + keywords[0] +
                        "&u_token_key=" + map.get("token");
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://www.fl.ru/projects/").openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Cookie", map.get("cookies"));
                connection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(params);
                wr.flush();
                wr.close();

                /*
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());
                */
            }
        }

        private Map<String, String> getRequestParams() throws IOException {
            Connection.Response response = Jsoup.connect("https://www.fl.ru/projects/")
                    .timeout(SysProps.getInstance().getTimeout())
                    .method(Connection.Method.GET)
                    .execute();
            Document doc = response.parse();
            Map<String, String> params = new HashMap<>();
            params.put("token", getToken(doc.select("div.b-layout__page script")));
            params.put("cookies", mapToString(response.cookies()));

            return params;
        }

        private String getToken(Elements elements) {
            Pattern pattern = Pattern.compile("(?<=var U_TOKEN_KEY = \")\\w+(?=\";)");
            for (Element e : elements) {
                Matcher matcher = pattern.matcher(e.html());
                if (matcher.find()) {
                    return matcher.group();
                }
            }
            return null;
        }

        private String mapToString(Map<String, String> map) {
            StringBuilder sb = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                sb.append(entry.getKey()).append('=').append(entry.getValue());
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }
            return sb.toString();
        }
    }
}
