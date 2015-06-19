package org.nick.utils;

import com.ui4j.api.browser.BrowserEngine;
import com.ui4j.api.browser.BrowserFactory;
import com.ui4j.api.browser.Page;
import com.ui4j.api.dom.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    static BrowserEngine browser = BrowserFactory.getWebKit();

    public static void main(String[] args) throws IOException {
        final List<Map<String, String>> mouseMap = getStoreMap("bluetooth", "mouse", "-2.4Ghz", "-keyboard");
        final List<Map<String, String>> chargerMap = getStoreMap("3 port usb car charger");

        final Map<Map<String, String>, Map<String, String>> matches = new HashMap<>();
        for (Map<String, String> mouse : mouseMap) {
            for (Map<String, String> charger : chargerMap) {
                if (mouse.get("storeLink").equals(charger.get("storeLink"))) {
                    matches.put(mouse, charger);
                }
            }
        }

        matches.toString();
    }

    public static List<Map<String, String>> getStoreMap(final String... query) {
        final StringBuilder querySB = new StringBuilder();

        for (String s : query) {
            if (s != null) {
                if (querySB.length() > 0) {
                    querySB.append("+");
                }
                querySB.append(s.replaceAll(" ", "+"));
            }
        }

        final String url = "http://www.aliexpress.com/wholesale?shipCountry=ru&page=1&groupsort=1&isFreeShip=y&SortType=total_tranpro_desc&SearchText=" + querySB.toString();

        BrowserEngine browser = BrowserFactory.getWebKit();

        /*PageConfiguration config = new PageConfiguration(new Interceptor() {

            @Override
            public void beforeLoad(Request request) {
                request.setCookies(Arrays.asList(
                                new HttpCookie("intl_locale", "en_US"),
                                new HttpCookie("Cookie2", "Value2"))
                );
            }

            @Override
            public void afterLoad(Response response) {

            }
        });*/

        Page page = browser.navigate(url);
        final List<Map<String, String>> links = new LinkedList<>();
        for (Element ul : page.getDocument().queryAll("ul")) {
            for (Element listItem : ul.queryAll(".list-item")) {
                final Map<String, String> item = new HashMap<>();

                for (Element a : listItem.queryAll("a")) {
                    final String href = a.getAttribute("href");
                    if (href.toLowerCase().contains("/store") && !href.toLowerCase().contains("/feedback")) {
                        item.put("storeLink", href);
                        item.put("storeTitle", a.getText());
                    }
                    if (href.toLowerCase().contains("item") && href.toLowerCase().endsWith(".html")) {
                        item.put("item", href);
                    }
                    if (href.toLowerCase().contains("item") && href.toLowerCase().endsWith("#thf")) {
                        final String em = a.query("em").getText();
                        final String ordersString = em.substring(em.indexOf('(') + 1, em.indexOf(')'));
                        if (Integer.parseInt(ordersString) > 0) {
                            item.put("orders", ordersString);
                        }
                    }

                    if (item.size() == 4) {
                        links.add(item);
                        break;
                    }
                }
            }
        }

        page.close();

        return links;
    }
}
